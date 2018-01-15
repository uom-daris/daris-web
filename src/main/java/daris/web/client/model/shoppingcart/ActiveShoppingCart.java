package daris.web.client.model.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.util.ListUtil;
import arc.mf.event.Filter;
import arc.mf.event.Subscriber;
import arc.mf.event.SystemEvent;
import arc.mf.event.SystemEventChannel;
import arc.mf.model.shopping.events.ShoppingCartEvent;
import arc.mf.model.shopping.events.ShoppingEvent;
import arc.mf.object.ObjectResolveHandler;
import daris.web.client.model.shoppingcart.ShoppingCart.DeliveryMethod;
import daris.web.client.model.shoppingcart.messages.ShoppingCartCreate;
import daris.web.client.model.shoppingcart.messages.ShoppingCartExists;
import daris.web.client.model.shoppingcart.messages.ShoppingCartOutputRetrieve;

public class ActiveShoppingCart {

    public static interface Listener {
        void activateShoppingCartUpdated();
    }

    private static final List<Listener> _ls = new ArrayList<Listener>();

    private static final List<Filter> _filters = ListUtil.list(new Filter(ShoppingCartEvent.SYSTEM_EVENT_NAME),
            new Filter(ShoppingEvent.SYSTEM_EVENT_NAME));

    private static final Subscriber _subscriber = new Subscriber() {

        @Override
        public List<Filter> systemEventFilters() {
            return _filters;
        }

        @Override
        public void process(SystemEvent e) {
            if (e instanceof ShoppingEvent) {
                ShoppingEvent se = (ShoppingEvent) e;
                switch (se.action()) {
                case DESTROY:
                    if (_ac != null && _ac.id() == se.cartId()) {
                        _ac = null;
                        notifyOfActiveCartChange();
                    }
                    break;
                default:
                    break;
                }
            } else if (e instanceof ShoppingCartEvent) {
                final ShoppingCartEvent sce = (ShoppingCartEvent) e;
                switch (sce.action()) {
                case MODIFY:
                    if (_ac != null && _ac.id() == sce.cartId()) {
                        _ac.reset();
                        _ac.resolve(cart -> {
                            if (cart.status() != ShoppingCart.Status.EDITABLE) {
                                _ac = null;
                            }
                            notifyOfActiveCartChange();
                        });
                    }
                    new ShoppingCartRef(sce.cartId()).resolve(cart -> {
                        if (cart.status() == ShoppingCart.Status.DATA_READY
                                && cart.deliveryMethod() == DeliveryMethod.DOWNLOAD) {
                            new ShoppingCartOutputRetrieve(cart.id()).send();
                        }
                    });
                    break;
                default:
                    break;
                }
            }
        }
    };

    private static ShoppingCartRef _ac = null;

    private static boolean _subscribed = false;

    public static void resolve(boolean refresh, ObjectResolveHandler<ShoppingCartRef> rh) {

        if (_ac != null) {
            if (!refresh) {
                if (rh != null) {
                    rh.resolved(_ac);
                }
                return;
            }
            _ac.reset();
            new ShoppingCartExists(_ac).send(exists -> {
                if (exists) {
                    _ac.resolve(cart -> {
                        if (cart.status() != ShoppingCart.Status.EDITABLE) {
                            // No longer editable, get a new one
                            _ac = null;
                            resolve(false, rh);
                        } else {
                            if (rh != null) {
                                rh.resolved(_ac);
                            }
                        }
                    });
                } else {
                    // No longer exists, get a new one
                    _ac = null;
                    resolve(false, rh);
                }
            });
        } else {
            new ShoppingCartCollectionRef(ListUtil.list(ShoppingCart.Status.EDITABLE)).setPagingSize(-1)
                    .resolve(carts -> {
                        if (carts == null || carts.isEmpty()) {
                            // No editable cart found, create a new one
                            new ShoppingCartCreate().send(cart -> {
                                _ac = cart;
                                if (rh != null) {
                                    rh.resolved(_ac);
                                }
                            });
                        } else {
                            // set the first editable cart as active cart.
                            _ac = carts.get(0);
                            if (rh != null) {
                                rh.resolved(_ac);
                            }
                        }
                    });
        }
    }

    public static void resolve(ObjectResolveHandler<ShoppingCart> rh, boolean refresh) {
        resolve(refresh, cart -> {
            cart.resolve(rh);
        });
    }

    public static void addListener(Listener l) {
        _ls.add(l);
    }

    public static void removeListener(Listener l) {
        _ls.remove(l);
    }

    public static void notifyOfActiveCartChange() {
        for (Listener l : _ls) {
            l.activateShoppingCartUpdated();
        }
    }

    public static void subscribe() {
        if (!_subscribed) {
            SystemEventChannel.add(_subscriber);
            _subscribed = true;
        }
    }

    public static void unsubscribe() {
        if (_subscribed) {
            SystemEventChannel.remove(_subscriber);
            _subscribed = false;
        }
    }

    public static void reset() {
        if (_ac != null) {
            _ac.reset();
        }
    }

}

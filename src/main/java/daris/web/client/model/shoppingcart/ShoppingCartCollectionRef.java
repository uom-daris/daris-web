package daris.web.client.model.shoppingcart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import arc.mf.client.util.ListUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.event.Filter;
import arc.mf.event.Subscriber;
import arc.mf.event.SystemEvent;
import arc.mf.event.SystemEventChannel;
import arc.mf.model.shopping.events.ShoppingCartEvent;
import arc.mf.model.shopping.events.ShoppingEvent;
import arc.mf.object.OrderedCollectionRef;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;

public class ShoppingCartCollectionRef extends OrderedCollectionRef<ShoppingCartRef> implements Subscriber {

    public static interface Listener {
        void shoppingCartCollectionUpdated();
    }

    public static final int DEFAULT_PAGE_SIZE = 100;

    private int _pageSize = DEFAULT_PAGE_SIZE;

    private Set<ShoppingCart.Status> _status;

    private List<Listener> _ls;

    private boolean _subscribed = false;

    public ShoppingCartCollectionRef(Collection<Status> status) {
        _status = new LinkedHashSet<ShoppingCart.Status>();
        if (status != null && !status.isEmpty()) {
            _status = new LinkedHashSet<ShoppingCart.Status>(status);
        }
        _pageSize = DEFAULT_PAGE_SIZE;
        setCountMembers(true);
    }

    public ShoppingCartCollectionRef() {
        this(null);
    }

    @Override
    public final int defaultPagingSize() {
        return _pageSize;
    }

    public final ShoppingCartCollectionRef setPagingSize(int pageSize) {
        _pageSize = pageSize;
        return this;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {
        if (_status != null) {
            for (ShoppingCart.Status status : _status) {
                w.add("status", status.value());
            }
        }
        w.add("idx", start + 1);
        if (_pageSize == -1) {
            w.add("size", "infinity");
        } else {
            w.add("size", size);
        }
    }

    @Override
    protected String resolveServiceName() {
        return "daris.shoppingcart.list";
    }

    @Override
    protected ShoppingCartRef instantiate(XmlElement ce) throws Throwable {
        return new ShoppingCartRef(ce);
    }

    @Override
    protected String referentTypeName() {
        return ShoppingCart.TYPE_NAME;
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "cart" };
    }

    @Override
    public List<Filter> systemEventFilters() {
        return ListUtil.list(new Filter(ShoppingCartEvent.SYSTEM_EVENT_NAME),
                new Filter(ShoppingEvent.SYSTEM_EVENT_NAME));
    }

    @Override
    public void process(SystemEvent se) {
        reset();
        notifyOfCollectionChange();
    }

    public void notifyOfCollectionChange() {
        if (_ls != null) {
            for (Listener l : _ls) {
                l.shoppingCartCollectionUpdated();
            }
        }
    }

    public void addListener(Listener l) {
        if (_ls == null) {
            _ls = new ArrayList<Listener>();
        }
        _ls.add(l);
    }

    public void removeListener(Listener l) {
        if (_ls != null) {
            _ls.remove(l);
        }
    }

    public ShoppingCartCollectionRef subscribe() {
        if (!_subscribed) {
            SystemEventChannel.add(this);
            _subscribed = true;
        }
        return this;
    }

    public ShoppingCartCollectionRef unsubscribe() {
        if (_subscribed) {
            SystemEventChannel.remove(this);
            _subscribed = false;
        }
        return this;
    }

    public static final ShoppingCartCollectionRef ALL_CARTS = new ShoppingCartCollectionRef().setPagingSize(-1)
            .subscribe();

}

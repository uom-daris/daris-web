package daris.web.client.model.shoppingcart.messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;
import daris.web.client.model.shoppingcart.ShoppingCartRef;

public class ShoppingCartDestroy extends ObjectMessage<Null> {

    private List<Long> _cartIds;
    private Set<Status> _statuses;

    public ShoppingCartDestroy(ShoppingCart... carts) {
        _cartIds = new ArrayList<Long>(carts.length);
        for (ShoppingCart cart : carts) {
            _cartIds.add(cart.id());
        }
    }

    public ShoppingCartDestroy(ShoppingCartRef... carts) {
        _cartIds = new ArrayList<Long>(carts.length);
        for (ShoppingCartRef cart : carts) {
            _cartIds.add(cart.id());
        }
    }

    public ShoppingCartDestroy(Status... statuses) {
        _statuses = new HashSet<Status>();
        for (Status status : statuses) {
            _statuses.add(status);
        }
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_statuses != null) {
            for (Status status : _statuses) {
                w.add("status", status.value());
            }
        }
        if (_cartIds != null) {
            for (Long cartId : _cartIds) {
                w.add("sid", cartId);
            }
        }
    }

    @Override
    protected String messageServiceName() {
        return "daris.shoppingcart.destroy";
    }

    @Override
    protected Null instantiate(XmlElement xe) throws Throwable {
        return new Null();
    }

    @Override
    protected String objectTypeName() {
        return ShoppingCart.TYPE_NAME;
    }

    @Override
    protected String idToString() {
        return null;
    }

}

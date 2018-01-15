package daris.web.client.model.shoppingcart.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCartRef;

public class ShoppingCartContentClear extends ObjectMessage<Null> {

    private long _cartId;

    public ShoppingCartContentClear(long cartId) {
        _cartId = cartId;
    }

    public ShoppingCartContentClear(ShoppingCartRef cart) {
        this(cart.id());
    }

    public ShoppingCartContentClear(ShoppingCart cart) {
        this(cart.id());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("sid", _cartId);
    }

    @Override
    protected String messageServiceName() {
        return "daris.shoppingcart.content.clear";
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
        return Long.toString(_cartId);
    }

}

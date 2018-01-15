package daris.web.client.model.shoppingcart.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.shoppingcart.ShoppingCart;

public class ShoppingCartModify extends ObjectMessage<Null> {

    private ShoppingCart _cart;

    public ShoppingCartModify(ShoppingCart cart) {

        _cart = cart;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {

        _cart.saveUpdateArgs(w);
    }

    @Override
    protected String messageServiceName() {

        return "shopping.cart.modify";
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

        return Long.toString(_cart.id());
    }

}

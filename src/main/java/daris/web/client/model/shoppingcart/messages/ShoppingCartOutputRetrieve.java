package daris.web.client.model.shoppingcart.messages;

import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;

import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCartRef;
import daris.web.client.util.DownloadUtil;

public class ShoppingCartOutputRetrieve extends ObjectMessage<Null> {

    private long _cartId;

    public ShoppingCartOutputRetrieve(long cartId) {
        _cartId = cartId;
    }
    
    public ShoppingCartOutputRetrieve(ShoppingCartRef cart){
        this(cart.id());
    }
    
    public ShoppingCartOutputRetrieve(ShoppingCart cart){
        this(cart.id());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("sid", _cartId);
    }

    @Override
    protected String messageServiceName() {
        return "shopping.cart.output.retrieve";
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

    @Override
    protected void process(Null o, List<Output> outputs) {
        if (outputs != null && !outputs.isEmpty()) {
            new ShoppingCartRef(_cartId).resolve(cart -> {
                for (Output output : outputs) {
                    DownloadUtil.download(output, generateArchiveFileName(cart));
                }
            });
        }
    }
    
    @Override
    protected int numberOfOutputs() {
        return 1;
    }

    public static String generateArchiveFileName(ShoppingCart cart) {
        String ext = cart.packaging().type().extension();
        String filename = "DARIS_SC_" + cart.id();
        if (cart.name() != null) {
            filename += "_" + cart.name();
        }
        filename += "_" + DateTimeFormat.getFormat("yyyy.MM.dd_HHmmss").format(cart.changed());
        filename += (ext != null ? ("." + ext) : "");
        return filename;
    }
}

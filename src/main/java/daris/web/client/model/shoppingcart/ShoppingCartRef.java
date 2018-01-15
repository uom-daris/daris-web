package daris.web.client.model.shoppingcart;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;
import arc.mf.session.Session;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;

public class ShoppingCartRef extends ObjectRef<ShoppingCart> {

    private long _id;
    private ShoppingCart.Status _status;
    private String _name;
    private String _description;
    private String _template;
    private long _itemCount = 0;
    private long _itemSize = 0;

    public ShoppingCartRef(long id) {
        _id = id;
    }

    public ShoppingCartRef(ShoppingCart cart) {
        super(cart);
        _id = cart.id();
        _status = cart.status();
        _name = cart.name();
        _description = cart.description();
        _template = cart.template();
        _itemCount = cart.numberOfContentItems();
        _itemSize = cart.sizeOfContentItems();
    }

    public ShoppingCartRef(XmlElement ce) {
        try {
            _id = ce.longValue("@id");
        } catch (Throwable e1) {
            Session.displayError("Parsing shopping cart id", e1);
        }
        _status = Status.fromString(ce.value("@status"));
        _name = ce.value("@name");
        _description = ce.value("@description");
        _template = ce.value("@template");
        try {
            _itemCount = ce.longValue("@count", 0);
            _itemSize = ce.longValue("@size", 0);
        } catch (Throwable e) {
            Session.displayError("Parsing shopping cart information...", e);
        }
    }

    public String name() {
        return _name;
    }

    public String description() {
        return _description;
    }

    public String template() {
        return _template;
    }

    public long itemCount() {
        return _itemCount;
    }

    public long itemSize() {
        return _itemSize;
    }

    public ShoppingCart.Status status() {
        return _status;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("sid", _id);
    }

    @Override
    protected String resolveServiceName() {
        return "daris.shoppingcart.describe";
    }

    @Override
    protected ShoppingCart instantiate(XmlElement xe) throws Throwable {
        XmlElement ce = xe.element("cart");
        if (ce != null) {
            ShoppingCart cart = new ShoppingCart(ce);
            _status = cart.status();
            _name = cart.name();
            _description = cart.description();
            _template = cart.template();
            _itemCount = cart.numberOfContentItems();
            _itemSize = cart.sizeOfContentItems();
            return cart;
        } else {
            _status = null;
            _name = null;
            _description = null;
            _template = null;
            _itemCount = 0;
            _itemSize = 0;
            return null;
        }
    }

    @Override
    public String referentTypeName() {
        return ShoppingCart.TYPE_NAME;
    }

    @Override
    public String idToString() {
        return Long.toString(_id);
    }

    public long id() {
        return _id;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof ShoppingCartRef)) {
            return ((ShoppingCartRef) o).id() == id();
        }
        return false;
    }

}

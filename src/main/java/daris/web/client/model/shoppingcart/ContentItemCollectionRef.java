package daris.web.client.model.shoppingcart;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.OrderedCollectionRef;

public class ContentItemCollectionRef extends OrderedCollectionRef<ContentItem> {

    public static final int DEFAULT_PAGE_SIZE = 1000;

    private ShoppingCartRef _cart;
    private int _pageSize;

    public ContentItemCollectionRef(ShoppingCartRef cart) {
        _cart = cart;
        _pageSize = DEFAULT_PAGE_SIZE;
        setCountMembers(true);
    }

    public ContentItemCollectionRef(ShoppingCart cart) {
        this(new ShoppingCartRef(cart));
    }

    public ShoppingCartRef cart() {
        return _cart;
    }

    @Override
    public final int defaultPagingSize() {
        return _pageSize;
    }

    public final ContentItemCollectionRef setPagingSize(int pageSize) {
        _pageSize = pageSize;
        return this;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {

        w.add("sid", _cart.id());
        w.add("idx", start + 1);
        w.add("size", size);

    }

    @Override
    protected String resolveServiceName() {
        return "daris.shoppingcart.content.list";
    }

    @Override
    protected ContentItem instantiate(XmlElement ce) throws Throwable {
        return new ContentItem(ce, _cart);
    }

    @Override
    protected String referentTypeName() {
        return "asset";
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "asset" };
    }

}

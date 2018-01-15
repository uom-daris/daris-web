package daris.web.client.model.shoppingcart.messages;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.HasCiteableId;
import daris.web.client.model.shoppingcart.ShoppingCart;

public class ShoppingCartContentAdd extends ObjectMessage<Null> {

    private long _cartId;
    private Map<String, Boolean> _cids;
    private Set<String> _assetIds;
    private String _where;

    public ShoppingCartContentAdd(long cartId, String where) {
        _cartId = cartId;
        _where = where;
    }

    public ShoppingCartContentAdd(long cartId, Map<HasCiteableId, Boolean> objects) {
        _cartId = cartId;
        assert objects != null;
        assert !objects.isEmpty();
        _cids = new LinkedHashMap<String, Boolean>();
        Set<HasCiteableId> os = objects.keySet();
        for (HasCiteableId o : os) {
            _cids.put(o.citeableId(), objects.get(o));
        }
    }

    public ShoppingCartContentAdd(long cartId, Collection<HasCiteableId> objects, boolean recursive) {
        _cartId = cartId;
        assert objects != null;
        assert !objects.isEmpty();
        _cids = new LinkedHashMap<String, Boolean>();
        for (HasCiteableId o : objects) {
            _cids.put(o.citeableId(), recursive);
        }
    }

    public ShoppingCartContentAdd(long cartId, Collection<HasCiteableId> objects) {
        this(cartId, objects, true);
    }

    public ShoppingCartContentAdd(long cartId, boolean recursive, HasCiteableId... objects) {
        _cartId = cartId;
        assert objects != null && objects.length > 0;
        _cids = new LinkedHashMap<String, Boolean>();
        for (HasCiteableId o : objects) {
            _cids.put(o.citeableId(), recursive);
        }
    }

    public ShoppingCartContentAdd(long cartId, HasCiteableId... objects) {
        this(cartId, true, objects);
    }

    public ShoppingCartContentAdd(long cartId, String... assetIds) {
        _cartId = cartId;
        assert assetIds != null && assetIds.length > 0;
        _assetIds = new LinkedHashSet<String>();
        for (String assetId : assetIds) {
            _assetIds.add(assetId);
        }
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("sid", _cartId);
        if (_cids != null) {
            Set<String> cids = _cids.keySet();
            for (String cid : cids) {
                w.add("cid", new String[] { "recursive", Boolean.toString(_cids.get(cid)) }, cid);
            }
        }
        if (_assetIds != null) {
            for (String assetId : _assetIds) {
                w.add("id", assetId);
            }
        }
        if (_where != null) {
            w.add("where", _where);
        }
    }

    @Override
    protected String messageServiceName() {
        return "daris.shoppingcart.content.add";
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

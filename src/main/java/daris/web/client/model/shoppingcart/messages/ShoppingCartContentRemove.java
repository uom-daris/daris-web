package daris.web.client.model.shoppingcart.messages;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.HasAssetId;
import daris.web.client.model.object.HasCiteableId;
import daris.web.client.model.shoppingcart.ContentItem;
import daris.web.client.model.shoppingcart.ShoppingCart;

public class ShoppingCartContentRemove extends ObjectMessage<Null> {

    private long _cartId;
    private Map<String, Boolean> _cids;
    private Set<String> _assetIds;

    public ShoppingCartContentRemove(long cartId, Collection<HasCiteableId> objects, boolean recursive) {
        _cartId = cartId;
        assert objects != null && !objects.isEmpty();
        _cids = new LinkedHashMap<String, Boolean>();
        for (HasCiteableId o : objects) {
            _cids.put(o.citeableId(), recursive);
        }
    }

    public ShoppingCartContentRemove(long cartId, boolean recursive, HasCiteableId... objects) {
        _cartId = cartId;
        assert objects != null && objects.length > 0;
        _cids = new LinkedHashMap<String, Boolean>();
        for (HasCiteableId o : objects) {
            _cids.put(o.citeableId(), recursive);
        }
    }

    public ShoppingCartContentRemove(long cartId, boolean recursive, String... assetIds) {
        _cartId = cartId;
        assert assetIds != null && assetIds.length > 0;
        _assetIds = new LinkedHashSet<String>();
        for (String assetId : assetIds) {
            _assetIds.add(assetId);
        }
    }

    public ShoppingCartContentRemove(ShoppingCart cart, Collection<HasAssetId> assets) {
        _cartId = cart.id();
        _assetIds = new LinkedHashSet<String>();
        for (HasAssetId asset : assets) {
            _assetIds.add(asset.assetId());
        }
    }

    public ShoppingCartContentRemove(ShoppingCart cart, List<ContentItem> assets) {
        _cartId = cart.id();
        _assetIds = new LinkedHashSet<String>();
        for (HasAssetId asset : assets) {
            _assetIds.add(asset.assetId());
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
    }

    @Override
    protected String messageServiceName() {
        return "daris.shoppingcart.content.remove";
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

package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.OrderedCollectionRef;

public class DObjectChildrenRef extends OrderedCollectionRef<DObjectRef> {

    public static enum SortKey {
        CID("cid"), MTIME("mtime"), TYPE("type"), NAME("meta/daris:pssd-object/name"), CTYPE("content/type");

        private String _xpath;

        SortKey(String xpath) {
            _xpath = xpath;
        }

        public String xpath() {
            return _xpath;
        }
    }

    private DObjectRef _parent;
    private int _pageSize = 100;
    private SortKey _sortKey;
    private SortOrder _sortOrder;

    public DObjectChildrenRef(DObjectRef parent) {
        _parent = parent;

        setCountMembers(true);

        _sortKey = SortKey.CID;
        _sortOrder = SortOrder.ASC;
    }

    public void setPageSize(int pageSize) {
        if (pageSize != _pageSize) {
            _pageSize = pageSize;
            reset();
        }
    }

    public void setSortOrder(SortOrder order) {
        _sortOrder = order;
    }

    public SortOrder sortOrder() {
        return _sortOrder;
    }

    public void setSortKey(SortKey key) {
        _sortKey = key;
    }

    public SortKey sortKey() {
        return _sortKey;
    }

    @Override
    public int defaultPagingSize() {
        return _pageSize;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {
        if (_parent != null && _parent.citeableId() != null) {
            w.add("cid", _parent.citeableId());
        }
        w.add("idx", start + 1);
        w.add("size", size);
        w.add("count", count);
        w.push("sort");
        w.add("key", _sortKey.xpath());
        if (_sortOrder != null) {
            w.add("order", _sortOrder.toString());
        }
        w.pop();
    }

    @Override
    protected String resolveServiceName() {
        return "daris.object.children.list";
    }

    @Override
    protected DObjectRef instantiate(XmlElement xe) throws Throwable {
        return new DObjectRef(xe);
    }

    @Override
    protected String referentTypeName() {
        return "pssd-object";
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "object" };
    }

    public DObjectRef parent() {
        return _parent;
    }
    
    @Override
    public boolean supportsPaging() {
        return true;
    }

}

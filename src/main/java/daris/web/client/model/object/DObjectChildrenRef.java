package daris.web.client.model.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.util.ListUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.dtype.EnumerationType;
import arc.mf.object.OrderedCollectionRef;
import daris.web.client.model.object.filter.Filter;

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

        public static EnumerationType<SortKey> toEnumerationType() {
            List<EnumerationType.Value<SortKey>> evs = new ArrayList<EnumerationType.Value<SortKey>>(5);
            evs.add(new EnumerationType.Value<SortKey>("object id", "Object id", CID));
            evs.add(new EnumerationType.Value<SortKey>("object name", "Object name", NAME));
            evs.add(new EnumerationType.Value<SortKey>("object mime type", "Object MIME type", TYPE));
            evs.add(new EnumerationType.Value<SortKey>("modification time", "Modification time", MTIME));
            evs.add(new EnumerationType.Value<SortKey>("content type", "Content MIME type", CTYPE));
            return new EnumerationType<SortKey>(evs);
        }
    }

    private DObjectRef _parent;
    private int _pageSize = 100;
    private SortKey _sortKey;
    private SortOrder _sortOrder;
    private List<Filter> _filters;

    public DObjectChildrenRef(DObjectRef parent, Filter filter, SortKey sortKey, SortOrder sortOrder) {
        this(parent, ListUtil.list(filter), sortKey, sortOrder);
    }

    public DObjectChildrenRef(DObjectRef parent, List<Filter> filters, SortKey sortKey, SortOrder sortOrder) {
        _parent = parent;

        setCountMembers(true);

        _filters = new ArrayList<Filter>();
        if (filters != null) {
            for (Filter filter : filters) {
                if (filter != null) {
                    _filters.add(filter);
                }
            }
        }

        _sortKey = sortKey == null ? SortKey.CID : sortKey;
        _sortOrder = sortOrder == null ? SortOrder.ASC : sortOrder;
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

    public void addFilter(Filter filter) {
        if (_filters == null) {
            _filters = new ArrayList<Filter>();
        }
        if (filter != null) {
            _filters.add(filter);
        }
    }

    public void removeFilter(Filter filter) {
        if (_filters != null) {
            _filters.remove(filter);
        }
    }

    public void setFilters(Collection<Filter> filters) {
        if (_filters == null) {
            _filters = new ArrayList<Filter>();
        } else {
            _filters.clear();
        }
        if (filters != null && !filters.isEmpty()) {
            for (Filter filter : filters) {
                if (filter != null) {
                    _filters.add(filter);
                }
            }
        }
    }

    public void setFilter(Filter filter) {
        setFilters(ListUtil.list(filter));
    }

    public void removeAllFilters() {
        if (_filters != null) {
            _filters.clear();
        }
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
        if (_filters != null) {
            for (Filter filter : _filters) {
                w.add("filter", filter.toAQLString());
            }
        }
        if (_sortKey != null) {
            w.push("sort");
            w.add("key", _sortKey.xpath());
            w.add("nulls", "include");
            if (_sortOrder != null) {
                w.add("order", _sortOrder.toString());
            }
            w.pop();
        }
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

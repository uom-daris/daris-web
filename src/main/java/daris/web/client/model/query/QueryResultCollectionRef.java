package daris.web.client.model.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.OrderedCollectionRef;

public abstract class QueryResultCollectionRef<T extends IsQueryResult> extends OrderedCollectionRef<T>
        implements HasXPath {

    public static final int DEFAULT_PAGE_SIZE = 100;

    private int _pageSize;

    private List<String> _wheres;
    private Action _action;
    private SortOrder _sortOrder;
    private Map<String, SortOrder> _sortKeys;
    private Set<XPath> _xpaths;

    protected QueryResultCollectionRef(String... wheres) {
        setCountMembers(true);
        _pageSize = DEFAULT_PAGE_SIZE;
        _wheres = new ArrayList<String>();
        if (wheres != null) {
            for (String w : wheres) {
                _wheres.add(w);
            }
        }
        _action = Action.GET_VALUE;
        _sortOrder = null;
        _sortKeys = new LinkedHashMap<String, SortOrder>();
    }

    public void addWhere(String where) {
        _wheres.add(where);
    }

    public void setWhere(String... wheres) {
        _wheres.clear();
        if (wheres != null) {
            for (String w : wheres) {
                _wheres.add(w);
            }
        }
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {
        for (String where : _wheres) {
            w.add("where", where);
        }
        if (_xpaths != null) {
            for (XPath xpath : _xpaths) {
                w.add("xpath", new String[] { "ename", xpath.ename() }, xpath.xpath());
            }
        }
        w.add("action", _action.value());
        w.add("idx", start + 1);
        w.add("size", size);
        if (!_sortKeys.isEmpty() || _sortOrder != null) {
            w.push("sort");
            if (!_sortKeys.isEmpty()) {
                Set<String> keys = _sortKeys.keySet();
                for (String key : keys) {
                    SortOrder order = _sortKeys.get(key);
                    w.add("key", new String[] { "order", order == null ? null : order.value() }, key);
                }
            }
            if (_sortOrder != null) {
                w.add("order", _sortOrder.value());
            }
            w.pop();
        }
    }

    @Override
    protected String resolveServiceName() {
        return "asset.query";
    }

    @Override
    protected String referentTypeName() {
        return "asset";
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "asset" };
    }

    public void addSortKey(String key, SortOrder order) {
        _sortKeys.put(key, order);
    }

    public void setSortOrder(SortOrder order) {
        _sortOrder = order;
    }

    public void setAction(Action action) {
        _action = action == null ? Action.GET_VALUE : action;
    }
    
    public Action action() {
        return _action;
    }

    @Override
    public void addXPath(String xpath, String ename) {
        if (_xpaths == null) {
            _xpaths = new LinkedHashSet<XPath>();
        }
        _xpaths.add(new XPath(xpath, ename));
    }

    @Override
    public Set<XPath> xpaths() {
        return _xpaths;
    }

    @Override
    public boolean hasXPaths() {
        return _xpaths != null && !_xpaths.isEmpty();
    }

    @Override
    public int defaultPagingSize() {
        return _pageSize;
    }

    public void setPagingSize(int pageSize) {
        _pageSize = pageSize;
    }

}

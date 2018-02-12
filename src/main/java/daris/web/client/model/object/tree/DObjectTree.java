package daris.web.client.model.object.tree;

import java.util.HashMap;
import java.util.List;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.image.Image;
import arc.mf.object.tree.Container;
import arc.mf.object.tree.Tree;
import arc.mf.object.tree.TreeNodeDescriptionHandler;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.filter.SimpleObjectFilter;
import daris.web.client.model.query.sort.SortKey;

public class DObjectTree implements Tree {

    public static final int DEFAULT_PAGE_SIZE = 100;

    private RootNode _root;
    private boolean _readOnly = false;
    private int _pageSize = DEFAULT_PAGE_SIZE;
    private HashMap<DObjectRef, SimpleObjectFilter> _filters;
    private SortKey _sortKey;

    public DObjectTree() {
        _pageSize = DEFAULT_PAGE_SIZE;
        _sortKey = SortKey.citeableId();
        _root = new RootNode(this);
    }

    public void setFilter(DObjectRef parent, SimpleObjectFilter filter) {
        if (_filters == null) {
            _filters = new HashMap<DObjectRef, SimpleObjectFilter>();
        }
        if (filter == null) {
            _filters.remove(parent);
        } else {
            _filters.put(parent, filter);
        }
    }

    public SimpleObjectFilter filter(DObjectRef parent) {
        return _filters == null ? null : _filters.get(parent);
    }

    public int pageSize() {
        return _pageSize;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    public SortKey sortKey() {
        return _sortKey;
    }

    public void setSortKey(SortKey sortKey) {
        _sortKey = sortKey;
    }

    @Override
    public Image icon() {
        return null;
    }

    @Override
    public Container root() {
        return _root;
    }

    public RootNode rootNode() {
        return _root;
    }

    @Override
    public boolean readOnly() {
        return _readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        _readOnly = readOnly;
    }

    @Override
    public void discard() {

    }

    public static class RootNode extends DObjectTreeNode {

        RootNode(DObjectTree tree) {
            super(tree, null, null);
        }

        @Override
        public String type() {
            return "repository";
        }

        @Override
        public String name() {
            return "DaRIS Repository";
        }

        @Override
        public String path() {
            return null;
        }

        @Override
        public void description(TreeNodeDescriptionHandler dh) {

        }

        @Override
        public List<BaseWidget> adornments() {
            return null;
        }

        @Override
        public Object object() {
            return null;
        }

    }

}

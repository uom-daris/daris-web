package daris.web.client.gui.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.ListGridCellWidget;
import daris.web.client.model.query.IsQueryResult;
import daris.web.client.model.query.QueryResultCollectionRef;
import daris.web.client.model.query.XPath;

public class QueryResultForm<T extends IsQueryResult> extends ValidatedInterfaceComponent implements PagingListener {

    private QueryResultCollectionRef<T> _rc;

    private T _selected;

    private VerticalPanel _vp;
    private ListGrid<T> _list;
    private List<SelectionHandler<T>> _shs;
    private PagingControl _pc;

    public QueryResultForm(QueryResultCollectionRef<T> rc) {
        _rc = rc;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<T>(ScrollPolicy.AUTO) {
            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<T>> entries) {
                if (entries != null && !entries.isEmpty()) {
                    if (_selected != null) {
                        if (!select(_selected)) {
                            select(0);
                        }
                    } else {
                        select(0);
                    }
                }
            }
        };
        _list.fitToParent();
        _list.setMultiSelect(true);
        _list.setMinRowHeight(DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _list.setClearSelectionOnRefresh(false);
        _list.setSelectionHandler(new SelectionHandler<T>() {

            @Override
            public void selected(T o) {
                _selected = o;
                notifyOfSelect(_selected);
            }

            @Override
            public void deselected(T o) {
                _selected = null;
                notifyOfDeselect(_selected);
            }
        });

        _list.addColumnDefn("type", "TYPE", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER);
        _list.addColumnDefn("cid", "ID", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER);
        _list.addColumnDefn("name", "NAME", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER);
        _list.setEmptyMessage("No results found.");
        _list.setLoadingMessage("Searching...");
        Set<XPath> xpaths = _rc.xpaths();
        if (xpaths != null) {
            for (XPath xpath : xpaths) {
                if (!"cid".equals(xpath.ename()) && !"type".equals(xpath.ename()) && !"name".equals(xpath.ename())) {
                    _list.addColumnDefn(xpath.ename(), xpath.ename().replace('-', ' ').toUpperCase(),
                            xpath.ename() + "(xpath: " + xpath.xpath() + ")",
                            ListGridCellWidget.DEFAULT_TEXT_FORMATTER);
                }
            }
        }
        _list.addColumnDefn("assetId", "ASSET ID", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER);
        _vp.add(_list);

        _pc = new PagingControl(_rc.pagingSize());
        _pc.addPagingListener(this);
        _vp.add(_pc);

        gotoOffset(0);
    }

    public void addSelectionHandler(SelectionHandler<T> sh) {
        if (_shs == null) {
            _shs = new ArrayList<SelectionHandler<T>>();
        }
        _shs.add(sh);
    }

    public void removeSelectionHandler(SelectionHandler<T> sh) {
        if (_shs != null) {
            _shs.remove(sh);
        }
    }

    private void notifyOfSelect(T selected) {
        if (_shs != null) {
            for (SelectionHandler<T> sh : _shs) {
                sh.selected(selected);
            }
        }
    }

    private void notifyOfDeselect(T selected) {
        if (_shs != null) {
            for (SelectionHandler<T> sh : _shs) {
                sh.deselected(selected);
            }
        }
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void gotoOffset(long offset) {
        _list.setBusyLoading();
        _rc.resolve(offset, offset + _rc.pagingSize(), os -> {
            long total = _rc.totalNumberOfMembers();
            _pc.setOffset(offset, total, true);
            List<ListGridEntry<T>> entries = null;
            if (os != null && !os.isEmpty()) {
                entries = new ArrayList<ListGridEntry<T>>();
                for (T o : os) {
                    ListGridEntry<T> entry = new ListGridEntry<T>(o);
                    entry.set("cid", o.citeableId());
                    entry.set("assetId", o.assetId());
                    entry.set("name", o.name());
                    Set<XPath> xpaths = _rc.xpaths();
                    if (xpaths != null) {
                        for (XPath xpath : xpaths) {
                            entry.set(xpath.ename(), o.xvalue(xpath).value());
                        }
                    }
                    entries.add(entry);
                }
            }
            _list.setData(entries);
            postLoad(_rc, offset, os);
        });
    }

    protected void postLoad(QueryResultCollectionRef<T> rc, long offset, List<T> ros) {

    }

    public void refresh() {
        _rc.reset();
        gotoOffset(0);
    }

    public T selected() {
        return _selected;
    }

    public List<T> selections() {
        return _list.selections();
    }

    public boolean haveSelections() {
        return _list.haveSelections();
    }

}

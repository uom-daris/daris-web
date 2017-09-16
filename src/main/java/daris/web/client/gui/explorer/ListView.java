package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.ImageButton;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.list.ListGridRowContextMenuHandler;
import arc.gui.gwt.widget.list.ListGridRowDoubleClickHandler;
import arc.gui.gwt.widget.list.ListGridRowEnterHandler;
import arc.gui.gwt.widget.menu.ActionMenu;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.table.Table.Row;
import arc.mf.client.util.ListUtil;
import arc.mf.client.util.ObjectUtil;
import arc.mf.event.Filter;
import arc.mf.event.Subscriber;
import arc.mf.event.SystemEvent;
import arc.mf.event.SystemEventChannel;
import arc.mf.object.CollectionResolveHandler;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.Session;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.DObjectGUI;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectChildrenRef;
import daris.web.client.model.object.DObjectChildrenRef.SortKey;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.SortOrder;
import daris.web.client.model.object.event.DObjectEvent;
import daris.web.client.model.object.filter.SimpleObjectFilter;
import daris.web.client.model.object.messages.DObjectChildCursorFromGet;
import daris.web.client.util.StringUtils;

public class ListView extends ContainerWidget implements PagingListener, Subscriber {

    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final SortKey DEFAULT_SORT_KEY = SortKey.CID;
    public static final SortOrder DEFAULT_SORT_ORDER = SortOrder.ASC;
    public static final int MAX_MAP_ENTRIES = 100;
    public static final int MIN_ROW_HEIGHT = 28;
    public static final int FONT_SIZE = 11;

    public static final arc.gui.image.Image ICON_OPTIONS = new arc.gui.image.Image(
            Resource.INSTANCE.options16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_FOLDER = new arc.gui.image.Image(
            Resource.INSTANCE.folder32().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_FOLDER_ENTER = new arc.gui.image.Image(
            Resource.INSTANCE.folderEnter32().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_DOCUMENT = new arc.gui.image.Image(
            Resource.INSTANCE.document32().getSafeUri().asString(), 16, 16);

    private DObjectRef _parent;
    private LinkedHashMap<DObjectRef, DObjectChildrenRef> _childrenMap;
    private LinkedHashMap<DObjectRef, DObjectRef> _selectedMap;
    private HashMap<DObjectRef, SimpleObjectFilter> _filters;
    private SortKey _sortKey;
    private SortOrder _sortOrder;
    private List<DObjectRef> _childrenInCurrentPage;
    private DObjectRef _toSelect;

    private VerticalPanel _vp;
    private VerticalPanel _listVP;
    private ListGrid<DObjectRef> _list;
    private ListViewOptionsForm _viewOptionsForm;
    private PagingControl _pc;
    private ImageButton _optionsButton;
    private int _pageSize = DEFAULT_PAGE_SIZE;

    public ListView(DObjectRef parent) {

        _parent = parent;
        _childrenMap = new LinkedHashMap<DObjectRef, DObjectChildrenRef>() {
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<DObjectRef, DObjectChildrenRef> eldest) {
                return size() > MAX_MAP_ENTRIES;
            }
        };
        _selectedMap = new LinkedHashMap<DObjectRef, DObjectRef>() {
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<DObjectRef, DObjectRef> eldest) {
                return size() > MAX_MAP_ENTRIES;
            }
        };
        _filters = new HashMap<DObjectRef, SimpleObjectFilter>();
        _sortKey = SortKey.CID;
        _sortOrder = SortOrder.ASC;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _listVP = new VerticalPanel();
        _listVP.fitToParent();
        _vp.add(_listVP);

        _list = new ListGrid<DObjectRef>(ScrollPolicy.AUTO) {

            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<DObjectRef>> entries) {
                /*
                 * restore selections
                 */
                if (entries != null && !entries.isEmpty()) {
                    DObjectRef selected = _selectedMap.get(_parent);
                    if (selected == null && _toSelect == null) {
                        select(0);
                    } else {
                        int selectedIdx = -1;
                        int toSelectIdx = -1;
                        for (int i = 0; i < entries.size(); i++) {
                            DObjectRef o = entries.get(i).data();
                            if (_toSelect != null && _toSelect.equals(o)) {
                                toSelectIdx = i;
                            }
                            if (selected != null && selected.equals(o)) {
                                selectedIdx = i;
                            }
                        }
                        if (toSelectIdx >= 0 && toSelectIdx != selectedIdx) {
                            clearSelections();
                            select(toSelectIdx);
                        } else if (selectedIdx >= 0) {
                            select(selectedIdx);
                        } else {
                            select(0);
                        }
                    }
                } else {
                    ListView.this.opened(_parent);
                }
                _toSelect = null;
            }

        };
        _list.setObjectRegistry(DObjectGUIRegistry.get());
        _list.fitToParent();
        _list.setMinRowHeight(MIN_ROW_HEIGHT);

        _list.setClearSelectionOnRefresh(false);

        _list.setMultiSelect(false);
        _list.setRowContextMenuHandler(new ListGridRowContextMenuHandler<DObjectRef>() {

            @Override
            public void show(DObjectRef o, ContextMenuEvent event) {
                ActionMenu.showAt(event.getNativeEvent().getScreenX(), _list.rowFor(o).absoluteBottom(),
                        DObjectGUI.INSTANCE.actionMenu(window(), o, null, false));
            }
        });
        _list.setSelectionHandler(new SelectionHandler<DObjectRef>() {

            @Override
            public void selected(DObjectRef o) {
                // save selection to cache
                if ((_parent == null && o.isProject()) || (_parent != null && _parent.isDirectParentOf(o))) {
                    _selectedMap.put(_parent, o);
                } else {
                    _selectedMap.put(o.parent(), o);
                }
                ListView.this.selected(o);
            }

            @Override
            public void deselected(DObjectRef o) {
                ListView.this.deselected(o);
            }
        });
        _list.setEmptyMessage("");
        _list.setLoadingMessage("loading...");
        _list.setCursorSize(_pageSize);
        _list.addColumnDefn("nbc", "", null, new WidgetFormatter<DObjectRef, Integer>() {

            @Override
            public BaseWidget format(DObjectRef o, Integer nbc) {
                return new ObjectIcon(o);
            }
        }).setWidth(28);

        _list.addColumnDefn("cid", "ID", "Object identifier", new WidgetFormatter<DObjectRef, String>() {

            @Override
            public BaseWidget format(DObjectRef o, String cid) {
                HTML html = new HTML(StringUtils.upperCaseFirst(o.referentTypeName()) + " " + cid);
                html.setFontSize(FONT_SIZE);
                html.setFontFamily("Roboto,Helvetica,sans-serif");
                html.setFontWeight(FontWeight.BOLD);
                if (!o.isDataset() && o.numberOfChildren() != 0) {
                    html.setCursor(Cursor.POINTER);
                }
                html.element().getStyle().setProperty("letterSpacing", "0.0625em");
                return html;
            }
        }).setWidth(150);

        _list.addColumnDefn("name", "Name", "Object name", new WidgetFormatter<DObjectRef, String>() {

            @Override
            public BaseWidget format(DObjectRef o, String name) {
                HTML html = new HTML(name);
                html.setFontSize(FONT_SIZE);
                html.setFontFamily("Roboto,Helvetica,sans-serif");
                html.setFontWeight(FontWeight.BOLD);
                if (!o.isDataset() && o.numberOfChildren() != 0) {
                    html.setCursor(Cursor.POINTER);
                }
                html.element().getStyle().setProperty("letterSpacing", "0.0625em");
                return html;
            }
        }).setWidth(500);

        _list.addColumnDefn("nbc", "Number of Members", "Number of member objects",
                new WidgetFormatter<DObjectRef, Integer>() {

                    @Override
                    public BaseWidget format(DObjectRef o, Integer nbc) {
                        HTML html = (nbc == null || nbc < 0) ? new HTML() : new HTML(Integer.toString(nbc));
                        html.setFontSize(FONT_SIZE);
                        html.setFontFamily("Roboto,Helvetica,sans-serif");
                        html.setFontWeight(FontWeight.BOLD);
                        if (!o.isDataset() && o.numberOfChildren() != 0) {
                            html.setCursor(Cursor.POINTER);
                        }
                        html.setTextAlign(TextAlign.CENTER);
                        html.setVerticalAlign(VerticalAlign.MIDDLE);
                        html.element().getStyle().setLineHeight(MIN_ROW_HEIGHT, Unit.PX);
                        html.setHeight(MIN_ROW_HEIGHT);
                        html.setWidth100();
                        return html;
                    }
                }).setWidth(120);

        _list.setRowDoubleClickHandler(new ListGridRowDoubleClickHandler<DObjectRef>() {

            @Override
            public void doubleClicked(DObjectRef o, DoubleClickEvent event) {
                open(o);
            }
        });

        _list.setRowEnterHandler(new ListGridRowEnterHandler<DObjectRef>() {

            @Override
            public void onEnter(DObjectRef o) {
                open(o);
            }
        });

        _listVP.add(_list);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setHeight(PagingControl.DEFAULT_HEIGHT);
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        hp.setBackgroundImage(new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM, RGB.GREY_AAA, RGB.GREY_777));
        hp.setWidth100();
        hp.setPaddingRight(2);

        _pc = new PagingControl(_pageSize);
        _pc.addPagingListener(this);
        hp.add(_pc);

        hp.addSpacer(10);

        _optionsButton = new ImageButton(ICON_OPTIONS);
        _optionsButton.addClickHandler(event -> {
            if (_viewOptionsForm != null) {
                hideViewOptionsForm();
            } else {
                showViewOptionsForm();
            }
        });
        hp.add(_optionsButton);

        _vp.add(hp);

        initWidget(_vp);

        /*
         * subscribe to system events.
         */
        SystemEventChannel.add(this);

        // gotoOffset(0);
    }

    public void open(DObjectRef o) {
        if (o != null) {
            if (o.isDataset()) {
                // cannot open dataset.
                return;
            }
            // save current selections to cache...
            if ((_parent == null && o.isProject()) || (_parent != null && _parent.isDirectParentOf(o))) {
                _selectedMap.put(_parent, o);
            } else {
                _selectedMap.put(o.parent(), o);
            }
        }
        if (_list != null) {
            _list.clearSelections();
            _list.setBusyLoading();
        }
        _parent = o;
        DObjectRef selected = _selectedMap.get(_parent);
        if (selected != null) {
            seekTo(selected, true);
        } else {
            gotoOffset(0);
        }
    }

    protected void opened(DObjectRef o) {

    }

    protected void selected(DObjectRef o) {

    }

    protected void deselected(DObjectRef o) {

    }

    protected void updated(DObjectRef o) {

    }

    private DObjectChildrenRef childrenRef() {
        DObjectChildrenRef c = _childrenMap.get(_parent);
        SimpleObjectFilter filter = _filters.get(_parent);
        if (c == null) {
            c = new DObjectChildrenRef(_parent, filter, _sortKey, _sortOrder);
            c.setPageSize(_pageSize);
            _pc.setPageSize(_pageSize);
            _childrenMap.put(_parent, c);
        } else {
            if (filter != null) {
                c.setFilter(_filters.get(_parent));
            } else {
                c.removeAllFilters();
            }
            c.setSortKey(_sortKey);
            c.setSortOrder(_sortOrder);
            c.setPageSize(_pageSize);
            c.reset();
            _pc.setPageSize(_pageSize);
        }
        return c;
    }

    @Override
    public void gotoOffset(final long offset) {
        final DObjectChildrenRef childrenRef = childrenRef();
        childrenRef.resolve(offset, offset + _pageSize, new CollectionResolveHandler<DObjectRef>() {
            @Override
            public void resolved(List<DObjectRef> cos) throws Throwable {
                long total = childrenRef.totalNumberOfMembers();
                _pc.setOffset(offset, total, true);
                _childrenInCurrentPage = cos;
                List<ListGridEntry<DObjectRef>> entries = null;
                if (_childrenInCurrentPage != null && !_childrenInCurrentPage.isEmpty()) {
                    entries = new ArrayList<ListGridEntry<DObjectRef>>();
                    for (DObjectRef co : _childrenInCurrentPage) {
                        ListGridEntry<DObjectRef> entry = new ListGridEntry<DObjectRef>(co);
                        entry.set("cid", co.citeableId());
                        entry.set("name", co.name());
                        entry.set("nbc", co.numberOfChildren());
                        entries.add(entry);
                    }
                }
                _list.setData(entries);
            }
        });

    }

    public void seekTo(DObjectRef object, boolean refresh) {
        DObjectRef parent = object.parent();
        if (!ObjectUtil.equals(_parent, parent)) {
            _parent = parent;
            refresh = true;
        }
        DObjectRef selected = _selectedMap.get(_parent);
        if (ObjectUtil.equals(selected, object) && !refresh) {
            return;
        }
        _toSelect = object;
        new DObjectChildCursorFromGet(object, childrenRef()).send(new ObjectMessageResponse<Long>() {

            @Override
            public void responded(Long idx) {
                _toSelect = object;
                if (idx != null && idx > 0) {
                    gotoOffset(idx - 1);
                } else {
                    gotoOffset(0);
                }
            }
        });
    }

    private static class ObjectIcon extends ContainerWidget {

        private AbsolutePanel _ap;
        private arc.gui.gwt.widget.image.Image _img;

        ObjectIcon(DObjectRef o) {

            _ap = new AbsolutePanel();
            _ap.setWidth100();
            _ap.setHeight(MIN_ROW_HEIGHT);
            _ap.setCursor(Cursor.POINTER);

            if (o.isDataset()) {
                _img = new arc.gui.gwt.widget.image.Image(ICON_DOCUMENT);
            } else {
                _img = new arc.gui.gwt.widget.image.Image(ICON_FOLDER_ENTER);
                _img.setTitle(toolTipFor(o));
            }
            _img.setPosition(Position.ABSOLUTE);
            _img.element().getStyle().setProperty("margin", "auto");
            _img.setTop(0);
            _img.setBottom(0);
            _img.setLeft(0);
            _img.setRight(0);
            _ap.add(_img);
            initWidget(_ap);
        }

        private static String toolTipFor(DObjectRef o) {
            if (o.numberOfChildren() > 0) {
                String childrenType = o.childTypeName();
                if (childrenType != null) {
                    if (childrenType.endsWith("y")) {
                        childrenType = childrenType.substring(0, childrenType.length() - 1) + "ies";
                    } else {
                        childrenType = childrenType + "s";
                    }
                }
                return "contains " + o.numberOfChildren() + " " + childrenType + ". Double-click to open.";
            } else {
                return "may contain " + o.childTypeName() + ". Double-click to open.";
            }
        }

        public void update(DObjectRef o) {
            if (!o.isDataset()) {
                _img.setTitle(toolTipFor(o));
            }
        }

    }

    private void refreshRow(DObjectRef object) {
        object.reset();
        object.resolve(o -> {
            Row row = _list.rowFor(object);
            if (row == null) {
                return;
            }
            // col 0: icon
            ObjectIcon icon = (ObjectIcon) row.cell(0).widget();
            icon.update(object);
            // col 2: name
            HTML name = (HTML) row.cell(2).widget();
            name.setHTML(object.name());
            // col 3: nbc
            HTML nbc = (HTML) row.cell(3).widget();
            if (object.numberOfChildren() < 0) {
                nbc.clear();
            } else {
                nbc.setHTML(Integer.toString(object.numberOfChildren()));
            }
        });
    }

    private boolean isCurrentPageFull() {
        return _childrenInCurrentPage != null && _childrenInCurrentPage.size() == _pageSize;
    }

    private boolean isInCurrentPage(DObjectRef o) {
        return _childrenInCurrentPage != null && _childrenInCurrentPage.contains(o);
    }

    private boolean isInCurrentPage(String cid) {
        return isInCurrentPage(new DObjectRef(cid, -1));
    }

    private void showViewOptionsForm() {
        hideViewOptionsForm();
        _viewOptionsForm = new ListViewOptionsForm(_filters.get(_parent), _sortKey, _sortOrder, _pageSize);
        _viewOptionsForm.addUpdateListener((filter, sortKey, sortOrder, pageSize) -> {
            _filters.put(_parent, filter);
            _sortKey = sortKey;
            _sortOrder = sortOrder;
            _pageSize = pageSize;
            gotoOffset(0);
            _viewOptionsForm.hide();
            _viewOptionsForm = null;
        });
        _viewOptionsForm.setCloseHandler(() -> {
            _listVP.remove(_viewOptionsForm.gui());
        });
        _listVP.add(_viewOptionsForm.gui());

    }

    private void hideViewOptionsForm() {
        if (_viewOptionsForm != null) {
            _listVP.remove(_viewOptionsForm.gui());
            _viewOptionsForm = null;
        }
    }

    private void collectionRemoved(String pid) {
        Set<DObjectRef> keys = _selectedMap.keySet();
        Set<DObjectRef> toDelete = new HashSet<DObjectRef>();
        for (DObjectRef key : keys) {
            DObjectRef v = _selectedMap.get(key);
            if (v != null) {
                String cid = v.citeableId();
                if (cid.equals(pid) || CiteableIdUtils.isChild(cid, pid)) {
                    toDelete.add(key);
                }
            }
        }
        if (!toDelete.isEmpty()) {
            for (DObjectRef key : toDelete) {
                _selectedMap.remove(key);
            }
        }
    }

    @Override
    public List<Filter> systemEventFilters() {
        return ListUtil.list(new Filter(DObjectEvent.SYSTEM_EVENT_NAME, null));
    }

    @Override
    public void process(SystemEvent se) {
        DObjectEvent de = (DObjectEvent) se;
        DObjectEvent.isRelavent(de, relavent -> {
            if (relavent) {
                handleEvent(de);
            }
        });
    }

    private void handleEvent(DObjectEvent de) {
        DObjectEvent.Action action = de.action();
        String ecid = de.citeableId();

        DObjectRef selected = _selectedMap.get(_parent);

        switch (action) {
        case MODIFY:
            DObjectRef eo = new DObjectRef(ecid, -1);
            if (de.matchesObject(selected)) {
                if (isInCurrentPage(selected)) {
                    refreshRow(selected);
                }
                updated(eo);
            } else if (de.isParentOf(selected)) {
                updated(eo);
            }
            break;
        case CREATE:
            if (_parent == null) {
                if (CiteableIdUtils.isProject(ecid)) {
                    if (!isCurrentPageFull()) {
                        if (selected != null) {
                            seekTo(selected, true);
                        } else {
                            gotoOffset(0);
                        }
                    }
                } else if (CiteableIdUtils.isSubject(ecid)) {
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (isInCurrentPage(o)) {
                        refreshRow(o);
                    }
                }
            } else {
                if (de.isDirectChildOf(_parent)) {
                    if (!isCurrentPageFull()) {
                        if (selected != null) {
                            seekTo(selected, true);
                        } else {
                            gotoOffset(0);
                        }
                    }
                } else if (de.isGrandChildOf(_parent)) {
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (isInCurrentPage(o)) {
                        refreshRow(o);
                    }
                }
            }
            break;
        case DESTROY:
            if (_parent == null) {
                if (CiteableIdUtils.isProject(ecid)) {
                    collectionRemoved(ecid);
                    if (isInCurrentPage(ecid)) {
                        if (selected != null) {
                            seekTo(selected, true);
                        } else {
                            gotoOffset(0);
                        }
                    }
                } else if (CiteableIdUtils.isSubject(ecid)) {
                    collectionRemoved(ecid);
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (isInCurrentPage(o)) {
                        refreshRow(o);
                    }
                }
            } else {
                if (de.isDirectChildOf(selected)) {
                    collectionRemoved(ecid);
                    if (isInCurrentPage(selected)) {
                        refreshRow(selected);
                    }
                } else if (de.isGrandChildOf(_parent)) {
                    collectionRemoved(ecid);
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (isInCurrentPage(o)) {
                        refreshRow(o);
                    }
                } else {
                    if (de.isDirectChildOf(_parent)) {
                        if (de.matchesObject(selected)) {
                            collectionRemoved(ecid);
                        }
                        if (selected != null) {
                            seekTo(selected, true);
                        } else {
                            gotoOffset(0);
                        }
                    } else if (de.matchesObject(_parent) || de.isParentOf(_parent)) {
                        collectionRemoved(ecid);
                        resolveNearestExistingParent(ecid, pid -> {
                            open(pid == null ? null : new DObjectRef(pid, -1));
                        });
                    }
                }
            }
            break;
        default:
            break;
        }
    }

    private void resolveNearestExistingParent(String cid, ObjectResolveHandler<String> rh) {
        String pid = cid == null ? null : CiteableIdUtils.parent(cid);
        if (pid == null) {
            if (rh != null) {
                rh.resolved(null);
            }
            return;
        }
        Session.execute("asset.exists", "<cid>" + pid + "</cid>", (xe, outputs) -> {
            boolean exists = xe.booleanValue("exists");
            if (!exists) {
                collectionRemoved(pid);
                resolveNearestExistingParent(pid, rh);
            } else {
                if (rh != null) {
                    rh.resolved(pid);
                }
            }
        });
    }

}

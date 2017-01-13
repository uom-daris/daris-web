package daris.web.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridColumn;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.list.ListGridRowDoubleClickHandler;
import arc.gui.gwt.widget.list.ListGridRowEnterHandler;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.ObjectUtil;
import arc.mf.object.CollectionResolveHandler;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.model.object.DObjectChildrenRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.messages.DObjectChildCursorFromGet;
import daris.web.client.util.StringUtils;

/**
 * NOTE: This ListGrid supports multi-select also it has header checkbox and row
 * check box to make selections easier. However, it causes flood of (selection)
 * events. Do not have time to deal with it, so decide not to use this class.
 * 
 * @author wliu5
 *
 */

public class DObjectListGrid extends ContainerWidget implements PagingListener {

    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final int MAX_MAP_ENTRIES = 100;
    public static final int MIN_ROW_HEIGHT = 28;
    public static final int FONT_SIZE = 11;

    public static interface ParentUpdateListener {
        void parentUpdated(DObjectRef parent);
    }

    private DObjectRef _parent;
    private LinkedHashMap<DObjectRef, DObjectChildrenRef> _childrenMap;
    private LinkedHashMap<DObjectRef, Set<DObjectRef>> _selectionMap;
    private LinkedHashMap<DObjectRef, DObjectRef> _selectedMap;
    private List<DObjectRef> _childrenInCurrentPage;

    private DObjectRef _toSelect;

    private VerticalPanel _vp;

    private ListGrid<DObjectRef> _list;
    private HeaderCheckBox _headerCheckBox;
    private PagingControl _pc;

    private List<SelectionHandler<DObjectRef>> _shs;
    private List<ParentUpdateListener> _puls;

    private int _pageSize = DEFAULT_PAGE_SIZE;

    public DObjectListGrid(DObjectRef parent) {

        _parent = parent;
        _childrenMap = new LinkedHashMap<DObjectRef, DObjectChildrenRef>() {
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<DObjectRef, DObjectChildrenRef> eldest) {
                return size() > MAX_MAP_ENTRIES;
            }
        };
        _selectionMap = new LinkedHashMap<DObjectRef, Set<DObjectRef>>() {
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<DObjectRef, Set<DObjectRef>> eldest) {
                return size() > MAX_MAP_ENTRIES;
            }
        };
        _selectedMap = new LinkedHashMap<DObjectRef, DObjectRef>() {
            private static final long serialVersionUID = 1L;

            protected boolean removeEldestEntry(Map.Entry<DObjectRef, DObjectRef> eldest) {
                return size() > MAX_MAP_ENTRIES;
            }
        };

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<DObjectRef>(ScrollPolicy.AUTO) {

            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<DObjectRef>> entries) {
                /*
                 * restore selections
                 */
                if (entries != null && !entries.isEmpty()) {
                    Set<DObjectRef> savedSelections = _selectionMap.get(_parent);
                    DObjectRef selected = _selectedMap.get(_parent);
                    if (savedSelections == null || savedSelections.isEmpty()) {
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
                            if (savedSelections.contains(o)) {
                                select(i, true, false);
                                if (selected == null && selectedIdx < 0) {
                                    selectedIdx = i;
                                }
                            }
                        }
                        if (toSelectIdx >= 0) {
                            clearSelections();
                            select(toSelectIdx);
                        } else if (selectedIdx >= 0) {
                            select(selectedIdx);
                        } else {
                            select(0);
                        }
                    }
                }
                _toSelect = null;
            }

            @Override
            public void select(int i, boolean multiSelect, boolean fireEvent) {
                super.select(i, multiSelect, fireEvent);
                DObjectRef o = _childrenInCurrentPage.get(i);
                RowCheckBox rcb = (RowCheckBox) rowFor(o).cell(0).widget();
                if (!rcb.checked()) {
                    rcb.setChecked(true);
                }
            }

            @Override
            public void deselect(int i, boolean fireEvent) {
                super.deselect(i, fireEvent);
                DObjectRef o = _childrenInCurrentPage.get(i);
                RowCheckBox rcb = (RowCheckBox) rowFor(o).cell(0).widget();
                if (rcb.checked()) {
                    rcb.setChecked(false);
                }
            }

            @Override
            public void deselectAll(boolean fireEvent) {
                super.deselectAll(fireEvent);
                if (_childrenInCurrentPage != null) {
                    for (DObjectRef o : _childrenInCurrentPage) {
                        RowCheckBox rcb = (RowCheckBox) rowFor(o).cell(0).widget();
                        if (rcb.checked()) {
                            rcb.setChecked(false);
                        }
                    }
                }
            }

        };
        _list.setObjectRegistry(DObjectGUIRegistry.get());
        _list.fitToParent();
        _list.setMinRowHeight(MIN_ROW_HEIGHT);

        // TODO: check if it work as expected
        _list.setClearSelectionOnRefresh(false);

        _list.setMultiSelect(true);

        _list.setSelectionHandler(new SelectionHandler<DObjectRef>() {

            @Override
            public void selected(DObjectRef selected) {

                System.out.println("Selected: " + selected.citeableId());

                _selectedMap.put(_parent, selected);

                Set<DObjectRef> selections = _selectionMap.get(_parent);
                if (selections == null) {
                    selections = new TreeSet<DObjectRef>();
                    _selectionMap.put(_parent, selections);
                }
                selections.add(selected);

                RowCheckBox rcb = (RowCheckBox) _list.rowFor(selected).cell(0).widget();
                if (!rcb.checked()) {
                    rcb.setChecked(true);
                }

                if (_list.selections().size() == _childrenInCurrentPage.size()) {
                    _headerCheckBox.setState(HeaderCheckBox.State.ALL);
                } else {
                    _headerCheckBox.setState(HeaderCheckBox.State.PARTIAL);
                }

                notifyOfSelectionInPage(selected);
            }

            @Override
            public void deselected(DObjectRef deselected) {

                System.out.println("Deselected: " + deselected.citeableId());

                _selectedMap.remove(_parent);

                Set<DObjectRef> selections = _selectionMap.get(_parent);
                if (selections != null) {
                    selections.remove(deselected);
                }

                RowCheckBox rcb = (RowCheckBox) _list.rowFor(deselected).cell(0).widget();
                if (rcb.checked()) {
                    rcb.setChecked(false);
                }

                if (_list.haveSelections()) {
                    _headerCheckBox.setState(HeaderCheckBox.State.PARTIAL);
                } else {
                    _headerCheckBox.setState(HeaderCheckBox.State.NONE);
                }

                notifyOfDeselectionInPage(deselected);
            }
        });
        _list.setEmptyMessage("");
        _list.setLoadingMessage("");
        _list.setCursorSize(_pageSize);

        _headerCheckBox = new HeaderCheckBox(HeaderCheckBox.State.NONE, false);
        _headerCheckBox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HeaderCheckBox.State state = _headerCheckBox.state();
                switch (state) {
                case NONE:
                case PARTIAL:
                    if (_childrenInCurrentPage != null) {
                        DObjectRef selected = selected();
                        int size = _childrenInCurrentPage.size();
                        for (int i = 0; i < size; i++) {
                            _list.select(i);
                        }
                        if (selected != null) {
                            _list.select(selected);
                        }
                        _headerCheckBox.setState(HeaderCheckBox.State.ALL);
                    } else {
                        _headerCheckBox.setState(HeaderCheckBox.State.NONE);
                    }
                    break;
                default:
                    if (_list.haveSelections()) {
                        _list.deselectAll(true);
                    }
                    break;
                }

            }
        });

        ListGridColumn<DObjectRef> checkBoxColumn = new ListGridColumn<DObjectRef>("selected", _headerCheckBox, null,
                new WidgetFormatter<DObjectRef, Boolean>() {

                    @Override
                    public BaseWidget format(DObjectRef o, Boolean selected) {
                        final RowCheckBox rcb = new RowCheckBox(selected == null ? false : selected);
                        rcb.addClickHandler(new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                                if (rcb.checked()) {
                                    rcb.setChecked(false);
                                    _list.deselect(o);
                                } else {
                                    rcb.setChecked(true);
                                    _list.select(o);
                                }
                            }
                        });
                        return rcb;
                    }
                }, "");
        checkBoxColumn.setWidth(MIN_ROW_HEIGHT);
        _list.add(checkBoxColumn);

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

        _list.setRowDoubleClickHandler(new ListGridRowDoubleClickHandler<DObjectRef>() {

            @Override
            public void doubleClicked(DObjectRef o, DoubleClickEvent event) {
                if (!o.isDataset() && o.numberOfChildren() != 0) {
                    setParentObject(o);
                }
            }
        });

        _list.setRowEnterHandler(new ListGridRowEnterHandler<DObjectRef>() {

            @Override
            public void onEnter(DObjectRef o) {
                if (!o.isDataset() && o.numberOfChildren() != 0) {
                    setParentObject(o);
                }
            }
        });

        _vp.add(_list);

        _pc = new PagingControl(_pageSize);
        _pc.addPagingListener(this);
        _vp.add(_pc);

        initWidget(_vp);

        gotoOffset(0);
    }

    DObjectRef parentObject() {
        return _parent;
    }

    public void setParentObject(DObjectRef parent) {
        if (!ObjectUtil.equals(parent, _parent)) {
            _parent = parent;
            DObjectRef selected = _selectedMap.get(parent);
            if (selected != null) {
                seekTo(selected);
            } else {
                gotoOffset(0);
            }
            notifyOfParentUpdate(_parent);
        }
    }

    DObjectChildrenRef childrenRef() {
        DObjectChildrenRef c = _childrenMap.get(_parent);
        if (c == null) {
            c = new DObjectChildrenRef(_parent);
            c.setPageSize(_pageSize);
            _childrenMap.put(_parent, c);
        }
        return c;
    }

    Set<DObjectRef> selections() {
        return _selectionMap.get(_parent);
    }

    DObjectRef selected() {
        return _selectedMap.get(_parent);
    }

    public int pageSize() {
        return _pageSize;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
        Collection<DObjectChildrenRef> cs = _childrenMap.values();
        if (cs != null) {
            for (DObjectChildrenRef c : cs) {
                c.setPageSize(pageSize);
            }
        }
    }

    @Override
    public void gotoOffset(final long offset) {
        final DObjectChildrenRef childrenRef = childrenRef();
        childrenRef.resolve(offset, offset + pageSize(), new CollectionResolveHandler<DObjectRef>() {
            @Override
            public void resolved(List<DObjectRef> cos) throws Throwable {
                long total = childrenRef.totalNumberOfMembers();
                _headerCheckBox.setEnabled(total > 0);
                _pc.setOffset(offset, total, true);
                _childrenInCurrentPage = cos;
                List<ListGridEntry<DObjectRef>> entries = null;
                if (_childrenInCurrentPage != null && !_childrenInCurrentPage.isEmpty()) {
                    entries = new ArrayList<ListGridEntry<DObjectRef>>();
                    for (DObjectRef co : _childrenInCurrentPage) {
                        ListGridEntry<DObjectRef> entry = new ListGridEntry<DObjectRef>(co);
                        entry.set("cid", co.citeableId());
                        entry.set("name", co.name());
                        entries.add(entry);
                    }
                }
                _list.setData(entries);
            }
        });

    }

    protected void seekTo(final DObjectRef object) {
        final DObjectChildrenRef childrenRef = childrenRef();
        new DObjectChildCursorFromGet(object, childrenRef).send(new ObjectMessageResponse<Long>() {

            @Override
            public void responded(Long idx) {
                if (idx != null && idx > 0) {
                    _toSelect = object;
                    gotoOffset(idx - 1);
                } else {
                    gotoOffset(0);
                }
            }
        });
    }

    public void seekTo(DObjectRef parent, DObjectRef object) {
        DObjectRef selected = _selectedMap.get(_parent);
        if (ObjectUtil.equals(selected, object)) {
            return;
        }
        if (!ObjectUtil.equals(parent, _parent)) {
            _parent = parent;
        }
        seekTo(object);
    }

    public void addSelectionHandler(SelectionHandler<DObjectRef> sh) {
        if (_shs == null) {
            _shs = new ArrayList<SelectionHandler<DObjectRef>>();
        }
        _shs.add(sh);
    }

    public void removeSelectionHandler(SelectionHandler<DObjectRef> sh) {
        if (_shs != null) {
            _shs.remove(sh);
        }
    }

    public void addParentUpdateListener(ParentUpdateListener pul) {
        if (_puls == null) {
            _puls = new ArrayList<ParentUpdateListener>();
        }
        _puls.add(pul);
    }

    public void removeParentUpdateListener(ParentUpdateListener pul) {
        if (_puls != null) {
            _puls.remove(pul);
        }
    }

    private void notifyOfParentUpdate(DObjectRef parent) {
        if (_puls != null) {
            for (ParentUpdateListener pul : _puls) {
                pul.parentUpdated(parent);
            }
        }
    }

    private void notifyOfSelectionInPage(DObjectRef o) {
        if (_shs != null) {
            for (SelectionHandler<DObjectRef> sh : _shs) {
                sh.selected(o);
            }
        }
    }

    private void notifyOfDeselectionInPage(DObjectRef o) {
        if (_shs != null) {
            for (SelectionHandler<DObjectRef> sh : _shs) {
                sh.deselected(o);
            }
        }
    }

    private static class HeaderCheckBox extends ContainerWidget {

        static final arc.gui.image.Image IMG_UNCHECKED = new arc.gui.image.Image(
                Resource.INSTANCE.checkbox_unchecked_24().getSafeUri().asString(), 12, 12);
        static final arc.gui.image.Image IMG_INDETERMINATE = new arc.gui.image.Image(
                Resource.INSTANCE.checkbox_indeterminate_24().getSafeUri().asString(), 12, 12);
        static final arc.gui.image.Image IMG_CHECKED = new arc.gui.image.Image(
                Resource.INSTANCE.checkbox_checked_24().getSafeUri().asString(), 12, 12);

        static enum State {
            NONE, PARTIAL, ALL
        }

        private AbsolutePanel _ap;
        private arc.gui.gwt.widget.image.Image _img;

        private State _state;
        private boolean _enabled;

        HeaderCheckBox(State state, boolean enabled) {
            _state = state;
            _enabled = enabled;
            _ap = new AbsolutePanel();
            _ap.setWidth100();
            _ap.setHeight(ListGridHeader.HEIGHT);
            _ap.setCursor(_enabled ? Cursor.POINTER : Cursor.DEFAULT);

            _img = createImage(_state);
            _ap.add(_img);

            initWidget(_ap);
        }

        public void setEnabled(boolean enabled) {
            if (enabled != _enabled) {
                _enabled = enabled;
                _ap.setCursor(_enabled ? Cursor.POINTER : Cursor.DEFAULT);
            }
        }

        public void setState(State state) {
            if (state != _state) {
                _state = state;
                _ap.remove(_img);
                _img = createImage(_state);
                _ap.add(_img);
            }
        }

        public State state() {
            return _state;
        }

        private static arc.gui.gwt.widget.image.Image createImage(State state) {
            arc.gui.gwt.widget.image.Image image;
            switch (state) {
            case ALL:
                image = new arc.gui.gwt.widget.image.Image(IMG_CHECKED);
                break;
            case PARTIAL:
                image = new arc.gui.gwt.widget.image.Image(IMG_INDETERMINATE);
                break;
            default:
                image = new arc.gui.gwt.widget.image.Image(IMG_UNCHECKED);
                break;
            }
            image.setPosition(Position.ABSOLUTE);
            image.element().getStyle().setProperty("margin", "auto");
            image.setTop(0);
            image.setBottom(0);
            image.setLeft(0);
            image.setRight(0);
            return image;
        }

    }

    private static class RowCheckBox extends ContainerWidget {

        static final arc.gui.image.Image IMG_CIRCLE = new arc.gui.image.Image(
                Resource.INSTANCE.checkbox_unchecked_24().getSafeUri().asString(), 12, 12);
        static final arc.gui.image.Image IMG_TICK = new arc.gui.image.Image(
                Resource.INSTANCE.checkbox_checked_24().getSafeUri().asString(), 12, 12);

        private AbsolutePanel _ap;
        private arc.gui.gwt.widget.image.Image _img;

        RowCheckBox(boolean checked) {
            _ap = new AbsolutePanel();
            _ap.setWidth100();
            _ap.setHeight(MIN_ROW_HEIGHT);
            _ap.setCursor(Cursor.POINTER);

            _img = new arc.gui.gwt.widget.image.Image(IMG_TICK);
            _img.setDisabledImage(IMG_CIRCLE);
            _img.setPosition(Position.ABSOLUTE);
            _img.element().getStyle().setProperty("margin", "auto");
            _img.setTop(0);
            _img.setBottom(0);
            _img.setLeft(0);
            _img.setRight(0);
            _img.setEnabled(checked);
            _ap.add(_img);
            initWidget(_ap);
        }

        public boolean checked() {
            return _img.enabled();
        }

        public void setChecked(boolean checked) {
            _img.setEnabled(checked);
        }

    }

}

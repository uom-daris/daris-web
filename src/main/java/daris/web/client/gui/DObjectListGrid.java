package daris.web.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.DoubleClickEvent;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
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
    private LinkedHashMap<DObjectRef, DObjectRef> _selectedMap;
    private List<DObjectRef> _childrenInCurrentPage;

    private DObjectRef _toSelect;

    private VerticalPanel _vp;

    private ListGrid<DObjectRef> _list;
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
                    DObjectRef selected = _selectedMap.get(_parent);
                    if (selected == null) {
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
                        if (toSelectIdx >= 0) {
                            clearSelections();
                            select(toSelectIdx);
                        } else if (selectedIdx >= 0) {
                            select(selectedIdx);
                        } else {
                            select(0);
                        }
                    }
                } else {
                    notifyOfDeselectionInPage(_parent);
                }
                _toSelect = null;
            }

        };
        _list.setObjectRegistry(DObjectGUIRegistry.get());
        _list.fitToParent();
        _list.setMinRowHeight(MIN_ROW_HEIGHT);

        _list.setClearSelectionOnRefresh(false);

        _list.setMultiSelect(false);

        _list.setSelectionHandler(new SelectionHandler<DObjectRef>() {

            @Override
            public void selected(DObjectRef selected) {

                _selectedMap.put(_parent, selected);

                notifyOfSelectionInPage(selected);
            }

            @Override
            public void deselected(DObjectRef deselected) {

                notifyOfDeselectionInPage(deselected);
            }
        });
        _list.setEmptyMessage("");
        _list.setLoadingMessage("");
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
                        if (nbc != null && nbc >= 0) {
                            HTML html = new HTML(Integer.toString(nbc));
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
                        return null;
                    }
                }).setWidth(120);

        _list.setRowDoubleClickHandler(new ListGridRowDoubleClickHandler<DObjectRef>() {

            @Override
            public void doubleClicked(DObjectRef o, DoubleClickEvent event) {
                if (!o.isDataset()) {
                    _selectedMap.put(_parent, o);
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
            _list.clearSelections(true);
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

    public static final arc.gui.image.Image IMG_FOLDER = new arc.gui.image.Image(
            Resource.INSTANCE.folder_32().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image IMG_FOLDER_ENTER = new arc.gui.image.Image(
            Resource.INSTANCE.folder_enter_32().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image IMG_DOCUMENT = new arc.gui.image.Image(
            Resource.INSTANCE.document_32().getSafeUri().asString(), 16, 16);

    private static class ObjectIcon extends ContainerWidget {

        private AbsolutePanel _ap;
        private arc.gui.gwt.widget.image.Image _img;

        ObjectIcon(DObjectRef o) {

            _ap = new AbsolutePanel();
            _ap.setWidth100();
            _ap.setHeight(MIN_ROW_HEIGHT);
            _ap.setCursor(Cursor.POINTER);

            if (o.isDataset()) {
                _img = new arc.gui.gwt.widget.image.Image(IMG_DOCUMENT);
            } else {
                _img = new arc.gui.gwt.widget.image.Image(IMG_FOLDER_ENTER);
                if (o.numberOfChildren() > 0) {
                    String childrenType = o.childTypeName();
                    if (childrenType != null) {
                        if (childrenType.endsWith("y")) {
                            childrenType = childrenType.substring(0, childrenType.length() - 1) + "ies";
                        } else {
                            childrenType = childrenType + "s";
                        }
                    }
                    _img.setTitle("contains " + o.numberOfChildren() + " " + childrenType + ". Double-click to open.");
                } else {
                    _img.setTitle("may contain " + o.childTypeName() + ". Double-click to open.");
                }
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

    }

}

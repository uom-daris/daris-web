package daris.web.client.gui;

import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import arc.gui.gwt.object.ObjectDetailedView;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.mf.client.plugin.Plugin;
import arc.mf.client.util.ListUtil;
import arc.mf.client.util.ObjectUtil;
import arc.mf.event.Filter;
import arc.mf.event.Subscriber;
import arc.mf.event.SystemEvent;
import arc.mf.event.SystemEventChannel;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.Session;
import daris.web.client.gui.DObjectListGrid.ParentUpdateListener;
import daris.web.client.gui.object.DObjectGUI;
import daris.web.client.gui.widget.DMenuButton;
import daris.web.client.gui.widget.DMenuButtonBar;
import daris.web.client.gui.widget.DNavButtonBar;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectPath;
import daris.web.client.model.object.DObjectPathRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.event.DObjectEvent;
import daris.web.client.util.ObjectUtils;
import daris.web.client.util.StringUtils;

public class DObjectExplorer extends ContainerWidget implements Subscriber {

    public static final arc.gui.image.Image ICON_DARIS = new arc.gui.image.Image(
            Resource.INSTANCE.daris16().getSafeUri().asString(), 14, 14);

    public static final arc.gui.image.Image ICON_ACTION = new arc.gui.image.Image(
            Resource.INSTANCE.launch16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_ABOUT = new arc.gui.image.Image(
            Resource.INSTANCE.about16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_PREFERENCES = new arc.gui.image.Image(
            Resource.INSTANCE.settings16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_EXIT = new arc.gui.image.Image(
            Resource.INSTANCE.exit16().getSafeUri().asString(), 16, 16);

    private VerticalPanel _vp;

    private DMenuButtonBar _menuBar;

    private DNavButtonBar _navBar;

    private DObjectListGrid _list;
    private ObjectDetailedView _dv;

    private DMenuButton _actionMenuButton;

    private DObjectExplorer() {
        _vp = new VerticalPanel();
        _vp.fitToParent();

        /*
         * menu bar
         */
        _menuBar = new DMenuButtonBar();
        _vp.add(_menuBar);
        initMenus();

        /*
         * Nav bar
         */
        _navBar = new DNavButtonBar(this);
        _vp.add(_navBar);

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(5);
        hsp.fitToParent();
        _vp.add(hsp);

        _list = new DObjectListGrid(null);
        _list.setPreferredWidth(0.5);
        _list.setHeight100();
        hsp.add(_list);

        _dv = new ObjectDetailedView();
        _dv.fitToParent();
        _dv.setObjectRegistry(DObjectGUIRegistry.get());
        hsp.add(_dv);

        _list.addSelectionHandler(new SelectionHandler<DObjectRef>() {

            @Override
            public void selected(DObjectRef o) {
                updateHistoryToken(o);
                _dv.loadAndDisplayObject(o);
                updateMenus();
            }

            @Override
            public void deselected(DObjectRef o) {
                _dv.clear(o);
                updateMenus();
            }
        });

        _list.addParentUpdateListener(new ParentUpdateListener() {

            @Override
            public void parentUpdated(DObjectRef parent) {
                _navBar.setBusyLoading();
                resolveObjectPath(parent, path -> {
                    if (path == null) {
                        _navBar.update(null);
                    } else {
                        _navBar.update(path.list(true, false));
                        updateHistoryToken(path.object(), path.child());
                    }
                });
            }
        });

        initWidget(_vp);

        if (Plugin.isStandaloneApplication()) {
            History.addValueChangeHandler(e -> {
                String token = e.getValue();
                if (token != null && token.startsWith("list_")) {
                    String cid = token.substring(5);
                    list(cid, false);
                } else if (token != null && token.startsWith("view_")) {
                    String cid = token.substring(5);
                    view(cid, false);
                } else {
                    if (!"list".equals(token)) {
                        History.replaceItem("list", false);
                    }
                    list();
                }
            });
        }

        /*
         * subscribe to system events.
         */
        SystemEventChannel.add(this);

        // Fit to browser window
        com.google.gwt.user.client.Window.addResizeHandler(new com.google.gwt.event.logical.shared.ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                int dw = event.getWidth() - DObjectExplorer.this.width();
                int dh = event.getHeight() - DObjectExplorer.this.height();
                DObjectExplorer.this.resizeBy(dw, dh);
            }
        });
    }

    private void initMenus() {
        /*
         * daris menu
         */
        Menu darisMenu = new Menu();
        darisMenu.add(new ActionEntry(ICON_ABOUT, "About DaRIS", () -> {
            new AboutDialog().show(window());
        }));
        darisMenu.addSeparator();
        darisMenu.add(new ActionEntry(ICON_PREFERENCES, "Preferences...", null));
        darisMenu.addSeparator();
        darisMenu.add(new ActionEntry(ICON_EXIT, "Log Out", () -> {
            Session.logoff(true);
        }));

        _menuBar.addMenuButton("DaRIS", ICON_DARIS, darisMenu);

        /*
         * action menu
         */
        _actionMenuButton = _menuBar.addMenuButton("Action", ICON_ACTION);

    }

    private void updateMenus() {
        final DObjectRef o = _list.selected();
        if (o == null) {
            _actionMenuButton.setMenu(null);
        } else {
            Menu menu = DObjectGUI.INSTANCE.actionMenu(window(), o, new SelectedObjectSet() {

                @Override
                public List<?> selections() {
                    return ListUtil.list(_list.selected());
                }
            }, false);
            _actionMenuButton.setMenu(menu);
        }
    }

    public void view(String cid, boolean refresh) {
        if (refresh) {
            // _list.childrenRef().reset();
        }
        resolveObjectPath(cid, path -> {
            display(path.parents(), path.object(), refresh);
        });
    }

    public void view(DObjectRef o, boolean refresh) {
        view(o.citeableId(), refresh);
    }

    /**
     * List projects.
     */
    public void list() {
        list((String) null, false);
    }

    public void list(DObjectRef parent, boolean refresh) {
        list(parent == null ? null : parent.citeableId(), refresh);
    }

    public void list(String parentCid, boolean refresh) {
        _navBar.setBusyLoading();
        _list.setBusyLoading();
        resolveObjectPath(parentCid, path -> {
            if (path == null) {
                _navBar.update(null);
                _list.setParentObject(null, refresh);
            } else {
                display(path.list(true, false), path.child(), refresh);
            }
        });
    }

    private void resolveObjectPath(DObjectRef o, ObjectResolveHandler<DObjectPath> rh) {
        resolveObjectPath(o == null ? null : o.citeableId(), rh);
    }

    private void resolveObjectPath(String cid, ObjectResolveHandler<DObjectPath> rh) {
        if (cid == null) {
            if (rh != null) {
                rh.resolved(null);
            }
            return;
        }
        new DObjectPathRef(cid).resolve(path -> {
            if (rh != null) {
                rh.resolved(path);
            }
        });
    }

    public void refreshDetailedView() {
        DObjectRef o = _list.selected();
        o.reset();
        _dv.reloadAndDisplayObject(o);
    }

    private void display(List<DObjectRef> parents, DObjectRef object, boolean refresh) {
        _navBar.update(parents);
        DObjectRef directParent = (parents == null || parents.isEmpty()) ? null : parents.get(parents.size() - 1);
        if (object != null) {
            _list.seekTo(directParent, object, refresh);
        } else {
            _list.setParentObject(directParent, refresh);
            _list.gotoOffset(0);
        }
        if (Plugin.isStandaloneApplication()) {
            // update history token if needed
            updateHistoryToken(directParent, object);
        }
    }

    private static String historyTokenFor(DObjectRef parent, DObjectRef object) {
        if (object == null) {
            if (parent == null) {
                return "list";
            } else {
                return "list_" + parent.citeableId();
            }
        } else {
            return "view_" + object.citeableId();
        }
    }

    private static String historyTokenFor(DObjectRef object) {
        if (object == null || object.isProject()) {
            return historyTokenFor(null, object);
        }
        DObjectRef parent = new DObjectRef(CiteableIdUtils.parent(object.citeableId()), -1);
        return historyTokenFor(parent, object);
    }

    private static void updateHistoryToken(String token) {
        if (!ObjectUtil.equals(token, History.getToken())) {
            History.newItem(token, false);
        }
    }

    private static void updateHistoryToken(DObjectRef parent, DObjectRef object) {
        String token = historyTokenFor(parent, object);
        updateHistoryToken(token);
        updateWindowTitle(object);
    }

    private static void updateHistoryToken(DObjectRef object) {
        String token = historyTokenFor(object);
        updateHistoryToken(token);
        updateWindowTitle(object);
    }

    private static void updateWindowTitle(DObjectRef object) {
        String title = windowTitleFor(object);
        if (!ObjectUtils.equals(Window.getTitle(), title)) {
            Window.setTitle(title);
        }
    }

    private static String windowTitleFor(DObjectRef object) {
        if (object != null) {
            StringBuilder sb = new StringBuilder("DaRIS ");
            sb.append(StringUtils.upperCaseFirst(object.referentTypeName())).append(" ");
            sb.append(object.citeableId());
            if (object.name() != null) {
                sb.append(": ").append(object.name());
            } else if (object.referent() != null && object.referent().name() != null) {
                sb.append(": ").append(object.referent().name());
            }
            return sb.toString();
        }
        return "Distributed and Reflective Informatics System (DaRIS)";
    }

    private static DObjectExplorer _instance;

    public static DObjectExplorer get() {
        if (_instance == null) {
            _instance = new DObjectExplorer();
        }
        return _instance;
    }

    @Override
    public List<Filter> systemEventFilters() {
        return ListUtil.list(new Filter(DObjectEvent.SYSTEM_EVENT_NAME, null));
    }

    private void handleEvent(DObjectEvent de) {
        DObjectEvent.Action action = de.action();
        String ecid = de.citeableId();

        DObjectRef so = _list.selected();
        DObjectRef po = _list.parentObject();

        switch (action) {
        case MODIFY:
            if (de.matchesObject(so)) {
                if (_list.isInCurrentPage(so)) {
                    _list.refreshRow(so);
                }
                _dv.reloadAndDisplayObject(so);
            } else if (de.isParentOf(so)) {
                resolveObjectPath(_list.parentObject(), path -> {
                    _navBar.update(path.list(true, false));
                });
            }
            break;
        case CREATE:
            if (po == null) {
                if (CiteableIdUtils.isProject(ecid)) {
                    if (!_list.isCurrentPageFull()) {
                        list((DObjectRef) null, true);
                    }
                } else if (CiteableIdUtils.isSubject(ecid)) {
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (_list.isInCurrentPage(o)) {
                        _list.refreshRow(o);
                    }
                }
            } else {
                if (de.isDirectChildOf(po)) {
                    if (!_list.isCurrentPageFull()) {
                        if (so != null) {
                            view(so, true);
                        } else {
                            list(po, true);
                        }
                    }
                } else if (de.isGrandChildOf(po)) {
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (_list.isInCurrentPage(o)) {
                        _list.refreshRow(o);
                    }
                }
            }
            break;
        case DESTROY:
            if (po == null) {
                if (CiteableIdUtils.isProject(ecid)) {
                    _list.collectionRemoved(ecid);
                    if (_list.isInCurrentPage(ecid)) {
                        list((DObjectRef) null, true);
                    }
                } else if (CiteableIdUtils.isSubject(ecid)) {
                    _list.collectionRemoved(ecid);
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (_list.isInCurrentPage(o)) {
                        _list.refreshRow(o);
                    }
                }
            } else {
                if (de.isDirectChildOf(so)) {
                    _list.collectionRemoved(ecid);
                    if (_list.isInCurrentPage(so)) {
                        _list.refreshRow(so);
                    }
                } else if (de.isGrandChildOf(po)) {
                    _list.collectionRemoved(ecid);
                    DObjectRef o = new DObjectRef(CiteableIdUtils.parent(ecid), -1);
                    if (_list.isInCurrentPage(o)) {
                        _list.refreshRow(o);
                    }
                } else {
                    if (de.isDirectChildOf(po)) {
                        if (de.matchesObject(so)) {
                            _list.collectionRemoved(ecid);
                        }
                        list(po, true);
                    } else if (de.matchesObject(po) || de.isParentOf(po)) {
                        _list.collectionRemoved(ecid);
                        resolveNearestExistingParent(ecid, pid -> {
                            list(pid, true);
                        });
                    }
                }
            }
            break;
        default:
            break;
        }

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
                _list.collectionRemoved(pid);
                resolveNearestExistingParent(pid, rh);
            } else {
                if (rh != null) {
                    rh.resolved(pid);
                }
            }
        });
    }

}

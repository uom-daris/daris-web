package daris.web.client.gui;

import java.util.List;

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
import arc.mf.client.util.DynamicBoolean;
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
                if (parent == null) {
                    _navBar.update(null);
                    return;
                }
                new DObjectPathRef(parent).resolve(new ObjectResolveHandler<DObjectPath>() {

                    @Override
                    public void resolved(DObjectPath path) {
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
                    list(cid);
                } else if (token != null && token.startsWith("view_")) {
                    String cid = token.substring(5);
                    view(cid);
                } else {
                    if (!"list".equals(token)) {
                        History.replaceItem("list", false);
                    }
                    list();
                }
            });
        }

        SystemEventChannel.add(this);
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

    public void view(String cid) {
        new DObjectPathRef(cid).resolve(new ObjectResolveHandler<DObjectPath>() {

            @Override
            public void resolved(DObjectPath path) {
                display(path.parents(), path.object());
            }
        });
    }

    public void view(DObjectRef o) {
        view(o.citeableId());
    }

    /**
     * List projects.
     */
    public void list() {
        list((String) null);
    }

    public void list(DObjectRef parent) {
        list(parent == null ? null : parent.citeableId());
    }

    public void list(String parentCid) {
        _navBar.setBusyLoading();
        _list.setBusyLoading();
        if (parentCid == null) {
            _navBar.update(null);
            _list.setParentObject(null);
            return;
        }
        new DObjectPathRef(parentCid).resolve(new ObjectResolveHandler<DObjectPath>() {

            @Override
            public void resolved(DObjectPath path) {
                display(path.list(true, false), path.child());
            }
        });
    }

    public void refreshDetailedView() {
        DObjectRef o = _list.selected();
        o.reset();
        _dv.reloadAndDisplayObject(o);
    }

    private void display(List<DObjectRef> parents, DObjectRef object) {
        _navBar.update(parents);
        DObjectRef directParent = (parents == null || parents.isEmpty()) ? null : parents.get(parents.size() - 1);
        _list.seekTo(directParent, object);
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
        DObjectRef o = _list.selected();
        if (o == null) {
            return null;
        }
        return ListUtil.list(new Filter("pssd-object", o.citeableId(), DynamicBoolean.TRUE));
    }

    @Override
    public void process(SystemEvent se) {
        DObjectEvent de = (DObjectEvent) se;
        DObjectRef selected = _list.selected();
        if (de.action() == DObjectEvent.Action.MODIFY) {
            if (de.matchesObject(_list.selected())) {
                _list.refreshRow(selected);
                _dv.reloadAndDisplayObject(selected);
            }
        } else if (de.action() == DObjectEvent.Action.CREATE) {
            // TODO
        } else if (de.action() == DObjectEvent.Action.DESTROY) {
            // TODO
        } else {
            // TODO
        }
    }

}

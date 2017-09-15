package daris.web.client.gui.explorer;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.Menu;
import arc.mf.client.plugin.Plugin;
import arc.mf.client.util.ObjectUtil;
import arc.mf.session.Session;
import daris.web.client.gui.AboutDialog;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.Resource;
import daris.web.client.gui.explorer.event.ObjectSelectionEvent;
import daris.web.client.gui.explorer.event.ObjectSelectionEventHandler;
import daris.web.client.gui.explorer.event.ObjectSelectionEventManager;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.gui.widget.DMenuButtonBar;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.ObjectUtils;
import daris.web.client.util.StringUtils;

public class Explorer extends ContainerWidget implements ObjectSelectionEventHandler {

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
    private NavLinkBar _navBar;
    private ListView _list;
    private DetailedView _dv;
    private DObjectMenu _actionMenu;

    private Explorer() {

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
        _navBar = new NavLinkBar();
        _vp.add(_navBar);

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(5);
        hsp.fitToParent();
        _vp.add(hsp);

        _list = new ListView(null);
        _list.setPreferredWidth(0.5);
        _list.setHeight100();
        hsp.add(_list);

        _dv = new DetailedView();
        _dv.fitToParent();
        _dv.setObjectRegistry(DObjectGUIRegistry.get());
        _dv.setDisplayContextMenu(false);
        hsp.add(_dv);

        initWidget(_vp);

        if (Plugin.isStandaloneApplication()) {
            History.addValueChangeHandler(e -> {
                String token = e.getValue();
                if (token != null && token.startsWith("list_")) {
                    String cid = token.substring(5);
                    DObjectRef po = cid == null ? null : new DObjectRef(cid, -1);
                    _navBar.update(po);
                    _actionMenu.setObject(null).setParent(po);
                    ObjectSelectionEventManager.fireEvent(Explorer.this, po, true);
                } else if (token != null && token.startsWith("view_")) {
                    String cid = token.substring(5);
                    DObjectRef o = cid == null ? null : new DObjectRef(cid, -1);
                    DObjectRef po = o.parent();
                    _navBar.update(po);
                    _actionMenu.setObject(o).setParent(po);
                    ObjectSelectionEventManager.fireEvent(Explorer.this, o, false);
                } else {
                    if (!"list".equals(token)) {
                        History.replaceItem("list", false);
                    }
                    _navBar.update(null);
                    _actionMenu.setObject(null).setParent(null);
                    ObjectSelectionEventManager.fireEvent(Explorer.this, null, true);
                }
            });
        }

        // Fit to browser window
        com.google.gwt.user.client.Window.addResizeHandler(new com.google.gwt.event.logical.shared.ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                int dw = event.getWidth() - Explorer.this.width();
                int dh = event.getHeight() - Explorer.this.height();
                Explorer.this.resizeBy(dw, dh);
            }
        });

        ObjectSelectionEventManager.subscribe(this);
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
        _actionMenu = new DObjectMenu(null, null, window());
        _menuBar.addMenuButton("Action", ICON_ACTION, _actionMenu);

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

    private static Explorer _instance;

    public static Explorer get() {
        if (_instance == null) {
            _instance = new Explorer();
        }
        return _instance;
    }

    @Override
    public void handleEvent(ObjectSelectionEvent event) {
        if (!ObjectUtil.equals(event.source(), Explorer.this)) {
            if (event.isParent()) {
                _actionMenu.setParent(event.object());
                updateHistoryToken(event.object(), null);
                updateWindowTitle(null);
            } else {
                if (event.object() == null) {
                    _dv.clear();
                } else {
                    _dv.setForEdit(false);
                    _dv.reloadAndDisplayObject(event.object());
                }
                _actionMenu.setObject(event.object());
                updateHistoryToken(event.object());
                updateWindowTitle(event.object());
            }
        }
    }

}
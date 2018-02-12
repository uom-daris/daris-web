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
import arc.mf.client.util.Action;
import arc.mf.client.util.ObjectUtil;
import arc.mf.session.Session;
import daris.web.client.gui.AboutDialog;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.Resource;
import daris.web.client.gui.background.BackgroundServiceManager;
import daris.web.client.gui.object.imports.FileUploadTaskManager;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.gui.query.DObjectFinder;
import daris.web.client.gui.query.DicomDatasetFinder;
import daris.web.client.gui.query.DicomStudyFinder;
import daris.web.client.gui.query.DicomSubjectFinder;
import daris.web.client.gui.shoppingcart.ShoppingCartManagerDialog;
import daris.web.client.gui.widget.MenuButtonBar;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.ObjectUtils;
import daris.web.client.util.StringUtils;

public class Explorer extends ContainerWidget {

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

    public static final arc.gui.image.Image ICON_VIEW = new arc.gui.image.Image(
            Resource.INSTANCE.faDesktopMonitor16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_BACKGROUND = new arc.gui.image.Image(
            Resource.INSTANCE.tasks16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_UPLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.upload16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartColor16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_FIND = new arc.gui.image.Image(
            Resource.INSTANCE.search16().getSafeUri().asString(), 16, 16);

    private VerticalPanel _vp;
    private MenuButtonBar _menuBar;
    private ContextLinkBar _contextLinks;
    private ListView _list;
    private DetailedView _dv;
    private DObjectMenu _actionMenu;

    private Explorer() {

        _vp = new VerticalPanel();
        _vp.fitToParent();

        /*
         * menu bar
         */
        _menuBar = new MenuButtonBar();
        _vp.add(_menuBar);
        initMenus();

        /*
         * Nav bar
         */
        _contextLinks = new ContextLinkBar();
        _contextLinks.addListener(new ContextLinkBar.Listener() {
            @Override
            public void selected(DObjectRef o) {
                if (_list != null) {
                    _list.open(o);
                }
                if (_actionMenu != null) {
                    _actionMenu.setObject(null).setParent(o);
                }
            }
        });
        _vp.add(_contextLinks);

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(5);
        hsp.fitToParent();
        _vp.add(hsp);

        _list = new ListView(null);
        _list.setPreferredWidth(0.5);
        _list.setHeight100();
        _list.addListener(new ContextView.Listener() {
            @Override
            public void opened(DObjectRef o) {
                if (_contextLinks != null) {
                    _contextLinks.update(o);
                }
                if (_actionMenu != null) {
                    _actionMenu.setParent(o).setObject(null);
                }
                updateHistoryToken(o, null);
                updateWindowTitle(null);
            }

            @Override
            public void selected(DObjectRef o) {
                if (_contextLinks != null) {
                    _contextLinks.update(o == null ? null : o.parent());
                }
                if (_actionMenu != null) {
                    _actionMenu.setParent(o.parent()).setObject(o);
                }
                if (_dv != null) {
                    _dv.setForEdit(false);
                    _dv.loadAndDisplayObject(o);
                }
                updateHistoryToken(o);
                updateWindowTitle(o);
            }

            @Override
            public void deselected(DObjectRef o) {
                if (_dv != null) {
                    _dv.clear(o);
                }
            }

            @Override
            public void updated(DObjectRef o) {
                if (_contextLinks != null && _contextLinks.contains(o)) {
                    _contextLinks.refresh();
                }
                if (_dv != null && _dv.isCurrentObject(o)) {
                    _dv.reloadAndDisplayObject(o);
                }
            }
        });
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
                    _contextLinks.update(po);
                    _actionMenu.setObject(null).setParent(po);
                    _list.open(po);
                } else if (token != null && token.startsWith("view_")) {
                    String cid = token.substring(5);
                    DObjectRef o = cid == null ? null : new DObjectRef(cid, -1);
                    DObjectRef po = o.parent();
                    _contextLinks.update(po);
                    _actionMenu.setObject(o).setParent(po);
                    _list.seekTo(o, true);
                } else {
                    if (!"list".equals(token)) {
                        History.replaceItem("list", false);
                    }
                    _contextLinks.update(null);
                    _actionMenu.setObject(null).setParent(null);
                    _list.open(null);
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

        /*
         * view menu
         */
        Menu viewMenu = new Menu();
        viewMenu.add(new ActionEntry(ICON_BACKGROUND, "Background tasks...", new Action() {

            @Override
            public void execute() {
                new BackgroundServiceManager().show(window());
            }
        }));
        viewMenu.add(new ActionEntry(ICON_UPLOAD, "Upload tasks...", new Action() {

            @Override
            public void execute() {
                FileUploadTaskManager.get().show(window());
            }
        }));
        viewMenu.add(new ActionEntry(ICON_SHOPPINGCART, "Shopping cart...", new Action() {

            @Override
            public void execute() {
                ShoppingCartManagerDialog.get().show(window(), true);
            }
        }));
        _menuBar.addMenuButton("View", ICON_VIEW, viewMenu);

        Menu findMenu = new Menu();
        findMenu.add(new ActionEntry("Find DaRIS object...", new Action() {

            @Override
            public void execute() {
                new DObjectFinder().show(window());
            }
        }));
        findMenu.add(new ActionEntry("Find DICOM subject (patient)...", new Action() {

            @Override
            public void execute() {
                new DicomSubjectFinder().show(window());
            }
        }));
        findMenu.add(new ActionEntry("Find DICOM study...", new Action() {

            @Override
            public void execute() {
                new DicomStudyFinder().show(window());
            }
        }));
        findMenu.add(new ActionEntry("Find DICOM data set (series)...", new Action() {

            @Override
            public void execute() {
                new DicomDatasetFinder().show(window());
            }
        }));
        _menuBar.addMenuButton("Find", ICON_FIND, findMenu);

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

    public static void updateHistoryToken(String token) {
        updateHistoryToken(token, false);
    }

    public static void updateHistoryToken(String token, boolean fireEvent) {
        if (!ObjectUtil.equals(token, History.getToken())) {
            History.newItem(token, fireEvent);
        }
    }

    public static void updateHistoryToken(DObjectRef parent, DObjectRef object, boolean fireEvent) {
        String token = historyTokenFor(parent, object);
        updateHistoryToken(token, fireEvent);
        updateWindowTitle(object);
    }

    public static void updateHistoryToken(DObjectRef parent, DObjectRef object) {
        updateHistoryToken(parent, object, false);
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

}
package daris.web.client.gui;

import java.util.List;

import com.google.gwt.user.client.History;

import arc.gui.gwt.object.ObjectDetailedView;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.plugin.Plugin;
import arc.mf.client.util.ObjectUtil;
import arc.mf.object.ObjectResolveHandler;
import daris.web.client.gui.DObjectListGrid.ParentUpdateListener;
import daris.web.client.gui.widget.DMenuButton;
import daris.web.client.gui.widget.DMenuButtonBar;
import daris.web.client.gui.widget.DNavButtonBar;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectPath;
import daris.web.client.model.object.DObjectPathRef;
import daris.web.client.model.object.DObjectRef;

public class DObjectExplorer extends ContainerWidget {

    public static final arc.gui.image.Image ICON_DARIS = new arc.gui.image.Image(
            Resource.INSTANCE.daris_16().getSafeUri().asString(), 14, 14);
    
    public static final arc.gui.image.Image ICON_ACTION = new arc.gui.image.Image(
            Resource.INSTANCE.launch_16().getSafeUri().asString(), 12, 12);

    private VerticalPanel _vp;

    private DMenuButtonBar _menuBar;
    private DMenuButton _actionMenuButton;

    private DNavButtonBar _navBar;

    private DObjectListGrid _list;
    private ObjectDetailedView _dv;

    private DObjectExplorer() {
        _vp = new VerticalPanel();
        _vp.fitToParent();

        /*
         * menu bar
         */
        _menuBar = new DMenuButtonBar();
        _vp.add(_menuBar);
        initMenuButtons();

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
            }

            @Override
            public void deselected(DObjectRef o) {
                //
            }
        });

        _list.addParentUpdateListener(new ParentUpdateListener() {

            @Override
            public void parentUpdated(DObjectRef parent) {
                if (parent == null) {
                    _navBar.update(null);
                    return;
                }
                new DObjectPathRef(parent).resolve(new ObjectResolveHandler<DObjectPath>() {

                    @Override
                    public void resolved(DObjectPath path) {
                        _navBar.update(path.list(true, false));
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
    }

    private void initMenuButtons() {
        _menuBar.addMenuButton("DaRIS", ICON_DARIS, null);
        _actionMenuButton = _menuBar.addMenuButton("Action", ICON_ACTION, null);
        
    }

    public void view(String cid) {
        new DObjectPathRef(cid).resolve(new ObjectResolveHandler<DObjectPath>() {

            @Override
            public void resolved(DObjectPath path) {
                view(path.parents(), path.object());
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
        if (parentCid == null) {
            _list.setParentObject(null);
            return;
        }
        new DObjectPathRef(parentCid).resolve(new ObjectResolveHandler<DObjectPath>() {

            @Override
            public void resolved(DObjectPath path) {
                view(path.list(true, false), path.child());
            }
        });
    }

    private void view(List<DObjectRef> parents, DObjectRef object) {
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
    }

    private static void updateHistoryToken(DObjectRef object) {
        String token = historyTokenFor(object);
        updateHistoryToken(token);
    }

    private static DObjectExplorer _instance;

    public static DObjectExplorer get() {
        if (_instance == null) {
            _instance = new DObjectExplorer();
        }
        return _instance;
    }

}

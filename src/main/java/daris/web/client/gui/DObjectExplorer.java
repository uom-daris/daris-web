package daris.web.client.gui;

import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.object.ObjectDetailedView;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.plugin.Plugin;
import arc.mf.client.util.ObjectUtil;
import arc.mf.object.ObjectResolveHandler;
import daris.web.client.gui.DObjectListGrid.ParentUpdateListener;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectPath;
import daris.web.client.model.object.DObjectPathRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.StringUtils;

public class DObjectExplorer extends ContainerWidget {

    public static final int NAV_HEIGHT = 32;

    public static final int NAV_FONT_SIZE = 13;

    public static final int NAV_SPACING = 5;

    public static final Colour NAV_BACKGROUND_COLOR = new RGB(0xea, 0xea, 0xea);

    public static final Colour NAV_LINK_COLOR = new RGB(0, 0x78, 0xd7);

    private static class NavButton extends HTML {

        NavButton(DObjectRef o, ClickHandler ch) {
            super(o == null ? "Home" : labelFor(o));
            setFontFamily("Roboto,Helvetica,sans-serif");
            setFontSize(NAV_FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setBorderRadius(3);
            setPaddingLeft(5);
            setPaddingRight(5);
            setHeight(NAV_HEIGHT);
            element().getStyle().setLineHeight(NAV_HEIGHT, Unit.PX);
            setOverflow(Overflow.HIDDEN);
            if (ch != null) {
                setColour(NAV_LINK_COLOR);
                setCursor(Cursor.POINTER);
                addMouseOverHandler(event -> {
                    setColour(RGB.WHITE);
                    setBackgroundColour(RGBA.GREY_888);
                });
                addMouseOutHandler(event -> {
                    setColour(NAV_LINK_COLOR);
                    setBackgroundColour(RGBA.TRANSPARENT);
                });
                addClickHandler(ch);
            } else {
                setColour(RGB.BLACK);
            }
        }

        private static String labelFor(DObjectRef o) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.upperCaseFirst(o.referentTypeName()));
            sb.append(" ");
            if (o.referentType() == DObject.Type.PROJECT) {
                sb.append(o.citeableId());
            } else {
                sb.append(CiteableIdUtils.ordinal(o.citeableId()));
            }
            if (o.name() != null) {
                sb.append(": ");
                sb.append(o.name());
            }
            return sb.toString();
        }

    }

    private static class NavSeparator extends HTML {

        NavSeparator() {
            super(">");
            setFontSize(NAV_FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setHeight(NAV_HEIGHT);
            element().getStyle().setLineHeight(NAV_HEIGHT, Unit.PX);
        }

    }

    private VerticalPanel _vp;

    private HorizontalPanel _actionsHP;

    private SimplePanel _navSP;
    private HorizontalPanel _navHP;

    private DObjectListGrid _list;
    private ObjectDetailedView _dv;

    private DObjectExplorer() {
        _vp = new VerticalPanel();
        _vp.fitToParent();

        AbsolutePanel actionsAP = new AbsolutePanel();
        actionsAP.setHeight(32);
        actionsAP.setWidth100();
        _vp.add(actionsAP);

        /*
         * Actions bar
         */
        _actionsHP = new HorizontalPanel();
        _actionsHP.setHeight(32);
        _actionsHP.setPosition(Position.ABSOLUTE);
        _actionsHP.setTop(0);
        _actionsHP.setLeft(0);

        actionsAP.add(_actionsHP);

        /*
         * Nav bar
         */
        _navSP = new SimplePanel();
        _navSP.setHeight(NAV_HEIGHT);
        _navSP.setWidth100();
        _navSP.setBackgroundColour(NAV_BACKGROUND_COLOR);

        _navHP = new HorizontalPanel();
        _navHP.setSpacing(NAV_SPACING);
        _navHP.setHeight(NAV_HEIGHT);

        _navSP.setContent(_navHP);
        updateNavBar(null);

        _vp.add(_navSP);

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
                    updateNavBar(null);
                    return;
                }
                new DObjectPathRef(parent).resolve(new ObjectResolveHandler<DObjectPath>() {

                    @Override
                    public void resolved(DObjectPath path) {
                        updateNavBar(path.list(true, false));
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

    private void updateNavBar(List<DObjectRef> parents) {
        int nbParents = parents == null ? 0 : parents.size();
        _navHP.removeAll();
        _navHP.add(new NavButton(null, (nbParents > 0) ? (event -> {
            list();
        }) : null));
        if (nbParents > 0) {
            for (int i = 0; i < nbParents; i++) {
                DObjectRef p = parents.get(i);
                _navHP.add(new NavSeparator());
                _navHP.add(new NavButton(p, (i != nbParents - 1) ? (event -> {
                    list(p);
                }) : null));
            }
        }
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
        updateNavBar(parents);
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

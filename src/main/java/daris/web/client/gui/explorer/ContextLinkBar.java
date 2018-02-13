package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.mf.client.util.ObjectUtil;
import daris.web.client.gui.Resource;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectPathRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.StringUtils;

public class ContextLinkBar extends ContainerWidget {

    public static interface Listener {
        void selectedLink(DObjectRef o);
    }

    public static final int FONT_SIZE = 13;
    public static final int HEIGHT = 32;
    public static final Colour BORDER_COLOUR = new RGB(0xf4, 0xf4, 0xf4);
    public static final int SPACING = 5;

    public static final arc.gui.image.Image LOADING_ICON = new arc.gui.image.Image(
            Resource.INSTANCE.loading16().getSafeUri().asString(), 56, 56);

    private SimplePanel _sp;
    private HorizontalPanel _hp;
    private arc.gui.gwt.widget.image.Image _loadingIcon;

    private DObjectPathRef _path;

    private List<Listener> _listeners;

    public ContextLinkBar() {

        _sp = new SimplePanel();
        _sp.setHeight(HEIGHT);
        _sp.setWidth100();
        _sp.setBorder(1, BORDER_COLOUR);

        _hp = new HorizontalPanel();
        _hp.setSpacing(SPACING);
        _hp.setHeight(HEIGHT);

        _loadingIcon = new arc.gui.gwt.widget.image.Image(LOADING_ICON, 16, 16);
        _hp.add(_loadingIcon);

        _sp.setContent(_hp);

        initWidget(_sp);

        _path = new DObjectPathRef(null);

        render(false);

    }

    private NavLink addButton(DObjectRef o, ClickHandler ch) {
        NavLink button = new NavLink(o, o == null ? "Home" : null, ch);
        _hp.add(button);
        return button;
    }

    private void addSeparator() {
        _hp.add(new Separator());
    }

    private void setBusyLoading() {
        _hp.removeAll();
        _hp.add(new NavLink(null, "loading...", null));
    }

    private void render(List<DObjectRef> parents) {
        _hp.removeAll();
        int nbParents = parents == null ? 0 : parents.size();
        /*
         * home/root
         */
        addButton(null, (nbParents > 0) ? (event -> {
            update(null);
            notifyOfSelect(null);
        }) : null);
        /*
         * parents
         */
        if (nbParents > 0) {
            for (int i = 0; i < nbParents; i++) {
                DObjectRef p = parents.get(i);
                addSeparator();
                addButton(p, (i != nbParents - 1) ? (event -> {
                    update(p);
                    notifyOfSelect(p);
                }) : null);
            }
        }
    }

    private void notifyOfSelect(DObjectRef o) {
        if (_listeners != null) {
            for (Listener l : _listeners) {
                l.selectedLink(o);
            }
        }
    }

    private void render(boolean refresh) {
        if (refresh) {
            _path.reset();
        }
        setBusyLoading();
        _path.resolve(path -> {
            if (path == null) {
                render((List<DObjectRef>) null);
                return;
            }
            render(path.list(true, false));
        });
    }

    public void update(DObjectRef leafParent) {
        if (!ObjectUtil.equals(_path.object(), leafParent)) {
            _path.setObject(leafParent);
            render(true);
        }
    }

    public void refresh() {
        render(true);
    }

    private static class Separator extends HTML {
        Separator() {
            super(">");
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(HEIGHT, Unit.PX);
        }
    }

    private static class NavLink extends HTML {

        public static final String FONT_FAMILY = "Helvetica,sans-serif";
        public static final int FONT_SIZE = 13;
        public static final int HEIGHT = ContextLinkBar.HEIGHT;
        public static final Colour COLOUR = new RGB(0, 0x78, 0xd7);
        public static final Colour BG_COLOUR = null;
        public static final Colour HOVER_COLOUR = RGB.WHITE;
        public static final Colour HOVER_BG_COLOUR = RGB.GREY_CCC;
        public static final Colour TEXT_COLOUR = RGB.BLACK;

        NavLink(DObjectRef o, String label, ClickHandler ch) {
            super(o == null ? label : labelFor(o));
            setFontFamily(FONT_FAMILY);
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setBorderRadius(3);
            setPaddingLeft(5);
            setPaddingRight(5);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(HEIGHT, Unit.PX);
            setOverflow(Overflow.HIDDEN);
            if (ch != null) {
                setColour(COLOUR);
                setCursor(Cursor.POINTER);
                addMouseOverHandler(event -> {
                    setColour(HOVER_COLOUR);
                    setBackgroundColour(HOVER_BG_COLOUR);
                });
                addMouseOutHandler(event -> {
                    setColour(COLOUR);
                    setBackgroundColour(BG_COLOUR);
                });
                addClickHandler(ch);
            } else {
                setColour(TEXT_COLOUR);
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

    public boolean contains(DObjectRef obj) {
        if (_path != null && _path.referent() != null) {
            List<DObjectRef> os = _path.referent().list(true, false);
            if (os != null) {
                for (DObjectRef o : os) {
                    if (o.equals(obj)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addListener(Listener l) {
        if (_listeners == null) {
            _listeners = new ArrayList<Listener>();
        }
        _listeners.add(l);
    }

    public void removeListener(Listener l) {
        if (_listeners != null) {
            _listeners.remove(l);
        }
    }
}

package daris.web.client.gui;

import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.StringUtils;

public class DObjectNavigationBar extends ContainerWidget {

    public static final int HEIGHT = 32;

    public static final int FONT_SIZE = 13;

    public static final int SPACING = 5;

    public static final Colour BACKGROUND_COLOR = new RGB(0xea, 0xea, 0xea);

    public static final Colour LINK_COLOR = new RGB(0, 120, 215);

    private static class NavButton extends HTML {

        NavButton(DObjectRef o, boolean link) {
            super(o == null ? "Home" : labelFor(o));
            setFontFamily("Roboto,Helvetica,sans-serif");
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setBorderRadius(3);
            setPaddingLeft(5);
            setPaddingRight(5);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(HEIGHT, Unit.PX);
            setOverflow(Overflow.HIDDEN);
            if (link) {
                setColour(LINK_COLOR);
                setCursor(Cursor.POINTER);
                addMouseOverHandler(new MouseOverHandler() {

                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        setColour(RGB.WHITE);
                        setBackgroundColour(RGBA.GREY_888);
                    }
                });
                addMouseOutHandler(new MouseOutHandler() {

                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        setColour(LINK_COLOR);
                        setBackgroundColour(RGBA.TRANSPARENT);
                    }
                });
                addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        // TODO
                    }
                });
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

    private static class Separator extends HTML {

        Separator() {
            super(">");
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(32, Unit.PX);
        }

    }

    private SimplePanel _sp;
    private HorizontalPanel _hp;

    public DObjectNavigationBar(List<DObjectRef> parents) {

        _sp = new SimplePanel();
        _sp.setHeight(HEIGHT);
        _sp.setWidth100();
        _sp.setBackgroundColour(BACKGROUND_COLOR);

        _hp = new HorizontalPanel();
        _hp.setSpacing(SPACING);
        _hp.setHeight(HEIGHT);

        _sp.setContent(_hp);

        initWidget(_sp);

        update(parents);
    }

    public void update(List<DObjectRef> parents) {
        _hp.removeAll();
        _hp.add(new NavButton(null, true));
        if (parents != null) {
            int n = parents.size();
            for (int i = 0; i < n; i++) {
                DObjectRef p = parents.get(i);
                _hp.add(new Separator());
                _hp.add(new NavButton(p, i != n - 1));
            }
        }
    }

}

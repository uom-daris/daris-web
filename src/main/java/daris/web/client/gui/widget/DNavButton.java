package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.HTML;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.util.StringUtils;

public class DNavButton extends HTML {

    DNavButton(DObjectRef o, ClickHandler ch) {
        super(o == null ? "Home" : labelFor(o));
        setFontFamily(DStyles.FONT_FAMILY);
        setFontSize(DStyles.NAV_BUTTON_FONT_SIZE);
        setFontWeight(FontWeight.BOLD);
        setBorderRadius(3);
        setPaddingLeft(5);
        setPaddingRight(5);
        setHeight(DStyles.NAV_BUTTON_BAR_HEIGHT);
        element().getStyle().setLineHeight(DStyles.NAV_BUTTON_BAR_HEIGHT, Unit.PX);
        setOverflow(Overflow.HIDDEN);
        if (ch != null) {
            setColour(DStyles.NAV_BUTTON_LINK_COLOUR);
            setCursor(Cursor.POINTER);
            addMouseOverHandler(event -> {
                setColour(RGB.WHITE);
                setBackgroundColour(RGBA.GREY_CCC);
            });
            addMouseOutHandler(event -> {
                setColour(DStyles.NAV_BUTTON_LINK_COLOUR);
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

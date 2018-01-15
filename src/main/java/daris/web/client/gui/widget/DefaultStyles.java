package daris.web.client.gui.widget;

import java.util.Collection;
import java.util.Date;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;

import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.SimpleHTML;
import arc.mf.client.util.DateTime;

public class DefaultStyles {

    public static final String FONT_FAMILY = "Helvetica,sans-serif";

    public static final int FONT_SIZE = 11;

    @SuppressWarnings("rawtypes")
    public static SimpleHTML formatText(Object value, String fontFamily, Integer fontSize, Integer lineHeight,
            TextAlign textAlign) {
        SimpleHTML fv = new SimpleHTML();
        if (fontFamily != null) {
            fv.setFontFamily(fontFamily);
        }
        if (fontSize != null) {
            fv.setFontSize(fontSize);
        }
        if (textAlign != null) {
            fv.setTextAlign(textAlign);
            fv.element().getStyle().setTextAlign(textAlign);
        }
        if (lineHeight != null) {
            fv.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        }
        if (value != null) {
            if (value instanceof Collection) {
                String v = null;
                Collection c = (Collection) value;
                for (Object o : c) {
                    String ov = null;
                    if (o instanceof Date) {
                        ov = DateTime.dateTimeAsClientString((Date) o);
                    } else {
                        ov = o.toString();
                    }
                    if (v == null) {
                        v = ov;
                    } else {
                        v += ", " + ov;
                    }
                }
                fv.setText(v);
            } else {
                if (value instanceof Date) {
                    fv.setText(DateTime.dateTimeAsClientString((Date) value));
                } else {
                    fv.setText(value.toString());
                }
            }
        }
        return fv;
    }

    public static SimpleHTML formatText(Object value, TextAlign textAlign) {
        return formatText(value, FONT_FAMILY, FONT_SIZE, null, textAlign);
    }

    public static SimpleHTML formatText(Object value) {
        return formatText(value, FONT_FAMILY, FONT_SIZE, null, null);
    }

    @SuppressWarnings("rawtypes")
    public static HTML formatHtml(Object value, String fontFamily, Integer fontSize, Integer lineHeight,
            TextAlign textAlign) {
        HTML fv = new HTML();
        if (fontFamily != null) {
            fv.setFontFamily(fontFamily);
        }
        if (fontSize != null) {
            fv.setFontSize(fontSize);
        }
        if (textAlign != null) {
            fv.setTextAlign(textAlign);
            fv.element().getStyle().setTextAlign(textAlign);
        }
        if (lineHeight != null) {
            fv.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        }
        if (value != null) {
            if (value instanceof Collection) {
                String v = null;
                Collection c = (Collection) value;
                for (Object o : c) {
                    if (v == null) {
                        v = o.toString();
                    } else {
                        v += ", " + o.toString();
                    }
                }
                fv.setHTML(v);
            } else {
                fv.setHTML(value.toString());
            }
        }
        return fv;
    }

    public static HTML formatHtml(Object value, TextAlign textAlign) {
        return formatHtml(value, FONT_FAMILY, FONT_SIZE, null, textAlign);
    }

    public static HTML formatHtml(Object value) {
        return formatHtml(value, FONT_FAMILY, FONT_SIZE, null, null);
    }

}

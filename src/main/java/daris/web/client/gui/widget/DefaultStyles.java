package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;

import arc.gui.form.Form;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.SimpleHTML;
import arc.gui.gwt.widget.list.ListGrid;

public class DefaultStyles {

    public static final String FONT_FAMILY = "Helvetica,sans-serif";

    public static final int HTML_FONT_SIZE = 11;

    public static final int LIST_GRID_CELL_FONT_SIZE = 11;

    public static final int LIST_GRID_MIN_ROW_HEIGHT = 26;

    public static final int FORM_SPACING = 15;

    public static final int FORM_PADDING = 20;

    public static final int FORM_FONT_SIZE = 11;

    public static void apply(Form form, int spacing, int padding) {
        if (spacing > 0) {
            form.setSpacing(spacing);
        }
        if (padding > 0) {
            form.setPadding(padding);
        }
    }

    public static void apply(Form form) {
        apply(form, FORM_SPACING, FORM_PADDING);
    }

    public static void apply(HTML html, String fontFamily, int fontSize, int lineHeight, TextAlign textAlign) {
        if (fontFamily != null) {
            html.setFontFamily(fontFamily);
        }
        if (fontSize > 0) {
            html.setFontSize(fontSize);
        }
        if (lineHeight > 0) {
            html.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        }
        if (textAlign != null) {
            html.setTextAlign(textAlign);
        }
    }

    public static void apply(HTML html) {
        apply(html, FONT_FAMILY, HTML_FONT_SIZE, 0, null);
    }

    public static void apply(SimpleHTML text, String fontFamily, int fontSize, int lineHeight, TextAlign textAlign) {
        if (fontFamily != null) {
            text.setFontFamily(fontFamily);
        }
        if (fontSize > 0) {
            text.setFontSize(fontSize);
        }
        if (lineHeight > 0) {
            text.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        }
        if (textAlign != null) {
            text.setTextAlign(textAlign);
        }
    }

    public static void apply(SimpleHTML text) {
        apply(text, FONT_FAMILY, HTML_FONT_SIZE, 0, null);
    }

    public static void apply(ListGrid<?> listGrid, int minRowHeight, int cellPadding, int cellSpacing) {
        if (minRowHeight > 0) {
            listGrid.setMinRowHeight(minRowHeight);
        }
        if (cellPadding >= 0) {
            listGrid.setCellPadding(cellPadding);
        }
        if (cellSpacing >= 0) {
            listGrid.setCellSpacing(cellSpacing);
        }
    }

    public static void apply(ListGrid<?> listGrid) {
        apply(listGrid, LIST_GRID_MIN_ROW_HEIGHT, -1, -1);
    }
}

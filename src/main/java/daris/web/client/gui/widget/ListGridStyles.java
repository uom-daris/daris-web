package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.TextAlign;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.SimpleHTML;
import arc.gui.gwt.widget.format.WidgetFormatter;

public class ListGridStyles {
    public static final String LIST_GRID_CELL_FONT_FAMILY = DefaultStyles.FONT_FAMILY;

    public static final int LIST_GRID_MIN_ROW_HEIGHT = 26;

    public static final int LIST_GRID_CELL_FONT_SIZE = DefaultStyles.FONT_SIZE;

    public static final WidgetFormatter<Object, Object> LIST_GRID_CELL_TEXT_FORMATTER = getTextFormatter(
            LIST_GRID_CELL_FONT_FAMILY, LIST_GRID_CELL_FONT_SIZE, null);

    static WidgetFormatter<Object, Object> getTextFormatter(String fontFamily, Integer fontSize, Integer lineHeight) {
        return new WidgetFormatter<Object, Object>() {
            @Override
            public BaseWidget format(Object context, Object value) {
                return formatCellText(value, fontFamily, fontSize, lineHeight);
            }
        };
    }

    static SimpleHTML formatCellText(Object value, String fontFamily, Integer fontSize, Integer lineHeight) {
        return DefaultStyles.formatText(value, fontFamily, fontSize, lineHeight, TextAlign.LEFT);
    }

    public static SimpleHTML formatCellText(Object value) {
        return formatCellText(value, LIST_GRID_CELL_FONT_FAMILY, LIST_GRID_CELL_FONT_SIZE, LIST_GRID_MIN_ROW_HEIGHT);
    }

    static WidgetFormatter<Object, Object> getHtmlFormatter(String fontFamily, Integer fontSize, Integer lineHeight,
            TextAlign textAlign) {
        return new WidgetFormatter<Object, Object>() {
            @Override
            public BaseWidget format(Object context, Object value) {
                return formatCellHtml(value, fontFamily, fontSize, lineHeight, textAlign);
            }
        };
    }

    public static WidgetFormatter<Object, Object> getHtmlFormatter(TextAlign textAlign) {
        return getHtmlFormatter(LIST_GRID_CELL_FONT_FAMILY, LIST_GRID_CELL_FONT_SIZE, LIST_GRID_MIN_ROW_HEIGHT,
                textAlign);
    }

    static HTML formatCellHtml(Object value, String fontFamily, Integer fontSize, Integer lineHeight,
            TextAlign textAlign) {
        return DefaultStyles.formatHtml(value, fontFamily, fontSize, lineHeight, textAlign);
    }

    public static HTML formatCellHtml(Object value, TextAlign textAlign) {
        return formatCellHtml(value, LIST_GRID_CELL_FONT_FAMILY, LIST_GRID_CELL_FONT_SIZE, LIST_GRID_MIN_ROW_HEIGHT,
                textAlign);
    }

    public static HTML formatCellHtml(Object value) {
        return formatCellHtml(value, LIST_GRID_CELL_FONT_FAMILY, LIST_GRID_CELL_FONT_SIZE, LIST_GRID_MIN_ROW_HEIGHT,
                TextAlign.LEFT);
    }

}

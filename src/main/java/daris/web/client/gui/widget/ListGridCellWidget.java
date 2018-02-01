package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.TextAlign;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.SimpleHTML;
import arc.gui.gwt.widget.format.WidgetFormatter;

public class ListGridCellWidget {

    public static final WidgetFormatter<Object, Object> DEFAULT_TEXT_FORMATTER = getTextFormatter(
            DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE, DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT);

    public static WidgetFormatter<Object, Object> getTextFormatter(String fontFamily, int fontSize, int lineHeight) {
        return new WidgetFormatter<Object, Object>() {
            @Override
            public BaseWidget format(Object context, Object value) {
                return new SimpleHtmlBuilder().setFontFamily(fontFamily).setFontSize(fontSize).setLineHeight(lineHeight)
                        .setHtml(value).build();
            }
        };
    }

    public static SimpleHTML createTextWidget(Object value, String fontFamily, int fontSize, int lineHeight) {
        return new SimpleHtmlBuilder().setFontFamily(fontFamily).setFontSize(fontSize).setLineHeight(lineHeight)
                .setHtml(value).build();
    }

    public static SimpleHTML createTextWidget(Object value) {
        return createTextWidget(value, DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE,
                DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT);
    }

    public static final WidgetFormatter<Object, Object> DEFAULT_HTML_FORMATTER = getHtmlFormatter(
            DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE, DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT,
            null);

    static WidgetFormatter<Object, Object> getHtmlFormatter(String fontFamily, int fontSize, int lineHeight,
            TextAlign textAlign) {
        return new WidgetFormatter<Object, Object>() {
            @Override
            public BaseWidget format(Object context, Object value) {
                return new HtmlBuilder().setFontFamily(fontFamily).setFontSize(fontSize).setLineHeight(lineHeight)
                        .setTextAlign(textAlign).setHtml(value).build();
            }
        };
    }

    public static WidgetFormatter<Object, Object> getHtmlFormatter(TextAlign textAlign) {
        return getHtmlFormatter(DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE,
                DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT, textAlign);
    }

    public static HTML createHtmlWidget(Object value, String fontFamily, int fontSize, int lineHeight,
            TextAlign textAlign) {
        return new HtmlBuilder().setFontFamily(fontFamily).setFontSize(fontSize).setLineHeight(lineHeight)
                .setTextAlign(textAlign).setHtml(value).build();
    }

    public static HTML createHtmlWidget(Object value, TextAlign textAlign) {
        return createHtmlWidget(value, DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE,
                DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT, textAlign);
    }

    public static HTML createHtmlWidget(Object value) {
        return createHtmlWidget(value, DefaultStyles.FONT_FAMILY, DefaultStyles.LIST_GRID_CELL_FONT_SIZE,
                DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT, TextAlign.LEFT);
    }

}

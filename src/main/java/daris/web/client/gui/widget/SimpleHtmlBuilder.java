package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;

import arc.gui.gwt.widget.SimpleHTML;

public class SimpleHtmlBuilder {

    private SimpleHTML _html;
    private StringBuilder _sb;

    public SimpleHtmlBuilder() {
        _html = new SimpleHTML();
        _sb = new StringBuilder();
    }

    public SimpleHtmlBuilder setFontFamily(String fontFamily) {
        if (fontFamily != null) {
            _html.setFontFamily(fontFamily);
        } else {
            _html.element().getStyle().clearProperty("font-family");
        }
        return this;
    }

    public SimpleHtmlBuilder setFontSize(Integer fontSize) {
        if (fontSize != null) {
            _html.setFontSize(fontSize);
        } else {
            _html.element().getStyle().clearFontSize();
        }
        return this;
    }

    public SimpleHtmlBuilder setFontWeight(FontWeight fontWeight) {
        if (fontWeight != null) {
            _html.setFontWeight(fontWeight);
        } else {
            _html.element().getStyle().clearFontWeight();
        }
        return this;
    }

    public SimpleHtmlBuilder setLineHeight(Integer lineHeight) {
        if (lineHeight != null) {
            _html.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        } else {
            _html.element().getStyle().clearLineHeight();
        }
        return this;
    }

    public SimpleHtmlBuilder setCursor(Cursor cursor) {
        if (cursor != null) {
            _html.setCursor(cursor);
        } else {
            _html.element().getStyle().clearCursor();
        }
        return this;
    }

    public SimpleHtmlBuilder setTextAlign(TextAlign textAlign) {
        if (textAlign != null) {
            _html.setTextAlign(textAlign);
        } else {
            _html.element().getStyle().clearTextAlign();
        }
        return this;
    }

    public SimpleHtmlBuilder appendHtml(Object o) {
        if (o != null) {
            _sb.append(HtmlBuilder.toString(o));
        }
        return this;
    }

    public SimpleHtmlBuilder appendHtml(byte n) {
        _sb.append(Byte.toString(n));
        return this;
    }

    public SimpleHtmlBuilder appendHtml(int n) {
        _sb.append(Integer.toString(n));
        return this;
    }

    public SimpleHtmlBuilder appendHtml(long n) {
        _sb.append(Long.toString(n));
        return this;
    }

    public SimpleHtmlBuilder appendHtml(float n) {
        _sb.append(Float.toString(n));
        return this;
    }

    public SimpleHtmlBuilder appendHtml(double n) {
        _sb.append(Double.toString(n));
        return this;
    }

    public SimpleHtmlBuilder setHtml(Object o) {
        if (_sb.length() > 0) {
            _sb.delete(0, _sb.length());
        }
        if (o != null) {
            _sb.append(HtmlBuilder.toString(o));
        }
        return this;
    }

    public SimpleHtmlBuilder setHeight(int height) {
        _html.setHeight(height);
        return this;
    }

    public SimpleHtmlBuilder setWidth(int width) {
        _html.setWidth(width);
        return this;
    }

    public SimpleHtmlBuilder setHeight100() {
        _html.setHeight100();
        return this;
    }

    public SimpleHtmlBuilder setWidth100() {
        _html.setWidth100();
        return this;
    }

    public SimpleHtmlBuilder fitToParent() {
        _html.fitToParent();
        return this;
    }

    public SimpleHTML build() {
        _html.setHTML(_sb.toString());
        return _html;
    }

}

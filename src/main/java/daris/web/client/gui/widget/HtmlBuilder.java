package daris.web.client.gui.widget;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;

import arc.gui.gwt.widget.HTML;

public class HtmlBuilder {

    private HTML _html;
    private StringBuilder _sb;

    public HtmlBuilder() {
        _html = new HTML();
        _sb = new StringBuilder();
    }

    public HtmlBuilder setFontFamily(String fontFamily) {
        if (fontFamily != null) {
            _html.setFontFamily(fontFamily);
        }
        return this;
    }

    public HtmlBuilder setFontSize(int fontSize) {
        if (fontSize > 0) {
            _html.setFontSize(fontSize);
        }
        return this;
    }

    public HtmlBuilder setFontWeight(FontWeight fontWeight) {
        if (fontWeight != null) {
            _html.setFontWeight(fontWeight);
        }
        return this;
    }

    public HtmlBuilder setLineHeight(int lineHeight) {
        if (lineHeight > 0) {
            _html.element().getStyle().setLineHeight(lineHeight, Unit.PX);
        }
        return this;
    }

    public HtmlBuilder setCursor(Cursor cursor) {
        if (cursor != null) {
            _html.setCursor(cursor);
        }
        return this;
    }

    public HtmlBuilder setTextAlign(TextAlign textAlign) {
        if (textAlign != null) {
            _html.setTextAlign(textAlign);
        }
        return this;
    }

    public HtmlBuilder appendHtml(Object o) {
        if (o != null) {
            _sb.append(toString(o));
        }
        return this;
    }

    public HtmlBuilder appendHtml(byte n) {
        _sb.append(Byte.toString(n));
        return this;
    }

    public HtmlBuilder appendHtml(int n) {
        _sb.append(Integer.toString(n));
        return this;
    }

    public HtmlBuilder appendHtml(long n) {
        _sb.append(Long.toString(n));
        return this;
    }

    public HtmlBuilder appendHtml(float n) {
        _sb.append(Float.toString(n));
        return this;
    }

    public HtmlBuilder appendHtml(double n) {
        _sb.append(Double.toString(n));
        return this;
    }

    public HtmlBuilder setHtml(Object o) {
        if (_sb.length() > 0) {
            _sb.delete(0, _sb.length());
        }
        if (o != null) {
            _sb.append(toString(o));
        }
        return this;
    }

    public HtmlBuilder setHeight(int height) {
        _html.setHeight(height);
        return this;
    }

    public HtmlBuilder setWidth(int width) {
        _html.setWidth(width);
        return this;
    }

    public HtmlBuilder setHeight100() {
        _html.setHeight100();
        return this;
    }

    public HtmlBuilder setWidth100() {
        _html.setWidth100();
        return this;
    }

    public HtmlBuilder fitToParent() {
        _html.fitToParent();
        return this;
    }

    public HTML build() {
        _html.setHTML(_sb.toString());
        return _html;
    }

    @SuppressWarnings("rawtypes")
    public static String toString(Object value) {
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
                return v;
            } else {
                return value.toString();
            }
        }
        return null;
    }
}

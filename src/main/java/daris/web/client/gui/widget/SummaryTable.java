package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.TextAlign;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;

public class SummaryTable extends ContainerWidget {

    private SimplePanel _sp;
    private HTML _html;

    private SummaryTable(String title, String[] keyValuePairs) {
        _sp = new SimplePanel();
        _sp.setPaddingTop(20);
        _html = new HTML();
        _html.fitToParent();
        _html.setFontFamily("Helvetica, sans-serif");
        _html.setFontSize(11);
        _html.setTextAlign(TextAlign.CENTER);
        _sp.setContent(new ScrollPanel(_html, ScrollPolicy.AUTO));
        initWidget(_sp);
        render(title, keyValuePairs);
    }

    private void render(String title, String[] keyValuePairs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table align=\"center\" style=\"width:80%;border: 1px solid #ddd;\">");
        if (title != null) {
            sb.append("<thead>");
            sb.append(
                    "<tr style=\"line-height:26px;\"><th colspan=\"2\" style=\"border: 1px solid #ddd;font-family:Helvetica, sans-serif;font-size:14px;\"><b>")
                    .append(title).append("</b></th></tr>");
            sb.append("</thead>");
        }
        if (keyValuePairs != null && keyValuePairs.length > 0) {
            if (keyValuePairs.length % 2 != 0) {
                throw new IllegalArgumentException(
                        "Invalid number of arguments. Expects even number of arguments (key value pairs).");
            }
            sb.append("<tbody>");
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                String name = keyValuePairs[i];
                String value = keyValuePairs[i + 1];
                if (name != null && value != null) {
                    sb.append(
                            "<tr style=\"line-height:22px;\"><td align=\"right\" style=\"width:50%;border: 1px solid #ddd;font-family:Helvetica, sans-serif;font-size:11px;font-weight:bold;\">"
                                    + name
                                    + ":</td><td align=\"left\" style=\"width:50%;border: 1px solid #ddd;font-family:Helvetica, sans-serif;font-size:11px;\">")
                            .append(value).append("</td></tr>");
                }
            }
            sb.append("</tbody>");
        }
        sb.append("</table>");
        _html.setHTML(sb.toString());
    }

    public static SummaryTable create(String title, String... kvs) {
        return new SummaryTable(title, kvs);
    }

}

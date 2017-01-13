package daris.web.client.gui.util;

import arc.gui.gwt.widget.button.Button;

public class ButtonUtil {

    public static Button createButton(arc.gui.image.Image icon, String label, String description, boolean gradient) {
        Button button = new Button("<div style=\"text-align:center;\"><img src=\"" + icon.path() + "\" style=\"width:"
                + icon.width() + "px;height:" + icon.height() + "px;vertical-align:middle\"><span style=\"\">&nbsp;"
                + label + "</span></div>", gradient);
        button.setBorderRadius(3);
        button.setFontSize(10);
        if (description != null) {
            button.setToolTip(description);
        }
        return button;
    }
}

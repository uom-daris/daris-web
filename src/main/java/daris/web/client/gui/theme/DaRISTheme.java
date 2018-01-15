package daris.web.client.gui.theme;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.style.Style;
import arc.gui.gwt.style.StyleSet;
import arc.gui.gwt.theme.StandardTheme;

public class DaRISTheme extends StandardTheme {
    public static final Style LIST_SELECT = createListSelect();

    private static Style createListSelect() {
        Style s = StyleSet.INSTANCE.createNewStyle("action_highlight");
        s.setBackgroundColour(new RGB(0xd0, 0xdf, 0xf6));
        s.setForegroundColour(RGB.BLACK);
        return s;
    }

    public Style listSelect() {
        return LIST_SELECT;
    }

}
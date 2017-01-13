package daris.web.client.gui.theme;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.style.Style;
import arc.gui.gwt.style.StyleSet;
import arc.gui.gwt.theme.StandardTheme;

public class DaRISTheme extends StandardTheme {
    public static final Style LIST_SELECT = createListSelect();

    private static Style createListSelect() {
        Style s = StyleSet.INSTANCE.createNewStyle("action_highlight");

        Colour bgColor = new RGB(0x42, 0x85, 0xf4);

        // s.setBackgroundImage(new
        // LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
        // new Colour[] { bgColor, base.lighter(0.1), base }));

        s.setBackgroundColour(bgColor);
        s.setForegroundColour(RGB.WHITE);
        return s;
    }

    public Style listSelect() {
        return LIST_SELECT;
    }

}
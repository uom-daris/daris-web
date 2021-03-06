package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.menu.MenuHideHandler;
import arc.gui.menu.Menu;
import daris.web.client.gui.Resource;

public class MenuButton extends HTML {

    public static final String FONT_FAMILY = DefaultStyles.FONT_FAMILY;
    public static final int HEIGHT = MenuButtonBar.HEIGHT;
    public static final int FONT_SIZE = 13;
    public static final Colour HOVER_COLOUR = RGB.GREY_CCC;

    public static arc.gui.image.Image DOWN = new arc.gui.image.Image(Resource.INSTANCE.down20().getSafeUri().asString(),
            20, 20);

    private Menu _menu;
    private boolean _showingMenu;

    MenuButton(String label, arc.gui.image.Image icon, Menu menu) {
        super(htmlFor(label, icon, menu));
        _menu = menu;
        _showingMenu = false;
        setFontFamily(FONT_FAMILY);
        setFontSize(FONT_SIZE);
        setFontWeight(FontWeight.BOLD);
        setBorderRadius(3);
        setPaddingLeft(5);
        setPaddingRight(5);
        setHeight(HEIGHT);
        element().getStyle().setLineHeight(HEIGHT, Unit.PX);
        setOverflow(Overflow.HIDDEN);

        // defaults to low light
        lowLight();

        setCursor(Cursor.POINTER);

        addMouseOverHandler(event -> {
            highLight();
        });
        addMouseOutHandler(event -> {
            if (!_showingMenu) {
                lowLight();
            }
        });
        addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                highLight();
                arc.gui.gwt.widget.menu.MenuButton.showMenu(event, _menu, MenuButton.this, new MenuHideHandler() {

                    @Override
                    public void hidden() {
                        lowLight();
                    }
                });
            }
        });
    }

    public void setMenu(Menu menu) {
        _menu = menu;
    }

    private void lowLight() {
        setColour(RGB.BLACK);
        setForegroundColour(RGB.BLACK);
        setBackgroundColour(RGBA.TRANSPARENT);
        setTextShadow(0, 1, 1, RGB.WHITE);
    }

    private void highLight() {
        setColour(RGB.WHITE);
        setForegroundColour(RGB.WHITE);
        setBackgroundColour(HOVER_COLOUR);
        setTextShadow(0, 1, 1, RGB.BLACK);
    }

    private static String htmlFor(String label, arc.gui.image.Image icon, Menu menu) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"text-align:center;\">");
        if (icon != null) {
            sb.append("<img src=\"").append(icon.path()).append("\" style=\"width:").append(icon.width())
                    .append("px;height:").append(icon.height()).append("px;vertical-align:middle\">");
        }

        sb.append("<span style=\"margin-left:3px;\">").append(label == null ? menu.label() : label).append("</span>");
        sb.append("<img src=\"").append(DOWN.path()).append("\" style=\"width:").append(DOWN.width())
                .append("px;height:").append(DOWN.height()).append("px;vertical-align:middle\">");
        sb.append("</div>");
        return sb.toString();
    }

}

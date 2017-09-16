package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Position;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.menu.Menu;

public class MenuButtonBar extends ContainerWidget {
    
    public static final int HEIGHT = 32;
    public static final Colour BACKGROUND_COLOUR = new RGB(0xf4, 0xf4, 0xf4);
    public static final int SPACING = 5;


    private AbsolutePanel _menuAP;
    private HorizontalPanel _menuHP;

    public MenuButtonBar() {
        _menuAP = new AbsolutePanel();
        _menuAP.setHeight(HEIGHT);
        _menuAP.setWidth100();
        _menuAP.setBackgroundColour(BACKGROUND_COLOUR);

        _menuHP = new HorizontalPanel();
        _menuHP.setHeight(HEIGHT);
        _menuHP.setSpacing(SPACING);
        _menuHP.setPosition(Position.ABSOLUTE);
        _menuHP.setTop(0);
        _menuHP.setLeft(0);
        _menuAP.add(_menuHP);

        initWidget(_menuAP);

    }

    public MenuButton addMenuButton(String label, arc.gui.image.Image icon, Menu menu) {
        MenuButton menuButton = new MenuButton(label, icon, menu);
        _menuHP.add(menuButton);
        return menuButton;
    }

    public MenuButton addMenuButton(String label, arc.gui.image.Image icon) {
        return addMenuButton(label, icon, null);
    }

}

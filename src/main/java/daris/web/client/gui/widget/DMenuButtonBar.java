package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Position;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.menu.Menu;

public class DMenuButtonBar extends ContainerWidget {

    private AbsolutePanel _menuAP;
    private HorizontalPanel _menuHP;

    public DMenuButtonBar() {
        _menuAP = new AbsolutePanel();
        _menuAP.setHeight(DStyles.MENU_BUTTON_BAR_HEIGHT);
        _menuAP.setWidth100();
        _menuAP.setBackgroundColour(DStyles.MENU_BUTTON_BAR_BACKGROUND_COLOR);

        _menuHP = new HorizontalPanel();
        _menuHP.setHeight(DStyles.MENU_BUTTON_BAR_HEIGHT);
        _menuHP.setSpacing(DStyles.MENU_BUTTON_SPACING);
        _menuHP.setPosition(Position.ABSOLUTE);
        _menuHP.setTop(0);
        _menuHP.setLeft(0);
        _menuAP.add(_menuHP);

        initWidget(_menuAP);

    }

    public DMenuButton addMenuButton(String label, arc.gui.image.Image icon, Menu menu) {
        DMenuButton menuButton = new DMenuButton(label, icon, menu);
        _menuHP.add(menuButton);
        return menuButton;
    }

    public DMenuButton addMenuButton(String label, arc.gui.image.Image icon) {
        return addMenuButton(label, icon, null);
    }

}

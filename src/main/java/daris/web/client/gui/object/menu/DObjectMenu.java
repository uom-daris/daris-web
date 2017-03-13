package daris.web.client.gui.object.menu;

import arc.gui.menu.DynamicMenuListener;
import arc.gui.menu.Menu;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.menu.MenuPreConditions;
import daris.web.client.model.object.menu.MenuPreConditionsRef;

public abstract class DObjectMenu extends Menu {

    private DObjectRef _o;
    private MenuPreConditionsRef _preConditions;

    public DObjectMenu(DObjectRef o) {
        _o = o;
        _preConditions = new MenuPreConditionsRef(_o);
    }

    @Override
    public void create(DynamicMenuListener ml) {
        _preConditions.resolve(pc -> {
            clear();
            updateMenuItems(this, pc);
            ml.created(this);
        });
    }

    protected abstract void updateMenuItems(Menu menu, MenuPreConditions pc);

}

package daris.web.client.gui.user;

import arc.gui.gwt.dnd.DragWidget;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.widget.label.Label;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.window.Window;
import daris.web.client.model.user.RoleUser;

public class RoleUserGUI implements ObjectGUI {

    public static final RoleUserGUI INSTANCE = new RoleUserGUI();

    private RoleUserGUI() {

    }

    @Override
    public String idToString(Object o) {

        return ((RoleUser) o).id();
    }

    @Override
    public String icon(Object o, int size) {

        return null;
    }

    @Override
    public Object reference(Object o) {

        return null;
    }

    @Override
    public boolean needToResolve(Object o) {

        return false;
    }

    @Override
    public void displayDetails(Object o, ObjectDetailsDisplay dd, boolean forEdit) {

    }

    @Override
    public void open(Window w, Object o) {

    }

    @Override
    public DropHandler dropHandler(Object o) {

        return null;
    }

    @Override
    public DragWidget dragWidget(Object o) {

        RoleUser ru = (RoleUser) o;
        return new DragWidget(RoleUser.TYPE_NAME, new Label(ru.name()));
    }

    @Override
    public Menu actionMenu(Window w, Object o, SelectedObjectSet selected, boolean readOnly) {
        return null;
    }

    @Override
    public Menu memberActionMenu(Window w, Object o, SelectedObjectSet selected, boolean readOnly) {
        return null;
    }

    @Override
    public ObjectUpdateHandle createUpdateMonitor(Object o, ObjectUpdateListener ul) {
        return null;
    }

}
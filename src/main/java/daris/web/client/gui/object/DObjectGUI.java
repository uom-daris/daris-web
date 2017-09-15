package daris.web.client.gui.object;

import arc.gui.gwt.dnd.DragWidget;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.window.Window;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.model.object.DObjectRef;

public class DObjectGUI implements ObjectGUI {

    public static final DObjectGUI INSTANCE = new DObjectGUI();

    public static arc.gui.image.Image ICON_CREATE = new arc.gui.image.Image(
            Resource.INSTANCE.add16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_MODIFY = new arc.gui.image.Image(
            Resource.INSTANCE.add16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_DOWNLOAD1 = new arc.gui.image.Image(
            Resource.INSTANCE.download16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_DOWNLOAD2 = new arc.gui.image.Image(
            Resource.INSTANCE.downloadDark16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_SHARE = new arc.gui.image.Image(
            Resource.INSTANCE.share16().getSafeUri().asString(), 16, 16);

    @Override
    public String idToString(Object o) {
        if (o != null && (o instanceof DObjectRef)) {
            return ((DObjectRef) o).citeableId();
        }
        return null;
    }

    @Override
    public String icon(Object o, int size) {
        return null;
    }

    @Override
    public Menu actionMenu(Window window, Object object, SelectedObjectSet selected, boolean readOnly) {
        DObjectRef o = (DObjectRef) object;
        DObjectRef po = o == null ? null : o.parent();
        return new DObjectMenu(po, o, window);
    }

    @Override
    public Menu memberActionMenu(Window w, Object o, SelectedObjectSet selected, boolean readOnly) {
        return null;
    }

    @Override
    public Object reference(Object o) {
        return null;
    }

    @Override
    public boolean needToResolve(Object o) {
        if (o != null && (o instanceof DObjectRef)) {
            return ((DObjectRef) o).needToResolve();
        }
        return false;
    }

    @Override
    public void displayDetails(Object object, ObjectDetailsDisplay dd, boolean forEdit) {
        DObjectRef o = ((DObjectRef) object);
        o.setForEdit(forEdit);
        if (forEdit) {
            o.resolveAndLock(oo -> {
                dd.display(o, DObjectUpdateForm.create(oo).gui());
            });
        } else {
            o.resolve(oo -> {
                dd.display(o, DObjectViewForm.create(oo).gui());
            });
        }
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
        return null;
    }

    @Override
    public ObjectUpdateHandle createUpdateMonitor(Object o, ObjectUpdateListener ul) {
        return null;
    }

}

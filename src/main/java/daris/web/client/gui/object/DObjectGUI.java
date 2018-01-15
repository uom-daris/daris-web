package daris.web.client.gui.object;

import com.google.gwt.dom.client.Style.TextAlign;

import arc.gui.gwt.dnd.DragWidget;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.widget.HTML;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.window.Window;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.gui.widget.HtmlBuilder;
import daris.web.client.model.object.DObjectRef;

public class DObjectGUI implements ObjectGUI {

    public static final DObjectGUI INSTANCE = new DObjectGUI();

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
        if (o != null) {
            DObjectRef obj = (DObjectRef) o;
            String iconUrl = obj.isDataset() ? Resource.INSTANCE.document32().getSafeUri().asString()
                    : Resource.INSTANCE.folder32().getSafeUri().asString();

            HTML w = new HtmlBuilder().setFontSize(11).setLineHeight(20).setTextAlign(TextAlign.CENTER).build();
            w.setHTML("<div style=\"text-align:center;\"><img src=\"" + iconUrl
                    + "\" style=\"width:16px;height:16px;vertical-align:middle\"><span style=\"\">&nbsp;"
                    + obj.typeAndId() + "</span></div>");
            return new DragWidget(w);
        }
        return null;
    }

    @Override
    public ObjectUpdateHandle createUpdateMonitor(Object o, ObjectUpdateListener ul) {
        return null;
    }

}

package daris.web.client.gui.object;

import arc.gui.gwt.dnd.DragWidget;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.window.Window;
import daris.web.client.gui.Resource;
import daris.web.client.gui.collection.action.CollectionArchiveDownloadAction;
import daris.web.client.gui.collection.action.CollectionArchiveShareAction;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.menu.MenuPreConditions;
import daris.web.client.util.DownloadUtil;

public class DObjectGUI implements ObjectGUI {

    public static final DObjectGUI INSTANCE = new DObjectGUI();

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
        if (object == null) {
            return null;
        }
        DObjectRef o = (DObjectRef) object;

        DObjectMenu menu = new DObjectMenu(o) {

            @Override
            protected void updateMenuItems(Menu menu, MenuPreConditions pc) {
                if (pc.contentExists()) {
                    /*
                     * download content
                     */
                    menu.add(new ActionEntry(ICON_DOWNLOAD1, "Download content", () -> {
                        o.resolve(oo -> {
                            DownloadUtil.download(oo.contentDownloadUrl());
                        });
                    }));
                }

                if (pc.numberOfDatasets() > 0) {
                    /*
                     * download as archive
                     */
                    menu.add(new ActionEntry(ICON_DOWNLOAD2, "Download as archive...", () -> {
                        new CollectionArchiveDownloadAction(o, window).execute();
                    }));
                    /*
                     * share url
                     */
                    menu.add(new ActionEntry(ICON_SHARE, "Share archive URL...", () -> {
                        new CollectionArchiveShareAction(o, window).execute();
                    }));
                }

                if (pc.numberOfDicomDatasets() > 0) {
                    /*
                     * dicom send
                     */
                }
            }
        };
        return menu;
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
                dd.display(o, DObjectEditorGUI.create(oo).gui());
            });
        } else {
            o.resolve(oo -> {
                dd.display(o, DObjectViewerGUI.create(oo).gui());
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

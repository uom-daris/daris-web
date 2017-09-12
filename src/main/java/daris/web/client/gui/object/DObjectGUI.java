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
import daris.web.client.gui.dataset.action.DerivedDatasetCreateAction;
import daris.web.client.gui.dataset.action.PrimaryDatasetCreateAction;
import daris.web.client.gui.object.menu.DObjectMenu;
import daris.web.client.gui.project.action.ProjectCreateAction;
import daris.web.client.gui.project.action.ProjectUpdateAction;
import daris.web.client.gui.study.action.StudyCreateAction;
import daris.web.client.gui.subject.action.SubjectCreateAction;
import daris.web.client.gui.subject.action.SubjectUpdateAction;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.DObjectSummary;
import daris.web.client.util.DownloadUtil;

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
        if (object == null) {
            return null;
        }
        DObjectMenu menu = new DObjectMenu((DObjectRef) object) {

            @Override
            protected void updateMenuItems(DObjectMenu m, DObjectRef po, DObjectRef o, DObjectSummary os) {
                if (po == null) {
                    m.add(new ActionEntry(ICON_CREATE, "Create project...", new ProjectCreateAction(window, 0.7, 0.7)));
                } else if (po.isProject()) {
                    m.add(new ActionEntry(ICON_CREATE,
                            "Create subject in " + po.referentTypeName() + " " + po.citeableId() + "...",
                            new SubjectCreateAction(po, window, 0.7, 0.7)));
                } else if (po.isExMethod()) {
                    m.add(new ActionEntry(ICON_CREATE,
                            "Create study in " + po.referentTypeName() + " " + po.citeableId() + "...",
                            new StudyCreateAction(po, window, 0.7, 0.7)));
                } else if (po.isStudy()) {
                    m.add(new ActionEntry(ICON_CREATE,
                            "Create primary dataset in " + po.referentTypeName() + " " + po.citeableId() + "...",
                            new PrimaryDatasetCreateAction(po, window, 0.7, 0.7)));
                    m.add(new ActionEntry(ICON_CREATE,
                            "Create derived dataset in " + po.referentTypeName() + " " + po.citeableId() + "...",
                            new DerivedDatasetCreateAction(po, null, window, 0.7, 0.7)));
                    if (o != null) {
                        m.add(new ActionEntry(ICON_CREATE,
                                "Create dataset derived from " + o.referentTypeName() + " " + o.citeableId() + "...",
                                new DerivedDatasetCreateAction(po, window, 0.7, 0.7, o)));
                    }
                }

                if (o == null) {
                    return;
                }

                m.addSeparator();
                if (o.isProject()) {
                    m.add(new ActionEntry(ICON_MODIFY, "Modify " + o.referentTypeName() + " " + o.citeableId() + "...",
                            new ProjectUpdateAction(o, window, 0.7, 0.7)));
                } else if (o.isSubject()) {
                    m.add(new ActionEntry(ICON_MODIFY, "Modify " + o.referentTypeName() + " " + o.citeableId() + "...",
                            new SubjectUpdateAction(o, window, 0.7, 0.7)));
                } else if (o.isExMethod()) {
                    // TODO
                } else if (o.isStudy()) {
                    // TODO
                } else if (o.isDataset()) {
                    // TODO
                }
                String typeAndId = o.referentTypeName() + " " + o.citeableId();
                if (os.contentExists()) {
                    /*
                     * download content
                     */
                    m.add(new ActionEntry(ICON_DOWNLOAD1, "Download " + typeAndId + " content", () -> {
                        o.resolve(oo -> {
                            DownloadUtil.download(oo.contentDownloadUrl());
                        });
                    }));
                }
                if (os.numberOfDatasets() > 0) {
                    /*
                     * download as archive
                     */
                    m.add(new ActionEntry(ICON_DOWNLOAD2, "Download " + typeAndId + " as archive...", () -> {
                        new CollectionArchiveDownloadAction(o, window).execute();
                    }));
                    /*
                     * share url
                     */
                    m.add(new ActionEntry(ICON_SHARE, "Share " + typeAndId + "...", () -> {
                        new CollectionArchiveShareAction(o, window).execute();
                    }));
                }

                if (os.numberOfDicomDatasets() > 0) {
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

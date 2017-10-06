package daris.web.client.gui.object.menu;

import java.util.List;

import arc.gui.image.Image;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.DynamicMenuListener;
import arc.gui.menu.Entry;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.menu.ObjectMenu;
import arc.gui.window.Window;
import arc.mf.client.util.Action;
import arc.mf.client.util.ObjectUtil;
import daris.web.client.gui.Resource;
import daris.web.client.gui.dataset.action.DerivedDatasetCreateAction;
import daris.web.client.gui.dataset.action.PrimaryDatasetCreateAction;
import daris.web.client.gui.dicom.exports.DicomSendAction;
import daris.web.client.gui.dicom.imports.DicomIngestAction;
import daris.web.client.gui.exmethod.action.ExMethodUpdateAction;
import daris.web.client.gui.object.action.DObjectDestroyAction;
import daris.web.client.gui.object.exports.DownloadAction;
import daris.web.client.gui.object.exports.ShareAction;
import daris.web.client.gui.project.action.ProjectCreateAction;
import daris.web.client.gui.project.action.ProjectUpdateAction;
import daris.web.client.gui.study.action.StudyCreateAction;
import daris.web.client.gui.study.action.StudyUpdateAction;
import daris.web.client.gui.subject.action.SubjectCreateAction;
import daris.web.client.gui.subject.action.SubjectUpdateAction;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.CollectionSummaryRef;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.project.Project;
import daris.web.client.model.subject.Subject;

public class DObjectMenu extends ObjectMenu<DObject> {

    public static arc.gui.image.Image ICON_CREATE1 = new arc.gui.image.Image(
            Resource.INSTANCE.addBlue16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_CREATE2 = new arc.gui.image.Image(
            Resource.INSTANCE.addGreen16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_MODIFY = new arc.gui.image.Image(
            Resource.INSTANCE.editGreen16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_IMPORT1 = new arc.gui.image.Image(
            Resource.INSTANCE.import16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_DOWNLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.downloadGold16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_SHARE1 = new arc.gui.image.Image(
            Resource.INSTANCE.linkGreen16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_SHARE2 = new arc.gui.image.Image(
            Resource.INSTANCE.linkBlue16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_DICOM_SEND = new arc.gui.image.Image(
            Resource.INSTANCE.send16().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_DESTROY = new arc.gui.image.Image(
            Resource.INSTANCE.delete16().getSafeUri().asString(), 16, 16);

    private DObjectRef _po;
    private DObjectRef _o;
    private CollectionSummaryRef _os;

    private Window _owner;

    private boolean _dirty;

    public DObjectMenu(DObjectRef po, DObjectRef o, Window owner) {
        super((String) null);
        setParent(po);
        setObject(o);
        _owner = owner;
        _dirty = true;
    }

    public DObjectMenu setParent(DObjectRef po) {
        if (!ObjectUtil.equals(_po, po)) {
            _po = po;
        }
        return this;
    }

    public DObjectMenu setObject(DObjectRef o) {
        if (!ObjectUtil.equals(_o, o)) {
            _o = o;
            _os = _o == null ? null : new CollectionSummaryRef(_o);
        }
        return this;
    }

    public DObjectMenu setOwner(Window owner) {
        _owner = owner;
        return this;
    }

    @Override
    public void create(DynamicMenuListener ml) {
        clear();
        if (_po != null) {
            _po.resolve(po -> {
                if (_o != null) {
                    _o.resolve(o -> {
                        _os.resolve(os -> {
                            updateMenuItems(os);
                            ml.created(DObjectMenu.this);
                        });
                    });
                } else {
                    updateMenuItems(null);
                    ml.created(DObjectMenu.this);
                }
            });
        } else {
            if (_o != null) {
                _o.resolve(o -> {
                    _os.resolve(os -> {
                        updateMenuItems(os);
                        ml.created(DObjectMenu.this);
                    });
                });
            } else {
                updateMenuItems(null);
                ml.created(DObjectMenu.this);
            }
        }
    }

    private void updateMenuItems(CollectionSummary os) {
        /*
         * create actions
         */
        if (_po == null) {
            add(new ActionEntry(ICON_CREATE1, "Create project...", new ProjectCreateAction(_owner, 0.7, 0.7)));
        } else if (_po.isProject()) {
            add(new ActionEntry(ICON_CREATE1, "Create subject in " + _po.typeAndId() + "...",
                    new SubjectCreateAction(_po, _owner, 0.7, 0.7)));
            if (_po.referent() != null && ((Project) _po.referent()).numberOfMethods() == 1) {
                add(new ActionEntry(ICON_IMPORT1, "Import DICOM data into " + _po.typeAndId() + "...",
                        new DicomIngestAction(_po, _owner, 0.7, 0.7)));
            }
        } else if (_po.isSubject()) {
            if (_po.referent() != null && ((Subject) _po.referent()).method() != null) {
                add(new ActionEntry(ICON_IMPORT1, "Import DICOM data into " + _po.typeAndId() + "...",
                        new DicomIngestAction(_po, _owner, 0.7, 0.7)));
            }
        } else if (_po.isExMethod()) {
            add(new ActionEntry(ICON_CREATE1, "Create study in " + _po.typeAndId() + "...",
                    new StudyCreateAction(_po, _owner, 0.7, 0.7)));
            add(new ActionEntry(ICON_IMPORT1, "Import DICOM data into " + _po.typeAndId() + "...",
                    new DicomIngestAction(_po, _owner, 0.7, 0.7)));
        } else if (_po.isStudy()) {
            add(new ActionEntry(ICON_CREATE1, "Create primary dataset in " + _po.typeAndId() + "...",
                    new PrimaryDatasetCreateAction(_po, _owner, 0.7, 0.7)));
            add(new ActionEntry(ICON_CREATE2, "Create derived dataset in " + _po.typeAndId() + "...",
                    new DerivedDatasetCreateAction(_po, null, _owner, 0.7, 0.7)));
            if (_o != null) {
                add(new ActionEntry(ICON_CREATE2, "Create dataset derived from " + _o.typeAndId() + "...",
                        new DerivedDatasetCreateAction(_po, _owner, 0.7, 0.7, _o)));
            }
            add(new ActionEntry(ICON_IMPORT1, "Import DICOM data into " + _po.typeAndId() + "...",
                    new DicomIngestAction(_po, _owner, 0.7, 0.7)));
        }

        if (_o == null) {
            return;
        }

        addSeparator();

        /*
         * update actions
         */
        if (_o.isProject()) {
            add(new ActionEntry(ICON_MODIFY, "Modify " + _o.typeAndId() + "...",
                    new ProjectUpdateAction(_o, _owner, 0.7, 0.7)));
        } else if (_o.isSubject()) {
            add(new ActionEntry(ICON_MODIFY, "Modify " + _o.typeAndId() + "...",
                    new SubjectUpdateAction(_o, _owner, 0.7, 0.7)));
        } else if (_o.isExMethod()) {
            add(new ActionEntry(ICON_MODIFY, "Modify " + _o.typeAndId() + "...",
                    new ExMethodUpdateAction(_o, _owner, 0.7, 0.7)));
        } else if (_o.isStudy()) {
            add(new ActionEntry(ICON_MODIFY, "Modify " + _o.typeAndId() + "...",
                    new StudyUpdateAction(_o, _owner, 0.7, 0.7)));
        } else if (_o.isDataset()) {
            // TODO
            // @formatter:off
            // add(new ActionEntry(ICON_MODIFY, "Modify " + _o.typeAndId() + "...",
            // DatasetUpdateAction.create(_o, _owner, 0.7, 0.7)));
            // @formatter:on
        }

        if (os != null) {
            /*
             * download
             */
            add(new ActionEntry(ICON_DOWNLOAD, "Download " + _o.typeAndId() + "...",
                    new DownloadAction(_o, os, _owner)));

            /*
             * share url
             */
            add(new ActionEntry(ICON_SHARE1, "Share " + _o.typeAndId() + "...", new ShareAction(_o, os, _owner)));

            if (os.numberOfDicomDatasets() > 0) {
                /*
                 * dicom send
                 */
                add(new ActionEntry(ICON_DICOM_SEND, "Send DICOM data in " + _o.typeAndId() + "...",
                        new DicomSendAction(_o, os, _owner)));
            }

            add(new ActionEntry(ICON_DESTROY, "Destroy " + _o.typeAndId() + "...",
                    new DObjectDestroyAction(_o, os, _owner)));
        }
    }

    public void add(ActionEntry ae) {
        super.add(ae);
        _dirty = true;
    }

    public void add(ActionInterface<DObject> ai) {
        add(null, ai.actionName(), ai);
    }

    public void add(Image icon, ActionInterface<DObject> ai) {
        add(icon, ai.actionName(), ai);
    }

    public void add(Image icon, String label, ActionInterface<DObject> ai) {
        add(new ActionEntry(icon, label, ai.actionDescription(), ai, true));
    }

    public void preShow() {
        if (_dirty) {
            _dirty = false;
            executeAllPreconditions();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeAllPreconditions() {
        List<Entry> es = entries();
        if (es != null) {
            for (Entry e : es) {
                if (e instanceof ActionEntry) {
                    ActionEntry ae = (ActionEntry) e;
                    Action action = ae.action();
                    if (action instanceof ActionInterface) {
                        ActionInterface<DObject> ai = (ActionInterface<DObject>) action;
                        executePreconditions(ai, ae);
                    }
                }
            }
        }
    }

    private void executePreconditions(ActionInterface<DObject> ai, final ActionEntry ae) {
        List<ActionPrecondition> ps = ai.beforeInteractionPreconditions();
        if (ps == null || ps.isEmpty()) {
            return;
        }

        ae.disable();

        for (ActionPrecondition p : ps) {

            p.execute(new ActionPreconditionListener() {

                public void executed(ActionPreconditionOutcome outcome, String message) {

                    if (outcome.equals(ActionPreconditionOutcome.PASS)) {
                        // May have been disabled by some other precondition. If
                        // so, don't re-enable.
                        if (!ae.softDisabled()) {
                            ae.enable();
                        }
                    } else if (outcome.equals(ActionPreconditionOutcome.CONDITIONAL_PASS)) {
                        // May have been disabled by some other precondition. If
                        // so, don't re-enable.
                        if (!ae.softDisabled()) {
                            ae.enable();
                        }
                    } else {
                        String reason = ae.disabledReason();
                        if (reason != null) {
                            message = reason + " AND " + message;
                        }
                        ae.softDisable(message);
                    }
                }
            });
        }
    }

}

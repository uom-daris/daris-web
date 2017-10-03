package daris.web.client.gui.dicom.exports;

import java.util.ArrayList;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DicomSendAction extends ActionInterface<DObject> {

    private CollectionSummary _summary;

    public DicomSendAction(DObjectRef o, CollectionSummary summary, Window owner) {
        super(o.referentTypeName(), o, new ArrayList<ActionPrecondition>(), owner,
                WindowUtil.calcWindowWidth(owner, 0.7), WindowUtil.calcWindowHeight(owner, 0.8));
        _summary = summary;
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        object().resolve(o -> {
            ch.created(new DicomSendForm(o, _summary));
        });
    }

    @Override
    public String actionName() {
        return "Send";
    }

    @Override
    public String title() {
        return "Send DICOM data in " + ((DObjectRef) object()).typeAndId();
    }

    @Override
    protected boolean needToLock() {
        return false;
    }

}

package daris.web.client.gui.dicom.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.CreateActionInterface;
import arc.gui.window.Window;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DicomIngestAction extends CreateActionInterface<DObject> {

    private DObjectRef _po;

    public DicomIngestAction(DObjectRef parentObject, Window owner, int width, int height) {
        super("DICOM data", null, owner, width, height);
        _po = parentObject;
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new DicomIngestForm(_po));
    }

}

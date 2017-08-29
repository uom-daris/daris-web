package daris.web.client.gui.object.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.UpdateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DObjectUpdateAction extends UpdateActionInterface<DObject> {

    public DObjectUpdateAction(DObjectRef o, List<ActionPrecondition> preconditions, Window owner, int width,
            int height) {
        super(o, preconditions, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        // TODO Auto-generated method stub

    }

}

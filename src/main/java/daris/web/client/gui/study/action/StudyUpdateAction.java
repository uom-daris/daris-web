package daris.web.client.gui.study.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.UpdateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class StudyUpdateAction extends UpdateActionInterface<DObject> {

    public StudyUpdateAction(DObjectRef o, List<ActionPrecondition> preconditions, Window owner, int width,
            int height) {
        super(o, preconditions, owner, width, height);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        // TODO Auto-generated method stub

    }

}

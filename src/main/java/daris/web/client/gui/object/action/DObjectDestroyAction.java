package daris.web.client.gui.object.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.DestroyActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import arc.mf.object.ObjectRef;
import daris.web.client.model.object.DObject;

public class DObjectDestroyAction extends DestroyActionInterface<DObject> {


    public DObjectDestroyAction(ObjectRef<DObject> o, List<ActionPrecondition> preconditions, Window owner) {
        super(o, preconditions, owner);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        // TODO Auto-generated method stub
        
    }


}

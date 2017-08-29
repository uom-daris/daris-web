package daris.web.client.gui.dataset.action;

import java.util.List;

import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.gui.object.action.DObjectCreateAction;
import daris.web.client.model.object.DObjectRef;

public abstract class DatasetCreateAction extends DObjectCreateAction {

    public DatasetCreateAction(DObjectRef parentObj, List<ActionPrecondition> preconditions, Window owner, int width,
            int height) {
        super(parentObj, preconditions, owner, width, height);
        // TODO Auto-generated constructor stub
    }

}

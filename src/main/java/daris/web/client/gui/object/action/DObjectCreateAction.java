package daris.web.client.gui.object.action;

import java.util.List;

import arc.gui.object.action.CreateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public abstract class DObjectCreateAction extends CreateActionInterface<DObject> {

    private DObjectRef _parentObj;

    public DObjectCreateAction(DObjectRef parentObj, List<ActionPrecondition> preconditions, Window owner, int width,
            int height) {
        super(parentObj == null ? DObject.Type.PROJECT.toString() : parentObj.childTypeName(), preconditions, owner,
                width, height);
        _parentObj = parentObj;
    }

    public DObjectRef parentObject() {
        return _parentObj;
    }

}

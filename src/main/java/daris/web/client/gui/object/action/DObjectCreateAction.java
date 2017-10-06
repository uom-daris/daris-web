package daris.web.client.gui.object.action;

import java.util.ArrayList;

import arc.gui.object.action.CreateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public abstract class DObjectCreateAction extends CreateActionInterface<DObject> {

    private DObjectRef _parentObj;

    protected DObjectCreateAction(DObjectRef parentObj, Window owner, double w, double h) {
        super(parentObj == null ? DObject.Type.PROJECT.toString() : parentObj.childTypeName(),
                new ArrayList<ActionPrecondition>(), owner, WindowUtil.calcWindowWidth(owner, w),
                WindowUtil.calcWindowHeight(owner, h));
        _parentObj = parentObj;
    }

    public DObjectRef parentObject() {
        return _parentObj;
    }

    protected void addPrecondition(ActionPrecondition precondition) {
        preconditions().add(precondition);
    }

}

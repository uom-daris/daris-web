package daris.web.client.gui.project.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.UpdateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.EvaluatePrecondition;
import arc.gui.window.Window;
import arc.mf.client.util.ListUtil;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class ProjectUpdateAction extends UpdateActionInterface<DObject> {

    public ProjectUpdateAction(DObjectRef project, Window owner, int width, int height) {
        super(project, ListUtil.list(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String description() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                // TODO Auto-generated method stub
                
            }
        }), owner, width, height);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        // TODO Auto-generated method stub

    }

}

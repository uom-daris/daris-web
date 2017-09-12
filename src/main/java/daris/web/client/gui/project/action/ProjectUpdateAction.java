package daris.web.client.gui.project.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.gui.object.action.DObjectUpdateAction;
import daris.web.client.gui.project.ProjectUpdateForm;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.project.Project;

public class ProjectUpdateAction extends DObjectUpdateAction<Project> {

    public ProjectUpdateAction(DObjectRef po, Window owner, double width, double height) {
        super(po, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        obj().resolve(o -> {
            if (o != null) {
                ch.created(new ProjectUpdateForm((Project) o));
            }
        });
    }

}

package daris.web.client.gui.subject.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.gui.object.action.DObjectUpdateAction;
import daris.web.client.gui.subject.SubjectUpdateForm;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.subject.Subject;

public class SubjectUpdateAction extends DObjectUpdateAction<Subject> {

    public SubjectUpdateAction(DObjectRef po, Window owner, double width, double height) {
        super(po, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        obj().resolve(o -> {
            if (o != null) {
                ch.created(new SubjectUpdateForm((Subject) o));
            }
        });
    }

}
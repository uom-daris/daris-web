package daris.web.client.gui.study.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.gui.object.action.DObjectUpdateAction;
import daris.web.client.gui.study.StudyUpdateForm;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.study.Study;

public class StudyUpdateAction extends DObjectUpdateAction<Study> {

    public StudyUpdateAction(DObjectRef o, Window owner, double width, double height) {
        super(o, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        obj().resolve(o -> {
            if (o != null) {
                ch.created(new StudyUpdateForm((Study) o));
            }
        });
    }

}

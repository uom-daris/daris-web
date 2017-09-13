package daris.web.client.gui.exmethod.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.gui.exmethod.ExMethodUpdateForm;
import daris.web.client.gui.object.action.DObjectUpdateAction;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.object.DObjectRef;

public class ExMethodUpdateAction extends DObjectUpdateAction<ExMethod> {

    public ExMethodUpdateAction(DObjectRef o, Window owner, double w, double h) {
        super(o, owner, w, h);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        obj().resolve(o -> {
            if (o != null) {
                ch.created(new ExMethodUpdateForm((ExMethod) o));
            }
        });
    }

}

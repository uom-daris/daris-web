package daris.web.client.gui.dataset.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.window.Window;
import daris.web.client.model.dataset.PrimaryDatasetCreator;
import daris.web.client.model.object.DObjectRef;

public class PrimaryDatasetCreateAction extends DatasetCreateAction {

    public PrimaryDatasetCreateAction(DObjectRef study, List<ActionPrecondition> preconditions, Window owner, int width,
            int height) {
        super(study, preconditions, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new PrimaryDatasetCreateForm(new PrimaryDatasetCreator(parentObject())));
    }

}

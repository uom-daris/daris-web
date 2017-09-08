package daris.web.client.gui.dataset.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.model.dataset.PrimaryDatasetCreator;
import daris.web.client.model.object.DObjectRef;

public class PrimaryDatasetCreateAction extends DatasetCreateAction {

    public PrimaryDatasetCreateAction(DObjectRef study, Window owner, double width, double height) {
        super(study, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new PrimaryDatasetCreateForm(new PrimaryDatasetCreator(parentObject())));
    }

}

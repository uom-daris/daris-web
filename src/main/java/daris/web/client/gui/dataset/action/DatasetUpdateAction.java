package daris.web.client.gui.dataset.action;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import arc.mf.session.Session;
import daris.web.client.gui.dataset.DerivedDatasetUpdateForm;
import daris.web.client.gui.dataset.PrimaryDatasetUpdateForm;
import daris.web.client.gui.object.action.DObjectUpdateAction;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DerivedDataset;
import daris.web.client.model.dataset.PrimaryDataset;
import daris.web.client.model.object.DObjectRef;

public class DatasetUpdateAction extends DObjectUpdateAction<Dataset> {

    public DatasetUpdateAction(DObjectRef o, Window owner, double width, double height) {
        super(o, owner, width, height);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        obj().resolve(o -> {
            if (o != null) {
                if (o instanceof DerivedDataset) {
                    ch.created(new DerivedDatasetUpdateForm((DerivedDataset) o));
                } else if (o instanceof PrimaryDataset) {
                    ch.created(new PrimaryDatasetUpdateForm((PrimaryDataset) o));
                } else {
                    Session.displayError("Creating dataset update form...",
                            new AssertionError("Unknown dataset class: " + o.getClass().getCanonicalName()));
                }
            }
        });
    }

}
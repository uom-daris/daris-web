package daris.web.client.gui.dataset;

import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.imports.FileUploadTaskManager;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.model.dataset.DerivedDataset;
import daris.web.client.model.dataset.DerivedDatasetUpdater;
import daris.web.client.model.dataset.messages.DerivedDatasetUpdate;
import daris.web.client.model.dataset.messages.DerivedDatasetUpdateTask;

public class DerivedDatasetUpdateForm extends DatasetUpdateForm<DerivedDataset> {

    public DerivedDatasetUpdateForm(DerivedDataset o) {
        super(o);
        // TODO Auto-generated constructor stub
    }

    private DerivedDatasetUpdater updater() {
        return (DerivedDatasetUpdater) updater;
    }

    @Override
    public void execute(ActionListener l) {
        final DerivedDatasetUpdater u = updater();
        if (u.hasFiles()) {
            new DerivedDatasetUpdateTask(u).execute(r -> {
                MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                        "Subject " + object.citeableId() + " has been updated.", 3);
            }, FileUploadTaskManager.get());
        } else {
            new DerivedDatasetUpdate(u).send(r -> {
                MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                        "Subject " + object.citeableId() + " has been updated.", 3);
            });
        }
        l.executed(true);
    }

}

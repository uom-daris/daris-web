package daris.web.client.gui.dataset;

import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.imports.FileUploadTaskManager;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.model.dataset.PrimaryDataset;
import daris.web.client.model.dataset.PrimaryDatasetUpdater;
import daris.web.client.model.dataset.messages.PrimaryDatasetUpdate;
import daris.web.client.model.dataset.messages.PrimaryDatasetUpdateTask;

public class PrimaryDatasetUpdateForm extends DatasetUpdateForm<PrimaryDataset> {

    public PrimaryDatasetUpdateForm(PrimaryDataset o) {
        super(o);
        // TODO Auto-generated constructor stub
    }

    private PrimaryDatasetUpdater updater() {
        return (PrimaryDatasetUpdater) updater;
    }

    @Override
    public void execute(ActionListener l) {
        final PrimaryDatasetUpdater u = updater();
        if (u.hasFiles()) {
            new PrimaryDatasetUpdateTask(u).execute(r -> {
                MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                        "Subject " + object.citeableId() + " has been updated.", 3);
            }, FileUploadTaskManager.get());
        } else {
            new PrimaryDatasetUpdate(u).send(r -> {
                MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                        "Subject " + object.citeableId() + " has been updated.", 3);
            });
        }
        l.executed(true);
    }

}

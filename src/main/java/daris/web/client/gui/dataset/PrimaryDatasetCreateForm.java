package daris.web.client.gui.dataset;

import arc.gui.gwt.widget.BaseWidget;
import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.imports.FileUploadTaskManager;
import daris.web.client.model.dataset.PrimaryDatasetCreator;
import daris.web.client.model.dataset.messages.PrimaryDatasetCreateTask;

public class PrimaryDatasetCreateForm extends DatasetCreateForm<PrimaryDatasetCreator> {

    public PrimaryDatasetCreateForm(PrimaryDatasetCreator dc) {
        super(dc);
    }

    @Override
    public void execute(ActionListener l) {
        l.executed(true);
        new PrimaryDatasetCreateTask(creator).execute(r -> {
            if (r != null) {
                // TODO display a message box.
            }
        }, FileUploadTaskManager.get());
        FileUploadTaskManager.get().show(((BaseWidget) gui()).window());
    }
}

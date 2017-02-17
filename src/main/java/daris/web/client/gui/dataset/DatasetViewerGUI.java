package daris.web.client.gui.dataset;

import arc.gui.form.Form;
import daris.web.client.gui.object.DObjectViewerGUI;
import daris.web.client.model.dataset.Dataset;

public class DatasetViewerGUI extends DObjectViewerGUI<Dataset> {

    public static final String CONTENT_METADATA_TAB_NAME = "Method Metadata";

    protected DatasetViewerGUI(Dataset o) {
        super(o);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void appendToInterfaceForm(Form interfaceForm) {

    }

    public static DatasetViewerGUI create(Dataset dataset) {
        // TODO:
        return new DatasetViewerGUI(dataset);
    }

}

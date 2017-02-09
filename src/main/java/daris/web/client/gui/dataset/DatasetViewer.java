package daris.web.client.gui.dataset;

import daris.web.client.gui.object.DObjectViewerGUI;
import daris.web.client.model.dataset.Dataset;

public class DatasetViewer extends DObjectViewerGUI<Dataset>{

    protected DatasetViewer(Dataset o) {
        super(o);
        // TODO Auto-generated constructor stub
    }
    
    public static DatasetViewer create(Dataset dataset) {
        // TODO:
        return new DatasetViewer(dataset);
    }

}

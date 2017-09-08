package daris.web.client.gui.dataset;

import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.model.dataset.Dataset;

public class DatasetUpdateForm<T extends Dataset> extends DObjectUpdateForm<T> {

    protected DatasetUpdateForm(T o) {
        super(o);
        // TODO Auto-generated constructor stub
    }

    public static <T extends Dataset> DatasetUpdateForm<T> create(T obj) {
        // TODO
        return null;
    }

}

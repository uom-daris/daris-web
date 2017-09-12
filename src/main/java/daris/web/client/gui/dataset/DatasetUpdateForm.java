package daris.web.client.gui.dataset;

import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DatasetUpdater;

public abstract class DatasetUpdateForm<T extends Dataset, U extends DatasetUpdater<T>> extends DObjectUpdateForm<T, U> {

    protected DatasetUpdateForm(T o) {
        super(o);
        // TODO Auto-generated constructor stub
    }

    public static <T extends Dataset, U extends DatasetUpdater<T>> DatasetUpdateForm<T, U> create(T obj) {
        // TODO
        return null;
    }

}

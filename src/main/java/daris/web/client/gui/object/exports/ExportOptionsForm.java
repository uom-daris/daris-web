package daris.web.client.gui.object.exports;

import arc.gui.ValidatedInterfaceComponent;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.exports.ExportOptions;

public abstract class ExportOptionsForm<T extends ExportOptions> extends ValidatedInterfaceComponent {

    protected DObject object;
    protected String where;
    protected CollectionSummary summary;
    protected T options;

    public ExportOptionsForm(String where, CollectionSummary summary, T options) {
        this(null, where, summary, options);
    }

    public ExportOptionsForm(DObject object, CollectionSummary summary, T options) {
        this(object, null, summary, options);
    }

    protected ExportOptionsForm(DObject object, String where, CollectionSummary summary, T options) {
        this.object = object;
        this.where = where;
        this.summary = summary;
        this.options = options;
    }

}

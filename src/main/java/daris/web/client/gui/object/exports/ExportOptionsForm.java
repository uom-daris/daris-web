package daris.web.client.gui.object.exports;

import arc.gui.ValidatedInterfaceComponent;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.exports.ExportOptions;

public abstract class ExportOptionsForm<T extends ExportOptions> extends ValidatedInterfaceComponent {

    protected DObject object;
    protected CollectionSummary summary;
    protected T options;

    protected ExportOptionsForm(DObject object, CollectionSummary summary, T options) {
        this.object = object;
        this.summary = summary;
        this.options = options;
    }

}

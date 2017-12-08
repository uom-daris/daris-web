package daris.web.client.gui.object.exports;

import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.object.BackgroundObjectMessageResponse;
import daris.web.client.gui.background.BackgroundServiceMonitor;
import daris.web.client.gui.sink.SinkForm;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.sink.exports.AssetSinkSend;

public class SinkSendForm extends SinkForm implements AsynchronousAction {

    public SinkSendForm(DObject o, CollectionSummary summary) {
        super(o, summary);
    }

    @Override
    public void execute(ActionListener l) {
        new AssetSinkSend(root(), sink()).send(new BackgroundObjectMessageResponse() {

            @Override
            public void responded(Long id) {
                l.executed(id != null);
                new BackgroundServiceMonitor(id).show(window());
            }
        });
    }

}

package daris.web.client.gui.object.exports;

import java.util.ArrayList;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import daris.web.client.gui.sink.SinkForm;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class SinkSendAction extends ActionInterface<DObject> {

    private CollectionSummary _summary;

    public SinkSendAction(DObjectRef o, CollectionSummary summary, arc.gui.window.Window owner) {
        super(o.referentTypeName(), o, new ArrayList<ActionPrecondition>(), owner,
                WindowUtil.calcWindowWidth(owner, 0.7), WindowUtil.calcWindowHeight(owner, 0.75));
        _summary = summary;
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        object().resolve(o -> {
            ch.created(new SinkSendForm(o, _summary));
        });
    }

    @Override
    public String actionName() {
        return "Send";
    }

    @Override
    public String title() {
        return actionName() + " " + ((DObjectRef) object()).typeAndId() + " to sink...";
    }

}

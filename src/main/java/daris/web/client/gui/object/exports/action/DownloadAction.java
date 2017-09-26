package daris.web.client.gui.object.exports.action;

import java.util.ArrayList;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import daris.web.client.gui.object.exports.DownloadOptionsForm;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DownloadAction extends ActionInterface<DObject> {

    private CollectionSummary _summary;

    public DownloadAction(DObjectRef o, CollectionSummary summary, arc.gui.window.Window owner) {
        super(o.referentTypeName(), o, new ArrayList<ActionPrecondition>(), owner, 420, 280);
        _summary = summary;
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        object().resolve(o -> {
            ch.created(new DownloadOptionsForm(o, _summary));
        });
    }

    @Override
    public String actionName() {
        return "Download";
    }

    @Override
    public String title() {
        return actionName() + " " + ((DObjectRef) object()).typeAndId();
    }

}

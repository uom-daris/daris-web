package daris.web.client.gui.object.exports;

import java.util.ArrayList;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DownloadAction extends ActionInterface<DObject> {

    private String _where;
    private CollectionSummary _summary;

    public DownloadAction(DObjectRef o, CollectionSummary summary, arc.gui.window.Window owner) {
        super(o.referentTypeName(), o, new ArrayList<ActionPrecondition>(), owner,
                WindowUtil.calcWindowWidth(owner, 0.5), WindowUtil.calcWindowHeight(owner, 0.5));
        _summary = summary;
    }

    public DownloadAction(String where, CollectionSummary summary, arc.gui.window.Window owner) {
        super("object", new ArrayList<ActionPrecondition>(), owner, WindowUtil.calcWindowWidth(owner, 0.5),
                WindowUtil.calcWindowHeight(owner, 0.5));
        _where = where;
        _summary = summary;
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        if (object() != null) {
            object().resolve(o -> {
                ch.created(new DownloadOptionsForm(o, null, _summary));
            });
        } else {
            ch.created(new DownloadOptionsForm(null, _where, _summary));
        }
    }

    @Override
    public String actionName() {
        return "Download";
    }

    @Override
    public String title() {
        if (object() != null) {
            return actionName() + " " + ((DObjectRef) object()).typeAndId();
        } else {
            return actionName() + " collection";
        }
    }

}

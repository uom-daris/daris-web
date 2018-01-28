package daris.web.client.gui.object.exports;

import arc.gui.dialog.DialogProperties;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.mf.client.util.Action;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class ShareAction implements Action {

    private DObjectRef _o;
    private String _where;
    private CollectionSummary _summary;

    private arc.gui.window.Window _owner;

    public ShareAction(DObjectRef o, String where, CollectionSummary summary, arc.gui.window.Window owner) {
        _o = o;
        _where = where;
        _summary = summary;
        _owner = owner;
    }

    public ShareAction(String where, CollectionSummary summary, arc.gui.window.Window owner) {
        this(null, where, summary, owner);
    }

    @Override
    public void execute() {
        if (_o != null) {
            _o.resolve(o -> {
                showDialog(o);
            });
        } else {
            showDialog(null);
        }
    }

    private void showDialog(DObject object) {
        ShareOptionsForm form = new ShareOptionsForm(object, _where, _summary);
        DialogProperties dp = new DialogProperties(DialogProperties.Type.ACTION, "Generate Download URL", form);
        dp.setButtonAction(form);
        dp.setButtonLabel("Generate");
        dp.setActionEnabled(true);
        dp.setModal(false);
        dp.setOwner(_owner);
        dp.setCancelLabel("Close");
        dp.setSize(650, 430);
        Dialog.postDialog(dp).show();
    }

}

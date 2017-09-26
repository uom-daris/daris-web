package daris.web.client.gui.object.exports.action;

import arc.gui.dialog.DialogProperties;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.mf.client.util.Action;
import daris.web.client.gui.object.exports.ShareOptionsForm;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class ShareAction implements Action {

    private DObjectRef _o;
    private CollectionSummary _summary;

    private arc.gui.window.Window _owner;

    public ShareAction(DObjectRef o, CollectionSummary summary, arc.gui.window.Window owner) {
        _o = o;
        _summary = summary;
        _owner = owner;
    }

    @Override
    public void execute() {
        _o.resolve(o -> {
            showDialog(o);
        });
    }

    private void showDialog(DObject object) {
        ShareOptionsForm form = new ShareOptionsForm(object, _summary);
        DialogProperties dp = new DialogProperties(DialogProperties.Type.ACTION, "Generate Download URL",
                form);
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

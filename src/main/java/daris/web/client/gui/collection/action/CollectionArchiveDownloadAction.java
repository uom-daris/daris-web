package daris.web.client.gui.collection.action;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.dialog.DialogProperties;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.util.Action;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.Validity;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.gui.collection.CollectionArchiveOptionsForm;
import daris.web.client.model.collection.messages.CollectionArchiveCreate;
import daris.web.client.model.object.DObjectRef;

public class CollectionArchiveDownloadAction extends CollectionArchiveOptionsForm implements AsynchronousAction, Action {

    private VerticalPanel _vp;
    private HTML _status;

    public CollectionArchiveDownloadAction(DObjectRef obj, arc.gui.window.Window owner) {
        super(obj, owner);

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _vp.add(super.gui());

        _status = new HTML();
        _status.setWidth100();
        _status.setHeight(22);
        _status.setPaddingLeft(15);
        _status.setColour(RGB.RED);
        _vp.add(_status);
    }

    @Override
    public Validity valid() {
        Validity validity = super.valid();
        if (!validity.valid()) {
            _status.setHTML(validity.reasonForIssue());
            return validity;
        }
        if (validity.valid()) {
            _status.clear();
        } else {
            _status.setHTML(validity.reasonForIssue());
        }
        return validity;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(final ActionListener al) {
        CollectionArchiveCreate msg = new CollectionArchiveCreate(object(), archiveOptions());
        msg.send(new ObjectMessageResponse<Null>() {

            @Override
            public void responded(Null r) {
                al.executed(true);
            }
        });
    }

    @Override
    public void execute() {
        /*
         * show dialog
         */
        DialogProperties dp = new DialogProperties(DialogProperties.Type.ACTION,
                "Download " + object().referentTypeName() + " " + object().citeableId(), this);
        dp.setButtonAction(this);
        dp.setButtonLabel("Download");
        dp.setActionEnabled(true);
        dp.setModal(true);
        dp.setOwner(ownerWindow());
        dp.setCancelLabel("Cancel");
        dp.setSize(640, 280);
        Dialog.postDialog(dp).show();
    }

}
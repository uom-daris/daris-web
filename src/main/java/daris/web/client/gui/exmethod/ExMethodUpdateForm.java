package daris.web.client.gui.exmethod;

import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.exmethod.ExMethodUpdater;
import daris.web.client.model.exmethod.messages.ExMethodUpdate;

public class ExMethodUpdateForm extends DObjectUpdateForm<ExMethod> {

    public ExMethodUpdateForm(ExMethod o) {
        super(o);
    }

    private ExMethodUpdater updater() {
        return (ExMethodUpdater) updater;
    }

    @Override
    public void execute(ActionListener l) {
        new ExMethodUpdate(updater()).send(r -> {
            MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                    "Ex-method " + object.citeableId() + " has been updated.", 3);
        });
        l.executed(true);
    }

}

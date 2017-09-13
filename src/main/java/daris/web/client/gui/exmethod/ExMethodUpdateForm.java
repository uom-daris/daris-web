package daris.web.client.gui.exmethod;

import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.exmethod.ExMethodUpdater;
import daris.web.client.model.exmethod.messages.ExMethodUpdate;

public class ExMethodUpdateForm extends DObjectUpdateForm<ExMethod, ExMethodUpdater> {

    public ExMethodUpdateForm(ExMethod o) {
        super(o);
    }

    @Override
    public void execute(ActionListener l) {
        new ExMethodUpdate(updater).send(r -> {
            // TODO: fade out message;
        });
        l.executed(true);
    }

}

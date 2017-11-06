package daris.web.client.gui.sink;

import arc.gui.gwt.widget.wizard.Wizard;
import arc.gui.gwt.widget.wizard.WizardPage;
import arc.mf.client.future.Future;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObjectRef;

public class SinkWizard extends Wizard {

    private DObjectRef _root;
    private CollectionSummary _summary;

    private SinkSelectForm _select;
    private SinkSettingsForm _settings;

    public SinkWizard(DObjectRef root, CollectionSummary summary) {
        _root = root;
        _summary = summary;

        _select = new SinkSelectForm();
        add(new WizardPage("Select a sink", "Select a data sink as destination", _select));
        add(new WizardPage("Sink settings", "Settings of the sink") {
            protected void enter(Wizard w) {
                if (_settings == null) {
//                    _settings = new SinkSettingsForm(_select.selectedSink());
                    setInterface(_settings);
                }
            }
        });

    }

    @Override
    protected Future<Boolean> execute() throws Throwable {
        // TODO Auto-generated method stub
        new Future<Boolean>() {

            @Override
            protected void doFutureWork() throws Throwable {
                // TODO Auto-generated method stub

            }
        };
        return null;
    }

}

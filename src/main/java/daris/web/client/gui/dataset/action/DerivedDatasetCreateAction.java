package daris.web.client.gui.dataset.action;

import java.util.HashMap;
import java.util.Map;

import arc.gui.InterfaceCreateHandler;
import arc.gui.window.Window;
import daris.web.client.gui.dataset.DerivedDatasetCreateForm;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DerivedDatasetCreator;
import daris.web.client.model.object.DObjectRef;

public class DerivedDatasetCreateAction extends DatasetCreateAction {

    private Map<String, String> _inputs;

    public DerivedDatasetCreateAction(DObjectRef study, Map<String, String> inputs, Window owner, double width,
            double height) {
        super(study, owner, width, height);
        _inputs = inputs;
    }

    public DerivedDatasetCreateAction(Dataset input, Window owner, double width, double height) {
        this(new DObjectRef(CiteableIdUtils.parent(input.citeableId()), -1), new HashMap<String, String>(), owner,
                width, height);
        _inputs.put(input.citeableId(), input.vid());
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new DerivedDatasetCreateForm(new DerivedDatasetCreator(parentObject(), _inputs)));
    }

    public String title() {
        return "Create derived dataset";
    }

}

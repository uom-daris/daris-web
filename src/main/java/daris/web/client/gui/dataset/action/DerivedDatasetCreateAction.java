package daris.web.client.gui.dataset.action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.action.precondition.EvaluatePrecondition;
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
        _inputs = new LinkedHashMap<String, String>();
        if (inputs != null && !inputs.isEmpty()) {
            _inputs.putAll(inputs);
        }
    }

    public DerivedDatasetCreateAction(Window owner, double width, double height, Dataset inputDataset) {
        this(new DObjectRef(CiteableIdUtils.parent(inputDataset.citeableId()), -1), owner, width, height, inputDataset);
    }

    public DerivedDatasetCreateAction(DObjectRef study, Window owner, double width, double height,
            Dataset inputDataset) {
        this(study, new HashMap<String, String>(), owner, width, height);
        _inputs.put(inputDataset.citeableId(), inputDataset.contentVid());
    }

    public DerivedDatasetCreateAction(Window owner, double width, double height, DObjectRef inputDataset) {
        this(new DObjectRef(CiteableIdUtils.parent(inputDataset.citeableId()), -1), owner, width, height, inputDataset);
    }

    public DerivedDatasetCreateAction(DObjectRef study, Window owner, double width, double height,
            DObjectRef inputDataset) {
        this(study, new HashMap<String, String>(), owner, width, height);
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Resolving vid of the input dataset";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                inputDataset.resolve(o -> {
                    if (o != null) {
                        _inputs.put(o.citeableId(), ((Dataset) o).contentVid());
                        l.executed(ActionPreconditionOutcome.PASS, "resolved vid of input dataset");
                    } else {
                        l.executed(ActionPreconditionOutcome.FAIL, "failed to resolve input dataset");
                    }
                });
            }
        });
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new DerivedDatasetCreateForm(new DerivedDatasetCreator(parentObject(), _inputs)));
    }

    public String title() {
        return "Create derived dataset";
    }

}

package daris.web.client.gui.object.action;

import java.util.ArrayList;
import java.util.List;

import arc.gui.object.action.UpdateActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.action.precondition.EvaluatePrecondition;
import arc.gui.window.Window;
import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public abstract class DObjectUpdateAction<T extends DObject> extends UpdateActionInterface<DObject> {

    protected DObjectUpdateAction(DObjectRef o, Window owner, double w, double h) {
        super(o, new ArrayList<ActionPrecondition>(), owner, WindowUtil.calcWindowWidth(owner, w),
                WindowUtil.calcWindowHeight(owner, h));
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Checking if you have sufficient privilege to modify " + obj().referentTypeName() + " "
                        + obj().citeableId();
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                XmlStringWriter w = new XmlStringWriter();
                w.add("cid", obj().citeableId());
                Session.execute("om.pssd.user.can.modify", w.document(), new ServiceResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                        boolean can = xe.booleanValue("can", false);
                        if (can) {
                            l.executed(ActionPreconditionOutcome.PASS, null);
                        } else {
                            l.executed(ActionPreconditionOutcome.FAIL, "Insufficient privilege to modify "
                                    + obj().referentTypeName() + " " + obj().citeableId());
                        }
                    }
                });
            }
        });
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Resolving metadata template of " + obj().referentTypeName() + " " + obj().citeableId();
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                if (obj().forEdit() && obj().resolved() && obj().referent() != null) {
                    l.executed(ActionPreconditionOutcome.PASS,
                            "Resolved metadata template of " + obj().referentTypeName() + " " + obj().citeableId());
                    return;
                }
                obj().setForEdit(true);
                obj().resolve(o -> {
                    l.executed(ActionPreconditionOutcome.PASS,
                            "Resolved " + obj().referentTypeName() + " " + obj().citeableId());
                    return;
                });
            }
        });
    }

    protected DObjectRef obj() {
        return (DObjectRef) object();
    }

    protected void addPrecondition(ActionPrecondition precondition) {
        preconditions().add(precondition);
    }

}

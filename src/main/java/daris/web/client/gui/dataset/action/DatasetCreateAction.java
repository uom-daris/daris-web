package daris.web.client.gui.dataset.action;

import java.util.List;

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
import daris.web.client.gui.object.action.DObjectCreateAction;
import daris.web.client.model.object.DObjectRef;

public abstract class DatasetCreateAction extends DObjectCreateAction {

    public DatasetCreateAction(DObjectRef parentObj, Window owner, double width, double height) {
        super(parentObj, owner, width, height);
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Checking if you have sufficient privilege to create dataset...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                XmlStringWriter w = new XmlStringWriter();
                w.add("pid", parentObj.citeableId());
                Session.execute("om.pssd.user.can.create", w.document(), new ServiceResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                        boolean can = xe.booleanValue("can", false);
                        if (can) {
                            l.executed(ActionPreconditionOutcome.PASS, null);
                        } else {
                            l.executed(ActionPreconditionOutcome.FAIL, "Insufficient privilege to create dataset.");
                        }
                    }
                });
            }
        });
    }

}

package daris.web.client.gui.object.action;

import java.util.ArrayList;
import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.DestroyActionInterface;
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
import daris.web.client.gui.object.DObjectDestroyForm;
import daris.web.client.gui.util.WindowUtil;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DObjectDestroyAction extends DestroyActionInterface<DObject> {

    private CollectionSummary _summary;

    public DObjectDestroyAction(DObjectRef o, CollectionSummary summary, Window owner) {
        super(o, new ArrayList<ActionPrecondition>(), owner, WindowUtil.calcWindowWidth(owner, 0.5),
                WindowUtil.calcWindowHeight(owner, 0.6));
        _summary = summary;
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Checking if user has sufficient privilege to destroy " + o.typeAndId();
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                XmlStringWriter w = new XmlStringWriter();
                w.add("cid", o.citeableId());
                Session.execute("om.pssd.user.can.destroy", w.document(), new ServiceResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                        boolean can = xe.booleanValue("can", false);
                        if (can) {
                            l.executed(ActionPreconditionOutcome.PASS, null);
                        } else {
                            l.executed(ActionPreconditionOutcome.FAIL,
                                    "Insufficient privilege to destroy " + o.typeAndId());
                        }
                    }
                });

            }
        });
    }

    protected void addPrecondition(ActionPrecondition precondition) {
        preconditions().add(precondition);
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ((DObjectRef) object()).resolve(o -> {
            ch.created(new DObjectDestroyForm(o, _summary));
        });
    }

    public boolean needToLock() {
        return false;
    }

}

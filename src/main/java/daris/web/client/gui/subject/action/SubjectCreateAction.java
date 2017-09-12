package daris.web.client.gui.subject.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.action.precondition.EvaluatePrecondition;
import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.gui.object.action.DObjectCreateAction;
import daris.web.client.gui.subject.SubjectCreateForm;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.project.Project;
import daris.web.client.model.subject.SubjectCreator;

public class SubjectCreateAction extends DObjectCreateAction {

    private SubjectCreator _creator;

    public SubjectCreateAction(DObjectRef parent, arc.gui.window.Window owner, double width, double height) {
        super(parent, owner, width, height);
        _creator = new SubjectCreator(parent);
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Checking if you have sufficient privilege to create subject...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                XmlStringWriter w = new XmlStringWriter();
                w.add("pid", parent.citeableId());
                Session.execute("om.pssd.user.can.create", w.document(), new ServiceResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                        boolean can = xe.booleanValue("can", false);
                        if (can) {
                            l.executed(ActionPreconditionOutcome.PASS, null);
                        } else {
                            l.executed(ActionPreconditionOutcome.FAIL, "Insufficient privilege to create subject.");
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
                return "Resolving project methods...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                parent.resolve(o -> {
                    if (o != null) {
                        Project p = (Project) o;
                        if (p.methods() != null && !p.methods().isEmpty()) {
                            l.executed(ActionPreconditionOutcome.PASS, "Resolved project methods.");
                            return;
                        }
                    }
                    l.executed(ActionPreconditionOutcome.FAIL, "Project has no method set.");
                });
            }
        });
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ch.created(new SubjectCreateForm(_creator));
    }

}

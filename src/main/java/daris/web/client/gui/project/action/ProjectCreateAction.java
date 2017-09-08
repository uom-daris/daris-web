package daris.web.client.gui.project.action;

import java.util.List;

import arc.gui.InterfaceCreateHandler;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.action.precondition.EvaluatePrecondition;
import arc.gui.window.Window;
import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.gui.object.action.DObjectCreateAction;
import daris.web.client.gui.project.ProjectCreateForm;
import daris.web.client.model.project.ProjectAssetNamespaceSetRef;
import daris.web.client.model.project.ProjectCiteableRootNameSetRef;
import daris.web.client.model.project.ProjectCreator;
import daris.web.client.model.project.ProjectMetadataRef;

public class ProjectCreateAction extends DObjectCreateAction {

    private ProjectCreator _creator;

    private ProjectMetadataRef _m;

    public ProjectCreateAction(Window owner, double width, double height) {
        super(null, owner, width, height);
        _creator = new ProjectCreator();
        _m = new ProjectMetadataRef();
        addPrecondition(new ActionPrecondition() {

            @Override
            public EvaluatePrecondition evaluate() {
                return EvaluatePrecondition.BEFORE_INTERACTION;
            }

            @Override
            public String description() {
                return "Checking if you have sufficient privilege to create project...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                Session.execute("om.pssd.user.can.create", new ServiceResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                        boolean can = xe.booleanValue("can", false);
                        if (can) {
                            l.executed(ActionPreconditionOutcome.PASS, null);
                        } else {
                            l.executed(ActionPreconditionOutcome.FAIL, "Insufficient privilege to create project.");
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
                return "Resolving project metadata template...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                _m.resolve(meta -> {
                    _creator.setMetadataForEdit(meta);
                    l.executed(ActionPreconditionOutcome.PASS, null);
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
                return "Resolving available citeable id root names...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                ProjectCiteableRootNameSetRef.get().resolve(cidRootNames -> {
                    _creator.setAvailableCidRootNames(cidRootNames);
                    l.executed(ActionPreconditionOutcome.PASS, null);
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
                return "Resolving available asset namespaces...";
            }

            @Override
            public void execute(ActionPreconditionListener l) {
                ProjectAssetNamespaceSetRef.get().resolve(namespaces -> {
                    _creator.setAvailableAssetNamespaces(namespaces);
                    l.executed(ActionPreconditionOutcome.PASS, null);
                });
            }
        });
    }

    @Override
    public void createInterface(InterfaceCreateHandler ch) {
        ProjectCiteableRootNameSetRef.get().resolve(cidRootNames -> {
            _creator.setAvailableCidRootNames(cidRootNames);
            ProjectAssetNamespaceSetRef.get().resolve(namespaces -> {
                _creator.setAvailableAssetNamespaces(namespaces);
                _m.resolve(meta -> {
                    _creator.setMetadataForEdit(meta);
                    ch.created(new ProjectCreateForm(_creator));
                });
            });
        });
    }
}

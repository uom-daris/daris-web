package daris.web.client.gui.project.user;

import arc.gui.dialog.DialogProperties;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import daris.web.client.model.project.Project;
import daris.web.client.model.project.messages.ProjectUserSet;

public class ProjectUserDialog {

    private Project _project;

    private ProjectUserSet _msg;
    private ProjectUserForm _form;

    public ProjectUserDialog(Project project) {
        _project = project;
        _msg = new ProjectUserSet(project);
        _form = new ProjectUserForm(project.users(), project.roleUsers());
        _form.addChangeListener(() -> {
            _msg.setUsers(_form.users());
            _msg.setRoleUsers(_form.roleUsers());
        });
    }

    public void show(arc.gui.window.Window owner, ActionListener al) {
        DialogProperties dp = new DialogProperties("Project " + _project.citeableId() + " Users", _form);
        dp.setActionEnabled(false);
        dp.setOwner(owner);
        dp.setButtonAction(new AsynchronousAction() {

            @Override
            public void execute(ActionListener l) {
                _msg.send(r -> {
                    l.executed(true);
                });
            }
        });
        dp.setCancelLabel("Cancel");
        dp.setButtonLabel("Apply");
        dp.setSize(1024, 500);
        dp.setModal(false);
        Dialog dlg = Dialog.postDialog(dp, al);
        dlg.show();
    }

}

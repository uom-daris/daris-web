package daris.web.client.model.project.messages;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.project.Project;
import daris.web.client.model.project.ProjectRoleUser;
import daris.web.client.model.project.ProjectUser;

public class ProjectUserSet extends ObjectMessage<Null> {

    private String _projectCid;
    private List<ProjectUser> _users;
    private List<ProjectRoleUser> _roleUsers;

    public ProjectUserSet(String projectCid, List<ProjectUser> users, List<ProjectRoleUser> roleUsers) {
        _projectCid = projectCid;
        _users = users;
        _roleUsers = roleUsers;
    }

    public ProjectUserSet(Project project) {
        this(project.citeableId(), project.users(), project.roleUsers());
    }

    public void setUsers(List<ProjectUser> users) {
        _users = users;
    }

    public void setRoleUsers(List<ProjectRoleUser> roleUsers) {
        _roleUsers = roleUsers;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_users != null) {
            for (ProjectUser u : _users) {
                w.push("user");
                w.add("domain", u.user().domain().name());
                w.add("user", u.user().name());
                if (u.user().domain().authority() != null) {
                    w.add("authority", new String[] { "protocol", u.user().domain().authority().protocol() },
                            u.user().domain().authority().name());
                }
                w.add("role", u.role().toString());
                if (u.dataUse() != null) {
                    w.add("data-use", u.dataUse().toString());
                }
                w.pop();
            }
        }

        if (_roleUsers != null) {
            for (ProjectRoleUser ru : _roleUsers) {
                w.push("role-user");
                w.add("name", ru.name());
                w.add("role", ru.role().toString());
                if (ru.dataUse() != null) {
                    w.add("data-use", ru.dataUse().toString());
                }
                w.pop();
            }
        }
    }

    @Override
    protected String messageServiceName() {
        return "daris.project.user.set";
    }

    @Override
    protected Null instantiate(XmlElement xe) throws Throwable {
        return new Null();
    }

    @Override
    protected String objectTypeName() {
        return null;
    }

    @Override
    protected String idToString() {
        return _projectCid;
    }

}

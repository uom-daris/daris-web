package daris.web.client.model.user;

import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.project.Project;
import daris.web.client.model.user.messages.ActorSelfHave;

public class UserSelf {

    private static Boolean _isAdmin;

    public static void isAdministrator(ObjectResolveHandler<Boolean> rh) {
        if (_isAdmin != null) {
            rh.resolved(_isAdmin);
            return;
        }
        new ActorSelfHave(Roles.SYSTEM_ADMINISTRATOR, "role").send(r -> {
            _isAdmin = r;
            rh.resolved(_isAdmin);
        });
    }

    public static void isProjectAdmin(String projectCID, ObjectResolveHandler<Boolean> rh) {
        final String projectAdminRoleName = Project.projectAdminRoleOf(projectCID);
        XmlStringWriter w = new XmlStringWriter();
        w.add("role", new String[] { "type", "role" }, Roles.SYSTEM_ADMINISTRATOR);
        w.add("role", new String[] { "type", "role" }, projectAdminRoleName);
        Session.execute("actor.self.have", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                boolean isSysAdmin = xe.booleanValue("role[@name='" + Roles.SYSTEM_ADMINISTRATOR + "']", false);
                boolean isPrjAdmin = xe.booleanValue("role[@name='" + projectAdminRoleName + "']", false);
                rh.resolved(isSysAdmin || isPrjAdmin);
            }
        });
    }
    
    public static void isProjectAdmin(Project project, ObjectResolveHandler<Boolean> rh) {
        isProjectAdmin(project.citeableId(), rh);
    }

}

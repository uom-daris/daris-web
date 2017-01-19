package daris.web.client.model.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.object.DObject;

public class Project extends DObject {

    private List<MethodRef> _methods;
    private DataUse _dataUse;
    private List<ProjectUser> _users;
    private List<ProjectRoleUser> _roleUsers;

    public Project(XmlElement oe) {
        super(oe);

        /*
         * data-use
         */
        _dataUse = DataUse.fromString(oe.value("data-use"));

        /*
         * methods
         */
        List<XmlElement> mes = oe.elements("method");
        if (mes != null && !mes.isEmpty()) {
            _methods = new Vector<MethodRef>(mes.size());
            for (XmlElement me : mes) {
                _methods.add(new MethodRef(me.value("id"), me.value("name"), me.value("description")));
            }
        }
        /*
         * members
         */
        List<XmlElement> ues = oe.elements("member");
        if (ues != null && !ues.isEmpty()) {
            _users = new ArrayList<ProjectUser>(ues.size());
            for (XmlElement ue : ues) {
                _users.add(new ProjectUser(ue));
            }
        }
        /*
         * role-members
         */
        List<XmlElement> rues = oe.elements("role-member");
        if (rues != null && !rues.isEmpty()) {
            _roleUsers = new ArrayList<ProjectRoleUser>(rues.size());
            for (XmlElement rue : rues) {
                _roleUsers.add(new ProjectRoleUser(rue));
            }
        }
    }

    @Override
    public Type type() {
        return DObject.Type.PROJECT;
    }

    public DataUse dataUse() {
        return _dataUse;
    }

    public List<MethodRef> methods() {
        return _methods;
    }

    public List<ProjectUser> users() {
        return _users;
    }

    public List<ProjectRoleUser> roleUsers() {
        return _roleUsers;
    }

}

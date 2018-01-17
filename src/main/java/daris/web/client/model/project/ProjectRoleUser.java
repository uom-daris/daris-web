package daris.web.client.model.project;

import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import daris.web.client.model.user.RoleUser;

public class ProjectRoleUser implements Comparable<ProjectRoleUser> {

    private String _roleId;
    private String _roleName;
    private ProjectRoleType _role;
    private DataUse _dataUse;

    public ProjectRoleUser(XmlElement rme) {

        _roleId = rme.value("@id");
        _roleName = rme.value("@name");
        _role = ProjectRoleType.fromString(rme.value("@role"));
        _dataUse = DataUse.fromString(rme.value("@data-use"));
    }

    private ProjectRoleUser(String roleId, String roleName, ProjectRoleType role, DataUse dataUse) {
        _roleId = roleId;
        _roleName = roleName;
        _role = role;
        _dataUse = dataUse;
    }

    public ProjectRoleUser(String roleName, ProjectRoleType role, DataUse dataUse) {
        this(null, roleName, role, dataUse);
    }

    public ProjectRoleUser(RoleUser ru, ProjectRoleType role, DataUse dataUse) {
        this(ru.id(), ru.name(), role, dataUse);
    }

    public ProjectRoleUser(RoleUser ru) {
        this(ru.id(), ru.name(), ProjectRoleType.MEMBER, DataUse.UNSPECIFIED);
    }

    public String name() {

        return _roleName;
    }

    public String id() {
        return _roleId;
    }

    public ProjectRoleType role() {

        return _role;
    }

    public void setRole(ProjectRoleType role) {
        _role = role;
    }

    public DataUse dataUse() {

        return _dataUse;
    }

    public void setDataUse(DataUse dataUse) {
        _dataUse = dataUse;
    }

    public String toHTML() {

        String html = "<table><thead><tr><th align=\"center\" colspan=\"2\">Role Member</th></tr><thead>";
        html += "<tbody>";
        html += "<tr><td><b>id:</b></td><td>" + _roleId + "</td></tr>";
        html += "<tr><td><b>name:</b></td><td>" + _roleName + "</td></tr>";
        html += "<tr><td><b>role:</b></td><td>" + _role + "</td></tr>";
        if (_dataUse != null) {
            html += "<tr><td><b>data-use:</b></td><td>" + _dataUse + "</td></tr>";
        }
        html += "</tbody></table>";
        return html;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o instanceof ProjectRoleUser) {
            ProjectRoleUser prm = (ProjectRoleUser) o;
            return _roleName.equals(prm.name()) && _role.equals(prm.role())
                    && ObjectUtil.equals(_dataUse, prm.dataUse());
        }
        return false;
    }

    @Override
    public int compareTo(ProjectRoleUser o) {
        if (o == null) {
            return 1;
        }
        if (_role.ordinal() > o.role().ordinal()) {
            return 1;
        }
        if (_role.ordinal() < o.role().ordinal()) {
            return -1;
        }
        return _roleName.compareTo(o.name());
    }

    public ProjectRoleUser copy() {
        return new ProjectRoleUser(_roleId, _roleName, _role, _dataUse);
    }
}

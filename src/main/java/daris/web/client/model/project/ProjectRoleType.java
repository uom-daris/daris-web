package daris.web.client.model.project;

import java.util.ArrayList;
import java.util.List;

import arc.gui.gwt.widget.combo.ComboBoxEntry;

public enum ProjectRoleType {

    PROJECT_ADMINISTRATOR, SUBJECT_ADMINISTRATOR, MEMBER, GUEST;

    @Override
    public String toString() {

        return super.toString().toLowerCase().replace('_', '-');
    }

    public static ProjectRoleType fromString(String role) {

        if (role != null) {
            ProjectRoleType[] vs = values();
            for (ProjectRoleType v : vs) {
                if (v.toString().equalsIgnoreCase(role)) {
                    return v;
                }
            }
        }
        return null;
    }

    public static List<ComboBoxEntry<ProjectRoleType>> comboBoxEntries() {
        ProjectRoleType[] vs = values();
        List<ComboBoxEntry<ProjectRoleType>> entries = new ArrayList<ComboBoxEntry<ProjectRoleType>>(vs.length);
        for (ProjectRoleType v : vs) {
            entries.add(new ComboBoxEntry<ProjectRoleType>(v));
        }
        return entries;
    }

}

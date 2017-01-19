package daris.web.client.model.project;

public enum ProjectRole {

    PROJECT_ADMINISTRATOR, SUBJECT_ADMINISTRATOR, MEMBER, GUEST;

    @Override
    public String toString() {

        return super.toString().toLowerCase().replace('_', '-');
    }

    public static ProjectRole fromString(String role) {

        if (role != null) {
            ProjectRole[] vs = values();
            for (ProjectRole v : vs) {
                if (v.toString().equalsIgnoreCase(role)) {
                    return v;
                }
            }
        }
        return null;
    }

}

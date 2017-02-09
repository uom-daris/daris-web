package daris.web.client.model.exmethod;

public enum State {
    abandoned, complete, incomplete, waiting;

    public static State fromString(String str) {
        if (str != null) {
            State[] vs = values();
            for (int i = 0; i < vs.length; i++) {
                if (vs[i].toString().equalsIgnoreCase(str)) {
                    return vs[i];
                }
            }
        }
        return null;
    }

}

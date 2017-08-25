package daris.web.client.model;

import daris.web.client.model.object.DObject;

public class CiteableIdUtils {

    public static final int PROJECT_CID_DEPTH = 3;
    public static final int SUBJECT_CID_DEPTH = 4;
    public static final int EX_METHOD_CID_DEPTH = 5;
    public static final int STUDY_CID_DEPTH = 6;
    public static final int DATASET_CID_DEPTH = 7;

    public static boolean isProject(String cid) {
        return isValid(cid) && depth(cid) == PROJECT_CID_DEPTH;
    }

    public static boolean isSubject(String cid) {
        return isValid(cid) && depth(cid) == SUBJECT_CID_DEPTH;
    }

    public static boolean isExMethod(String cid) {
        return isValid(cid) && depth(cid) == EX_METHOD_CID_DEPTH;
    }

    public static boolean isStudy(String cid) {
        return isValid(cid) && depth(cid) == STUDY_CID_DEPTH;
    }

    public static boolean isDataset(String cid) {
        return isValid(cid) && depth(cid) == DATASET_CID_DEPTH;
    }

    public static boolean isValid(String cid) {
        if (cid == null) {
            return false;
        }
        return cid.matches("^\\d+(\\d*.)*\\d+$");
    }

    public static int depth(String cid) {
        if (cid == null || cid.length() == 0) {
            return 0;
        }
        int depth = 1;
        int idx = cid.indexOf('.');
        while (idx != -1) {
            depth++;
            idx = cid.indexOf('.', idx + 1);
        }
        return depth;
    }

    public static String parent(String cid, int levels) {
        for (int i = 0; i < levels; i++) {
            cid = parent(cid);
        }
        return cid;
    }

    public static String ordinal(String cid) {
        if (cid == null || !isValid(cid)) {
            return null;
        }
        int idx = cid.lastIndexOf('.');
        if (idx == -1) {
            return cid;
        }
        return cid.substring(idx + 1);
    }

    public static String parent(String cid) {
        if (cid == null) {
            return null;
        }
        int idx = cid.lastIndexOf('.');
        if (idx == -1) {
            return null;
        }
        return cid.substring(0, idx);
    }

    public static DObject.Type type(String cid) {
        if (cid != null) {
            if (isProject(cid)) {
                return DObject.Type.PROJECT;
            } else if (isSubject(cid)) {
                return DObject.Type.SUBJECT;
            } else if (isExMethod(cid)) {
                return DObject.Type.EX_METHOD;
            } else if (isStudy(cid)) {
                return DObject.Type.STUDY;
            } else if (isDataset(cid)) {
                return DObject.Type.DATASET;
            }
        }
        return null;
    }

    public static boolean isDirectParent(String parent, String child) {
        if (parent != null && child != null && isValid(parent) && isValid(child)) {
            return child.startsWith(parent + ".") && depth(parent) + 1 == depth(child);
        }
        return false;
    }

    public static boolean isDirectChild(String child, String parent) {
        return isDirectParent(parent, child);
    }

    public static int compare(String id1, String id2) {

        assert id1 != null && id2 != null;
        if (id1.equals(id2)) {
            return 0;
        }
        String[] parts1 = id1.split("\\.");
        String[] parts2 = id2.split("\\.");
        if (parts1.length < parts2.length) {
            return -1;
        }
        if (parts1.length > parts2.length) {
            return 1;
        }
        for (int i = 0; i < parts1.length; i++) {
            if (!parts1[i].equals(parts2[i])) {
                long n1 = Long.parseLong(parts1[i]);
                long n2 = Long.parseLong(parts2[i]);
                if (n1 < n2) {
                    return -1;
                }
                if (n1 > n2) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public static boolean isParent(String parent, String child) {
        if (parent != null && child != null) {
            return child.startsWith(parent + ".");
        }
        return false;
    }

    public static boolean isChild(String child, String parent) {
        return isParent(parent, child);
    }

    public static boolean isSibling(String cid1, String cid2) {
        if (cid1 != null && cid2 != null) {
            int depth1 = depth(cid1);
            int depth2 = depth(cid2);
            if (depth1 == depth2) {
                if (depth1 <= 1) {
                    return true;
                } else {
                    return isParent(parent(cid1), cid2);
                }
            }
            return false;
        } else if (cid1 == null && cid2 == null) {
            return true;
        }
        return false;
    }

}

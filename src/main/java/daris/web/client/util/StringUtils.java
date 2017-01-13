package daris.web.client.util;

public class StringUtils {

    public static String upperCaseFirst(String s, boolean lowerCaseRest) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.substring(0, 1).toUpperCase());
        if (lowerCaseRest) {
            sb.append(s.substring(1).toLowerCase());
        } else {
            sb.append(s.substring(1));
        }
        return sb.toString();
    }

    public static String upperCaseFirst(String s) {
        return upperCaseFirst(s, false);
    }

}

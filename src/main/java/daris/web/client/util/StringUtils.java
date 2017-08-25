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

    public static String trimPrefix(String str, String prefix, boolean repeat) {
        String r = str;
        if (repeat) {
            while (r.startsWith(prefix)) {
                r = r.substring(prefix.length());
            }
        } else {
            if (r.startsWith(prefix)) {
                r = r.substring(prefix.length());
            }
        }
        return r;
    }

    public static String trimSuffix(String str, String suffix, boolean repeat) {
        String r = str;
        if (repeat) {
            while (r.endsWith(suffix)) {
                r = r.substring(0, r.length() - suffix.length());
            }
        } else {
            if (r.endsWith(suffix)) {
                r = r.substring(0, r.length() - suffix.length());
            }
        }
        return r;
    }

}

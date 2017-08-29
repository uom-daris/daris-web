package daris.web.client.util;

public class PathUtils {

    public static final String SLASH = "/";

    public static String trimLeadingSlash(String str) {
        return StringUtils.trimPrefix(str, SLASH, true);
    }

    public static String trimTrailingSlash(String str) {
        return StringUtils.trimSuffix(str, SLASH, true);
    }

    public static String trimSlash(String str) {
        return trimLeadingSlash(trimTrailingSlash(str));
    }

    public static String getRelativePath(String path, String basePath) {
        if (path == null || path.isEmpty() || basePath == null || basePath.isEmpty()) {
            return path;
        }
        path = trimSlash(path);
        basePath = trimSlash(basePath);
        if (path.equals(basePath)) {
            return null;
        }
        if (path.startsWith(basePath + "/")) {
            return path.substring(basePath.length() + 1);
        } else {
            return path;
        }
    }

    public static String getParentPath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return null;
        }
        path = trimTrailingSlash(path);
        int i = path.lastIndexOf('/');
        if (i == -1) {
            return null;
        }
        if (i == 0) {
            return "/";
        }
        return path.substring(0, i);
    }

}

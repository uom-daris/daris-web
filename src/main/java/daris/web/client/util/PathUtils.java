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

    public static String relativePath(String path, String basePath) {
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

}

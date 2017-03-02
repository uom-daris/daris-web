package daris.web.client.util;

public class SizeUtil {

    public static String getHumanReadableSize(long nBytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (nBytes < unit) {
            return nBytes + " B";
        }
        int exp = (int) (Math.log(nBytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return NumberUtil.toFixed(nBytes / Math.pow(unit, exp), 1) + " " + pre + "B";
    }

}

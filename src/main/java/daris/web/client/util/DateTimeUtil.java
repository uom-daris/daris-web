package daris.web.client.util;

public class DateTimeUtil {

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SECONDS_PER_HOUR = 3600L;
    public static final long SECONDS_PER_DAY = 86400L;
    public static final long SECONDS_PER_YEAR = 31536000L;

    public static String convertSecondsToHumanReadableTime(double seconds) {
        long year = ((long) seconds) / SECONDS_PER_YEAR;
        if (year > 0) {
            seconds -= year * SECONDS_PER_YEAR;
        }
        long day = ((long) seconds) / SECONDS_PER_DAY;
        if (day > 0) {
            seconds -= day * SECONDS_PER_DAY;
        }
        long hour = ((long) seconds) / SECONDS_PER_HOUR;
        if (hour > 0) {
            seconds -= hour * SECONDS_PER_HOUR;
        }
        long minute = ((long) seconds) / SECONDS_PER_MINUTE;
        if (minute > 0) {
            seconds -= minute * SECONDS_PER_MINUTE;
        }
        StringBuilder sb = new StringBuilder();
        if (year > 0) {
            sb.append(year).append(" year");
            sb.append(" ");
        }
        if (day > 0) {
            sb.append(day).append(" day");
            sb.append(" ");
        }
        if (hour > 0) {
            sb.append(hour).append(" hour");
            sb.append(" ");
        }
        if (minute > 0) {
            sb.append(minute).append(" minute");
            sb.append(" ");
        }
        String s = String.valueOf(seconds);
        int idx = s.indexOf('.');
        if (idx >= 0) {
            s = s.substring(0, idx);
        }
        sb.append(s).append(" seconds");
        return sb.toString().trim();
    }

    public static void main(String[] args) {
//        System.out.println(convertSecondsToHumanReadableTime(1234567.123));
    }

}

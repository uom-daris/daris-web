package daris.web.client.model.query;

public enum DatePeriod {

    TODAY(0, "today"), YESTERDAY(1, "yesterday"), LAST_3_DAYS(3, "last 3 days"), LAST_WEEK(7,
            "last week"), LAST_30_DAYS(30, "last 30 days"), LAST_90_DAYS(90, "last 90 days"), LAST_180_DAYS(180,
                    "last 180 days"), LAST_365_DAYS(365, "last 365 days"), ALL_THE_TIME(-1, "all the time");
    private int _nbDays;
    private String _label;

    DatePeriod(int nbDays, String label) {
        _nbDays = nbDays;
        _label = label;
    }

    public String label() {
        return _label;
    }

    public int numberOfDays() {
        return _nbDays;
    }

    public String toString() {
        return _label;
    }

}

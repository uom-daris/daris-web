package daris.web.client.model.query;

public enum DateAction {

    TIME_PERIOD("time period"), CHOOSE_DATES("choose dates");

    private String _value;

    DateAction(String value) {
        _value = value;
    }

    public String value() {
        return _value;
    }

    public String toString() {
        return _value;
    }

}

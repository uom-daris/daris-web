package daris.web.client.model.query;

public enum SortOrder {

    ASCEND("asc"), DESCEND("desc");

    private String _value;

    SortOrder(String value) {
        _value = value;
    }

    public String value() {
        return _value;
    }

    public final String toString() {
        return value();
    }
}

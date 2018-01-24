package daris.web.client.model.query;

public enum Action {
    GET_VALUE("get-value"), GET_VALUES("get-values");
    private String _value;

    Action(String value) {
        _value = value;
    }

    public String value() {
        return _value;
    }

    @Override
    public String toString() {
        return _value;
    }
}

package daris.web.client.model.query.filter;

public enum LogicalOperator {

    AND, OR;

    public String value() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}

package daris.web.client.model.object.exports;

public enum Parts {
    META, CONTENT, ALL;
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

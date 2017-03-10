package daris.web.client.model.collection;

public enum Parts {
    META, CONTENT, ALL;
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

package daris.web.client.model.object;

public enum SortOrder {

    DESC, ASC;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}

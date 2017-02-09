package daris.web.client.model.dataset;

public enum SourceType {
    primary, derivation;
    public static SourceType fromString(String str, SourceType defaultType) {

        if (primary.name().equalsIgnoreCase(str)) {
            return primary;
        } else if (derivation.name().equalsIgnoreCase(str)) {
            return derivation;
        } else {
            return defaultType;
        }
    }
}

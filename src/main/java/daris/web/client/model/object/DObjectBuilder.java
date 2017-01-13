package daris.web.client.model.object;

public interface DObjectBuilder {

    void setName(String name);

    void setDescription(String description);

    void setAllowIncompleteMeta(boolean allowIncompleteMeta);

    void setAllowInvalidMeta(boolean allowInvalidMeta);

}

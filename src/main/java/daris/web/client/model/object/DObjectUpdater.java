package daris.web.client.model.object;

public abstract class DObjectUpdater<T extends DObject> extends DObjectBuilder {

    private T _obj;

    protected DObjectUpdater(T obj) {
        _obj = obj;
        setName(_obj.name());
        setDescription(_obj.description());
        setMetadata(_obj.metadata());
        setMetadataForEdit(_obj.metadataForEdit());
    }

    public T object() {
        return _obj;
    }

}

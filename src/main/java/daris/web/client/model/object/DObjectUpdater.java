package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;

public abstract class DObjectUpdater<T extends DObject> extends DObjectBuilder {

    private T _obj;
    private XmlElement _metadataForEdit;

    protected DObjectUpdater(T obj) {
        _obj = obj;
        setName(_obj.name());
        setDescription(_obj.description());
        _metadataForEdit = obj.metadataForEdit();
    }

    public T object() {
        return _obj;
    }

    public void setMetadataForEdit(XmlElement metadataForEdit) {
        _metadataForEdit = metadataForEdit;
    }

    public XmlElement metadataForEdit() {
        return _metadataForEdit;
    }

    public abstract String serviceName();

    public abstract void serviceArgs(XmlWriter w);



}

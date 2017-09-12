package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;

public abstract class DObjectCreator extends DObjectBuilder {

    private DObjectRef _parentObject;
    private boolean _fillInIdNumber;
    private XmlElement _metadataForCreate;

    protected DObjectCreator(DObjectRef parentObject) {
        _parentObject = parentObject;
    }

    public boolean fillInIdNumber() {
        return _fillInIdNumber;
    }

    public void setFillInIdNumber(boolean fillInIdNumber) {
        _fillInIdNumber = fillInIdNumber;
    }

    public DObjectRef parentObject() {
        return _parentObject;
    }

    public void setMetadataForCreate(XmlElement metadataForCreate) {
        _metadataForCreate = metadataForCreate;
    }

    public XmlElement metadataForCreate() {
        return _metadataForCreate;
    }

    public abstract String serviceName();

    public abstract void serviceArgs(XmlWriter w);

}

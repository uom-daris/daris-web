package daris.web.client.model.object;

import arc.mf.client.xml.XmlWriter;

public abstract class DObjectCreator extends DObjectBuilder {

    private DObjectRef _parentObject;
    private boolean _fillInIdNumber;

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

    public abstract String serviceName();

    public abstract void serviceArgs(XmlWriter w);

}

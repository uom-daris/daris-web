package daris.web.client.model.object;

public abstract class DObjectUpdater extends DObjectBuilder {

    private DObjectRef _obj;

    protected DObjectUpdater(DObjectRef obj) {
        _obj = obj;
    }

    public DObjectRef object() {
        return _obj;
    }

}

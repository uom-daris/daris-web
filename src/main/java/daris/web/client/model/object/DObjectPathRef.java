package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class DObjectPathRef extends ObjectRef<DObjectPath> {

    private String _cid;

    public DObjectPathRef(String cid) {
        _cid = cid;
    }

    public DObjectPathRef(DObjectRef o) {
        this(o.citeableId());
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("cid", _cid);
    }

    @Override
    protected String resolveServiceName() {
        return "daris.object.path.find";
    }

    @Override
    protected DObjectPath instantiate(XmlElement xe) throws Throwable {
        return new DObjectPath(xe);
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return _cid;
    }

}

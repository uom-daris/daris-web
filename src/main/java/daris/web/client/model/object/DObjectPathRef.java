package daris.web.client.model.object;

import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class DObjectPathRef extends ObjectRef<DObjectPath> {

    private DObjectRef _o;

    public DObjectPathRef(DObjectRef o) {
        _o = o;
    }

    public void setObject(DObjectRef o) {
        if (!ObjectUtil.equals(_o, o)) {
            _o = o;
            reset();
        }
    }
    
    public DObjectRef object(){
        return _o;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        if (_o != null) {
            w.add("cid", _o.citeableId());
        }
    }

    @Override
    protected String resolveServiceName() {
        if (_o != null) {
            return "daris.object.path.find";
        } else {
            return "server.ping";
        }
    }

    @Override
    protected DObjectPath instantiate(XmlElement xe) throws Throwable {
        if (xe != null && xe.element("object") != null) {
            return new DObjectPath(xe);
        } else {
            return null;
        }
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return _o == null ? null : _o.citeableId();
    }

}

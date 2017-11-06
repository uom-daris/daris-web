package daris.web.client.model.object.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DObjectDestroy extends ObjectMessage<Null> {

    private DObject _o;
    private DObjectRef _ro;

    public DObjectDestroy(DObject o) {
        _o = o;
    }

    public DObjectDestroy(DObjectRef ro) {
        _ro = ro;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_o != null) {
            w.add("cid", _o.citeableId());
            if (_o.objectType() == DObject.Type.PROJECT) {
                w.add("destroy", true);
            }
        } else {
            w.add("cid", _ro.citeableId());
            if (_ro.isProject()) {
                w.add("destroy", true);
            }
        }
    }

    @Override
    protected String messageServiceName() {
        return "om.pssd.object.destroy";
    }

    @Override
    protected Null instantiate(XmlElement xe) throws Throwable {
        return new Null();
    }

    @Override
    protected String objectTypeName() {
        return _o != null ? _o.objectType().name() : _ro.referentTypeName();
    }

    @Override
    protected String idToString() {
        return _o != null ? _o.citeableId() : _ro.citeableId();
    }

}

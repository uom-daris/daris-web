package daris.web.client.model.object.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectRef;

public class DObjectExists extends ObjectMessage<Boolean> {

    private String _cid;

    public DObjectExists(String cid) {
        _cid = cid;
    }

    public DObjectExists(DObjectRef o) {
        this(o.citeableId());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("cid", _cid);
    }

    @Override
    protected String messageServiceName() {
        return "asset.exists";
    }

    @Override
    protected Boolean instantiate(XmlElement xe) throws Throwable {
        return xe.booleanValue("exists");
    }

    @Override
    protected String objectTypeName() {
        return String.valueOf(CiteableIdUtils.type(_cid));
    }

    @Override
    protected String idToString() {
        return _cid;
    }

}

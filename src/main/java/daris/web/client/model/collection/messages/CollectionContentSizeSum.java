package daris.web.client.model.collection.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class CollectionContentSizeSum extends ObjectMessage<Long> {

    public static final String SERVICE_NAME = "daris.collection.content.size.sum";

    private String _cid;
    private String _where;
    private boolean _includeAttachments;

    public CollectionContentSizeSum(String cid, boolean includeAttachments) {
        _cid = cid;
        _where = null;
        _includeAttachments = includeAttachments;
    }

    public CollectionContentSizeSum(String cid) {
        this(cid, true);
    }
    
    public CollectionContentSizeSum(DObjectRef o){
        this(o.citeableId(), true);
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("cid", _cid);
        if (_where != null) {
            w.add("where", _where);
        }
        w.add("include-attachments", _includeAttachments);
    }

    @Override
    protected String messageServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected Long instantiate(XmlElement xe) throws Throwable {
        if (xe != null) {
            return xe.longValue("size");
        }
        return null;
    }

    @Override
    protected String objectTypeName() {
        DObject.Type type = CiteableIdUtils.type(_cid);
        return type == null ? null : type.toString();
    }

    @Override
    protected String idToString() {
        return _cid;
    }

}

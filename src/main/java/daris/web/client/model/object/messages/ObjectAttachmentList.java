package daris.web.client.model.object.messages;

import java.util.List;
import java.util.Vector;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.Attachment;
import daris.web.client.model.object.DObjectRef;

public class ObjectAttachmentList extends ObjectMessage<List<Attachment>> {

    private String _cid;

    public ObjectAttachmentList(String cid) {

        _cid = cid;
    }

    public ObjectAttachmentList(DObjectRef o) {
        this(o.citeableId());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {

        w.add("cid", _cid);

    }

    @Override
    protected String messageServiceName() {

        return "daris.object.attachment.list";
    }

    @Override
    protected List<Attachment> instantiate(XmlElement xe) throws Throwable {

        if (xe != null) {
            List<XmlElement> aes = xe.elements("attachment");
            if (aes != null && !aes.isEmpty()) {
                List<Attachment> as = new Vector<Attachment>(aes.size());
                for (XmlElement ae : aes) {
                    as.add(new Attachment(ae.value("@id"), ae.value("name"), ae.value("description"), ae.value("type"),
                            ae.value("type/@ext"), ae.longValue("size", 0), ae.value("size/@h")));
                }
                return as;
            }
        }
        return null;
    }

    @Override
    protected String objectTypeName() {

        return "attachment";
    }

    @Override
    protected String idToString() {

        return _cid;
    }

}

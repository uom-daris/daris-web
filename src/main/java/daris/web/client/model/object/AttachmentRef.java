package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class AttachmentRef extends ObjectRef<Attachment> {

    private String _id;
    private String _name;

    public AttachmentRef(String id) {
        this(id, null);
    }

    public AttachmentRef(String id, String name) {
        _id = id;
        _name = name;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("id", _id);
    }

    @Override
    protected String resolveServiceName() {
        return "asset.get";
    }

    @Override
    protected Attachment instantiate(XmlElement xe) throws Throwable {
        _name = xe.value("asset/name");
        return new Attachment(_id, _name, xe.value("asset/description"), xe.value("asset/content/type"),
                xe.value("asset/content/type/@ext"), xe.longValue("asset/content/size", 0),
                xe.value("asset/content/size/@h"));
    }

    @Override
    public String referentTypeName() {
        return "attachment";
    }

    @Override
    public String idToString() {
        return _id;
    }

}

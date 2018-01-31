package daris.web.client.model.object.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObjectChildrenRef;
import daris.web.client.model.object.DObjectRef;

public class DObjectChildCursorFromGet extends ObjectMessage<Long> {

    private String _cid;
    private int _size;

    public DObjectChildCursorFromGet(String cid, int size) {
        _cid = cid;
        _size = size;
    }

    public DObjectChildCursorFromGet(DObjectRef child, DObjectChildrenRef children) {
        this(child.citeableId(), children.defaultPagingSize());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("cid", _cid);
        w.add("size", _size);
    }

    @Override
    protected String messageServiceName() {
        return "daris.object.child.cursor.get";
    }

    @Override
    protected Long instantiate(XmlElement xe) throws Throwable {
        XmlElement ce = xe.element("cursor");
        if (ce != null) {
            Long from = ce.longValue("from");
            if (from > 0) {
                return from;
            }
        }
        return 0L;
    }

    @Override
    protected String objectTypeName() {
        return null;
    }

    @Override
    protected String idToString() {
        return null;
    }

}

package daris.web.client.model.object.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObjectChildrenRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.SortOrder;

public class DObjectChildCursorFromGet extends ObjectMessage<Long> {

    private String _cid;
    private int _size;
    private DObjectChildrenRef.SortKey _sortKey;
    private SortOrder _sortOrder;

    public DObjectChildCursorFromGet(String cid, int size, DObjectChildrenRef.SortKey sortKey, SortOrder sortOrder) {
        _cid = cid;
        _size = size;
        _sortKey = sortKey;
        _sortOrder = sortOrder;
    }

    public DObjectChildCursorFromGet(DObjectRef child, int size, DObjectChildrenRef.SortKey sortKey,
            SortOrder sortOrder) {
        this(child.citeableId(), size, sortKey, sortOrder);
    }

    public DObjectChildCursorFromGet(DObjectRef child, DObjectChildrenRef children) {
        this(child.citeableId(), children.defaultPagingSize(), children.sortKey(), children.sortOrder());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("cid", _cid);
        w.add("size", _size);
        w.push("sort");
        w.add("key", _sortKey.xpath());
        if (_sortOrder != null) {
            w.add("order", _sortOrder.toString());
        }
        w.pop();
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

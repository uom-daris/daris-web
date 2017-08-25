package daris.web.client.model.object.event;

import arc.mf.client.util.DynamicBoolean;
import arc.mf.client.util.ObjectUtil;
import arc.mf.event.Filter;
import daris.web.client.model.object.DObjectRef;

public class DObjectEventFilter extends arc.mf.event.Filter {

    private String _cid;
    private String _pid;

    private DObjectEventFilter(DObjectRef obj, DObjectRef parent) {
        this(obj == null ? null : obj.citeableId(), parent == null ? null : parent.citeableId());
    }

    public DObjectEventFilter(String cid, String pid) {
        super(DObjectEvent.SYSTEM_EVENT_NAME, null, DynamicBoolean.TRUE);
        _cid = cid;
        _pid = pid;
    }

    public String contextParent() {
        return _pid;
    }

    public String contextObject() {
        return _cid;
    }

    public boolean equals(Filter of) {
        if (of == null || !(of instanceof DObjectEventFilter)) {
            return false;
        }
        DObjectEventFilter df = (DObjectEventFilter) of;

        if (!type().equals(df.type())) {
            return false;
        }

        if (!ObjectUtil.equals(descend(), df.descend())) {
            return false;
        }

        if (contextParent() == null) {
            return true;
        }

        if (!contextParent().equals(df.contextParent())) {
            return false;
        }

        if (contextObject() == null) {
            return true;
        }

        if (!contextObject().equals(df.contextObject())) {
            return false;
        }

        // If no object is defined, then the default is to catch all
        if (object() == null) {
            return true;
        }

        if (!object().equals(of.object())) {
            return false;
        }

        return true;
    }

}

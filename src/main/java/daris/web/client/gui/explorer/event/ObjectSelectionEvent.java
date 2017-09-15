package daris.web.client.gui.explorer.event;

import daris.web.client.model.object.DObjectRef;

public class ObjectSelectionEvent {

    private Object _source;
    private DObjectRef _o;
    private boolean _isParent;

    public ObjectSelectionEvent(Object source, DObjectRef o, boolean isParent) {
        _source = source;
        _o = o;
        _isParent = isParent;
    }

    public Object source() {
        return _source;
    }

    public DObjectRef object() {
        return _o;
    }

    public boolean isParent() {
        return _isParent;
    }

}

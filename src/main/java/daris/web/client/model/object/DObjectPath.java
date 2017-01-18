package daris.web.client.model.object;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;

public class DObjectPath {

    private List<DObjectRef> _parents;
    private DObjectRef _object;
    private DObjectRef _child;

    public DObjectPath(XmlElement xe) throws Throwable {
        List<XmlElement> pes = xe.elements("parent");
        if (pes != null && !pes.isEmpty()) {
            _parents = new ArrayList<DObjectRef>(pes.size());
            for (XmlElement pe : pes) {
                DObjectRef p = new DObjectRef(pe.value("@cid"), pe.value("@id"), pe.value("@proute"), pe.value("@name"),
                        pe.intValue("@nbc", 0), false);
                _parents.add(p);
            }
        }
        _object = new DObjectRef(xe.value("object/@cid"), xe.value("object/@id"), xe.value("object/@proute"),
                xe.value("object/@name"), xe.intValue("object/@nbc", 0), false);
        XmlElement ce = xe.element("child");
        if (ce != null) {
            _child = new DObjectRef(ce.value("@cid"), ce.value("@id"), ce.value("@proute"), ce.value("@name"),
                    ce.intValue("@nbc", 0), false);

        }

    }

    public List<DObjectRef> parents() {
        return _parents;
    }

    public DObjectRef directParent() {
        if (_parents == null || _parents.isEmpty()) {
            return null;
        }
        return _parents.get(_parents.size() - 1);
    }

    public DObjectRef object() {
        return _object;
    }

    public DObjectRef child() {
        return _child;
    }

    public List<DObjectRef> list(boolean object, boolean child) {
        List<DObjectRef> list = new ArrayList<DObjectRef>();
        if (_parents != null) {
            list.addAll(_parents);
        }
        if (object) {
            list.add(_object);
        }
        if (child && _child != null) {
            list.add(_child);
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

}

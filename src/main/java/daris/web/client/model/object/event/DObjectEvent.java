package daris.web.client.model.object.event;

import java.util.Collection;

import arc.mf.client.xml.XmlElement;
import arc.mf.event.Filter;
import arc.mf.event.SystemEvent;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.Session;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class DObjectEvent extends SystemEvent {

    public enum Action {
        CREATE, MODIFY, MEMBERS, DESTROY;

        public static Action fromString(String str) {
            if (str != null) {
                Action[] vs = values();
                for (Action v : vs) {
                    if (v.name().equalsIgnoreCase(str)) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    public static final String SYSTEM_EVENT_NAME = "pssd-object";

    private DObjectRef _o;
    private Action _action;

    public DObjectEvent(XmlElement ee) {
        super(SYSTEM_EVENT_NAME, ee.value("object"));
        _action = Action.fromString(ee.value("action"));
        int nbc = -1;
        try {
            nbc = ee.intValue("object/@nbc", -1);
        } catch (Throwable e) {
            nbc = -1;
        }
        _o = new DObjectRef(ee.value("object"), nbc);
    }

    public Action action() {
        return _action;
    }

    public DObjectRef objectRef() {
        return _o;
    }

    public String citeableId() {
        return object();
    }

    @Override
    public boolean matches(Filter f) {
        if (!type().equals(f.type())) {
            return false;
        }
        if (f.object() != null) {
            switch (_action) {
            case CREATE:
            case DESTROY:
                return CiteableIdUtils.isDirectChild(object(), f.object());
            default:
                return f.object().equals(object());
            }
        }
        // ListView subscriber
        // RootNode subscriber
        return true;
    }

    public String toString() {
        return _action + ": " + object();
    }

    public boolean matchesObject(Object o) {
        if (o != null) {
            if (o instanceof DObjectRef) {
                return citeableId().equals(((DObjectRef) o).citeableId());
            } else if (o instanceof DObject) {
                return citeableId().equals(((DObject) o).citeableId());
            }
        }
        return false;
    }

    public boolean isParentOf(Object o) {
        if (o != null) {
            String cid = null;
            if (o instanceof DObjectRef) {
                cid = ((DObjectRef) o).citeableId();
            } else if (o instanceof DObject) {
                cid = ((DObject) o).citeableId();
            }
            if (cid != null) {
                return cid.startsWith(citeableId() + ".");
            }
        }
        return false;
    }

    public boolean isDirectChildOf(DObjectRef parentObject) {
        if (parentObject != null) {
            String pid = parentObject.citeableId();
            return CiteableIdUtils.isDirectChild(citeableId(), pid);
        }
        return false;
    }

    public boolean isGrandChildOf(DObjectRef parentObject) {
        if (parentObject != null) {
            return CiteableIdUtils.parent(citeableId(), 2).equals(parentObject.citeableId());
        }
        return false;
    }

//    public static void isRelavent(DObjectEvent de, ObjectMessageResponse<Boolean> rh) {
//        if (de.action() == Action.DESTROY) {
//            final String ecid = de.citeableId();
//            if (CiteableIdUtils.isProject(ecid)) {
//                // project deleted.
//                rh.responded(true);
//                return;
//            }
//            Session.execute("asset.query",
//                    "<where>model='om.pssd.project'</where><action>get-cid</action><size>infinity</size>",
//                    (xe, outputs) -> {
//                        Collection<String> cids = xe.values("cid");
//                        if (cids != null) {
//                            for (String cid : cids) {
//                                if (ecid.startsWith(cid + ".") || ecid.equals(cid)) {
//                                    rh.responded(true);
//                                    return;
//                                }
//                            }
//                        }
//                        rh.responded(false);
//                    });
//        } else {
//            Session.execute("asset.query", "<where>cid='" + de.object() + "'</where><action>count</action>",
//                    (xe, outputs) -> {
//                        int count = xe.intValue("value");
//                        rh.responded(count > 0);
//                    });
//        }
//    }

}

package daris.web.client.model.object.event;

import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.event.Filter;
import arc.mf.event.SystemEvent;
import daris.web.client.model.CiteableIdUtils;
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

	public String cid() {
		return object();
	}

	@Override
	public boolean matches(Filter f) {

		if (!type().equals(f.type())) {
			return false;
		}
		switch (_action) {
		case CREATE:
		case DESTROY:
			if (f.object() == null) {
				// local object type: repository
				// event object type = project?
				return CiteableIdUtils.isProject(object());
			} else {
				return CiteableIdUtils.isDirectParent(f.object(), object());
			}
		case MODIFY:
		case MEMBERS:
			return ObjectUtil.equals(f.object(), object());
		default:
			return ObjectUtil.equals(f.object(), object());
		}
	}

	public String toString() {
		return _action + ": " + object();
	}

}

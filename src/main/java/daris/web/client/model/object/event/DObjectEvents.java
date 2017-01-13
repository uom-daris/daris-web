package daris.web.client.model.object.event;

import arc.mf.client.xml.XmlElement;
import arc.mf.event.SystemEvent;
import arc.mf.event.SystemEventFactory;
import arc.mf.event.SystemEventRegistry;

public class DObjectEvents {
	private static boolean _init = false;

	public static void initialize() {
		if (_init) {
			return;
		}

		SystemEventRegistry.add(DObjectEvent.SYSTEM_EVENT_NAME, new SystemEventFactory() {
			public SystemEvent instantiate(String type, XmlElement ee) throws Throwable {
				return new DObjectEvent(ee);
			}
		});

		_init = true;
	}
}

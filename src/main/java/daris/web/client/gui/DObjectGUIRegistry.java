package daris.web.client.gui;

import arc.gui.object.register.SystemObjectGUIRegistry;
import daris.web.client.model.object.DObjectRef;

public class DObjectGUIRegistry {

    private static boolean _registered = false;

    public static SystemObjectGUIRegistry get() {

        SystemObjectGUIRegistry registry = SystemObjectGUIRegistry.get();
        if (!_registered) {
            registry.add(DObjectRef.class, DObjectGUI.INSTANCE);
            _registered = true;
        }
        return registry;
    }

}

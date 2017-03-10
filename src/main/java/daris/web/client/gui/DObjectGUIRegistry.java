package daris.web.client.gui;

import arc.gui.object.register.SystemObjectGUIRegistry;
import arc.mf.model.authentication.UserRef;
import daris.web.client.gui.object.DObjectGUI;
import daris.web.client.gui.user.RoleUserGUI;
import daris.web.client.gui.user.UserGUI;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.user.RoleUser;

public class DObjectGUIRegistry {

    private static boolean _registered = false;

    public static SystemObjectGUIRegistry get() {

        SystemObjectGUIRegistry registry = SystemObjectGUIRegistry.get();
        if (!_registered) {
            registry.add(DObjectRef.class, DObjectGUI.INSTANCE);
            registry.add(UserRef.class, UserGUI.INSTANCE);
            registry.add(RoleUser.class, RoleUserGUI.INSTANCE);
            _registered = true;
        }
        return registry;
    }

}

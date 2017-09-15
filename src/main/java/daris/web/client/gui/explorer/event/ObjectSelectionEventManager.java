package daris.web.client.gui.explorer.event;

import java.util.ArrayList;
import java.util.List;

import daris.web.client.model.object.DObjectRef;

public class ObjectSelectionEventManager {

    private static List<ObjectSelectionEventHandler> _shs = new ArrayList<ObjectSelectionEventHandler>();

    public static void subscribe(ObjectSelectionEventHandler sh) {
        _shs.add(sh);
    }

    public static void fireEvent(ObjectSelectionEvent event) {
        for (ObjectSelectionEventHandler sh : _shs) {
            sh.handleEvent(event);
        }
    }

    public static void fireEvent(Object source, DObjectRef o, boolean isParent) {
        ObjectSelectionEvent event = new ObjectSelectionEvent(source, o, isParent);
        fireEvent(event);
    }

}

package daris.web.client.gui.explorer;

import arc.gui.gwt.widget.window.Window;
import arc.gui.window.WindowProperties;
import daris.web.client.model.object.DObjectRef;

public class Test {

    public static void show() {
        WindowProperties wp = new WindowProperties();
        wp.setSize(0.7, 0.7);
        wp.setCanBeClosed(true);
        wp.setTitle("test");
        TreeView tv = new TreeView(new DObjectRef("39.1.10.1.1.1.1", -1));
        tv.fitToParent();
        Window win = Window.create(wp);
        win.setContent(tv);
        win.show();
        win.centerInPage();
        
    }
}

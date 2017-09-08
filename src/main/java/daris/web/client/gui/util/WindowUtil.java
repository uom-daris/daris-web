package daris.web.client.gui.util;

public class WindowUtil {

    public static int calcWindowWidth(arc.gui.window.Window owner, double w) {
        if (owner != null) {
            if (owner.nativeWindow() instanceof arc.gui.gwt.widget.window.Window) {
                arc.gui.gwt.widget.window.Window nw = (arc.gui.gwt.widget.window.Window) (owner.nativeWindow());
                return (int) (nw.width() * w);
            }
        }
        return (int) (com.google.gwt.user.client.Window.getClientWidth() * w);
    }

    public static int calcWindowHeight(arc.gui.window.Window owner, double h) {
        if (owner != null) {
            if (owner.nativeWindow() instanceof arc.gui.gwt.widget.window.Window) {
                arc.gui.gwt.widget.window.Window nw = (arc.gui.gwt.widget.window.Window) (owner.nativeWindow());
                return (int) (nw.height() * h);
            }
        }
        return (int) (com.google.gwt.user.client.Window.getClientHeight() * h);
    }

    public static int calcWindowWidth(double w) {
        return calcWindowWidth(null, w);
    }

    public static int calcWindowHeight(double h) {
        return calcWindowHeight(null, h);
    }

}

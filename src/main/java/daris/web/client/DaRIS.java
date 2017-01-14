package daris.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;

import arc.gui.gwt.dnd.DragAndDrop;
import arc.gui.gwt.theme.ThemeRegistry;
import arc.gui.gwt.widget.panel.RootPanel;
import arc.mf.client.plugin.Plugin;
import arc.mf.event.SystemEventChannel;
import arc.mf.model.shopping.events.ShoppingEvents;
import arc.mf.session.DefaultLoginDialog;
import arc.mf.session.LoginDialog;
import arc.mf.session.Session;
import arc.mf.session.SessionHandler;
import daris.web.client.gui.DObjectExplorer;
import daris.web.client.gui.theme.DaRISTheme;
import daris.web.client.model.object.event.DObjectEvents;

public class DaRIS implements EntryPoint, SessionHandler {
    public void onModuleLoad() {

        /*
         * Initialise theme
         */
        new ThemeRegistry().setCurrentTheme(new DaRISTheme());

//        /*
//         * initialize history
//         */
//        HistoryManager.initialize();

        /*
         * start mediaflux session
         */
        LoginDialog dlg = new DefaultLoginDialog();
        dlg.setTitle("DaRIS");
        dlg.setVersion(Version.VERSION);
        Session.setAutoLogonCredentials("system", "manager", "change_me");
        Session.setLoginDialog(dlg);
        Session.initialize(this);
    }

    @Override
    public void sessionCreated(boolean initial) {

        /*
         * Enable pssd events
         */
        DObjectEvents.initialize();

        /*
         * Enable shopping cart events
         */
        ShoppingEvents.initialize();

        /*
         * Enable drag and drop
         */
        DragAndDrop.initialize();

        /*
         * Subscribes to system event channel
         */
        SystemEventChannel.subscribe();

        /*
         * show gui
         */
        RootPanel.add(DObjectExplorer.get());

        /*
         * Fire current history state
         */
        if (Plugin.isStandaloneApplication()) {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public void sessionExpired() {
        /*
         * stop listening to system events
         */
        SystemEventChannel.unsubscribe(Session.created());

        /*
         * 
         */
        RootPanel.remove(DObjectExplorer.get());
    }

    @Override
    public void sessionTerminated() {
        /*
         * stop listening to system events
         */
        SystemEventChannel.unsubscribe(Session.created());

        /*
         * 
         */
        RootPanel.remove(DObjectExplorer.get());
    }

}

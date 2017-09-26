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
import daris.web.client.gui.explorer.Explorer;
import daris.web.client.gui.theme.DaRISTheme;
import daris.web.client.model.object.event.DObjectEvents;

public class DaRIS implements EntryPoint, SessionHandler {
    public void onModuleLoad() {

        /*
         * Initialise theme
         */
        new ThemeRegistry().setCurrentTheme(new DaRISTheme());

        if (Plugin.isStandaloneApplication()) {
            /*
             * start mediaflux session
             */
            LoginDialog dlg = new DefaultLoginDialog();
            dlg.setTitle("DaRIS");
            dlg.setVersion(Version.VERSION);
            Session.setAutoLogonCredentials("system", "manager", "change_me");
            Session.setLoginDialog(dlg);
            Session.initialize(this);
        } else {
            Session.initializeForPlugin();
            Session.addSessionHandler(this);
            sessionCreated(true);
        }
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
         * Subscribes to system event channel
         */
        SystemEventChannel.subscribe();

        if (Plugin.isStandaloneApplication()) {

            /*
             * Enable drag and drop
             */
            DragAndDrop.initialize();

            /*
             * show gui
             */
            RootPanel.add(Explorer.get());

            /*
             * Fire current history state
             */
            History.fireCurrentHistoryState();
        } else {
            // TODO: install as adesktop plugin application...
        }
    }

    @Override
    public void sessionExpired() {

        if (Plugin.isStandaloneApplication()) {
            /*
             * stop listening to system events
             */
            SystemEventChannel.unsubscribe(Session.created());

            RootPanel.remove(Explorer.get());
        }
    }

    @Override
    public void sessionTerminated() {

        /*
         * 
         */
        if (Plugin.isStandaloneApplication()) {
            /*
             * stop listening to system events
             */
            SystemEventChannel.unsubscribe(Session.created());

            RootPanel.remove(Explorer.get());
        }
    }

}

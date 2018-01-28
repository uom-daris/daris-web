package daris.web.client.gui.widget;

import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.window.Window;
import arc.gui.window.WindowProperties;
import daris.web.client.gui.Resource;
import daris.web.client.gui.util.ButtonUtil;

public class SyncDialog {

    public static final arc.gui.image.Image ICON_ABORT = new arc.gui.image.Image(
            Resource.INSTANCE.crossRed16().getSafeUri().asString(), 12, 12);

    private VerticalPanel _vp;
    private SimplePanel _sp;
    private Button _abortButton;

    private Window _win;

    public SyncDialog(String title, String message, int width, int height, arc.gui.window.Window owner) {

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _sp = new SimplePanel();
        _sp.fitToParent();
        _sp.setContent(new LoadingMessage(message));

        _vp.add(_sp);

        ButtonBar bb = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.RIGHT);
        bb.setHeight(32);

        _abortButton = ButtonUtil.createButton(ICON_ABORT, abortButtonLabel(), "Abort the request to server", true);
        _abortButton.setWidth(80);
        _abortButton.setMarginRight(25);
        _abortButton.addClickHandler(e -> {
            abort();
        });
        bb.add(_abortButton);

        _vp.add(bb);

        WindowProperties wp = new WindowProperties();
        wp.setTitle(title);
        wp.setOwnerWindow(owner);
        wp.setCanBeClosed(false);
        wp.setModal(true);
        wp.setCanBeMaximised(false);
        wp.setCanBeResized(false);
        wp.setCanBeMoved(true);
        wp.setSize(width, height);
        _win = Window.create(wp);
        _win.setContent(_vp);
    }

    public void show() {
        if (!_win.isShowing()) {
            _win.show();
        }
    }

    public void abort() {
        if (_win.isShowing()) {
            _win.close();
        }
        aborted();
    }

    protected void aborted() {

    }

    public void complete() {
        if (_win.isShowing()) {
            _win.close();
        }
        completed();
    }

    protected void completed() {

    }

    protected String abortButtonLabel() {
        return "Abort";
    }

}

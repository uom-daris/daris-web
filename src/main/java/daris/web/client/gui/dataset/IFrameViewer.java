package daris.web.client.gui.dataset;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.Frame;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.panel.SimplePanel;

public class IFrameViewer extends ContainerWidget {

    private String _url;

    private SimplePanel _sp;
    private Frame _frame;

    protected IFrameViewer(String url) {
        _url = url;

        _sp = new SimplePanel();
        _frame = new Frame();
        _frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
        _frame.setSize("100%", "100%");
        _sp.setContent(_frame);
        _sp.fitToParent();
        _sp.addAttachHandler(new Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // load url until the sp is attached
                if (event.isAttached() && (_frame.getUrl() == null || _frame.getUrl().isEmpty())) {
                    _frame.setUrl(_url);
                }
            }
        });

        initWidget(_sp);
    }

}

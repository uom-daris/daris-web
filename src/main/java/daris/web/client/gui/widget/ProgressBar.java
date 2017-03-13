package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.Position;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import daris.web.client.util.ObjectUtils;

public class ProgressBar extends ContainerWidget {

    private AbsolutePanel _ap;

    private SimplePanel _progressSP;
    private SimplePanel _totalSP;
    private double _progress;
    private HTML _msg;

    public ProgressBar() {
        this(0, null);
    }

    public ProgressBar(double progress, String message) {
        _progress = progress;

        _ap = new AbsolutePanel();
        _ap.setHeight(18);
        _ap.setWidth100();
        _ap.setBorder(1, RGB.GREY_EEE);

        _progressSP = new SimplePanel();
        _progressSP.setWidth(0);
        _progressSP.setHeight100();
        _progressSP.setBackgroundImage(
                new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
                        new RGB(0xef, 0x60, 0x23), new RGB(0xff, 0x70, 0x33)));

        _totalSP = new SimplePanel();
        _totalSP.fitToParent();
        _totalSP.setPosition(Position.ABSOLUTE);
        _totalSP.setBackgroundColour(new RGB(0xee, 0xee, 0xee));
        _totalSP.setContent(_progressSP);

        _ap.add(_totalSP);

        _msg = new HTML();
        if (message != null) {
            _msg.setHTML(message);
        }
        _msg.setFontSize(11);
        _msg.setPosition(Position.ABSOLUTE);
        _msg.setColour(new RGB(0x13, 0x41, 0x8b));

        _ap.add(_msg);

        initWidget(_ap);
    }

    public void setMessage(String msg) {
        if (!ObjectUtils.equals(msg, _msg.html())) {
            _msg.setHTML(msg);
            doLayoutChildren();
        }
    }

    /**
     * Sets progress.
     * 
     * @param p
     *            Should be in the range [0,1]
     */
    public void setProgress(double progress, String msg) {
        if (progress < 0.0) {
            progress = 0.0;
        }
        if (progress > 1.0) {
            progress = 1.0;
        }
        if (progress != _progress || !ObjectUtils.equals(msg, _msg.html())) {
            _progress = progress;
            _msg.setHTML(msg);
            doLayoutChildren();
        }
    }

    public void setProgress(long progress, long total, String msg) {
        setProgress((double) progress / (double) total, msg);
    }

    /**
     * Sets progress.
     * 
     * @param p
     *            Should be in the range [0,1]
     */
    public void setProgress(double progress) {
        setProgress(progress, "");
    }

    protected void doLayoutChildren() {
        super.doLayoutChildren();
        _msg.setLeft(_totalSP.width() / 2 - _msg.width() / 2);
        _msg.setTop(_ap.height() / 2 - _msg.height() / 2);
        _progressSP.setWidth((int) ((double) _totalSP.width() * _progress));
    }

}
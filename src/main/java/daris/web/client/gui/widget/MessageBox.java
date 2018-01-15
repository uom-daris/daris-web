package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.TextAlignment;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.popup.PopupPanel;
import arc.gui.gwt.widget.window.Window;

public class MessageBox {

    public static enum Position {
        TOP, BOTTOM, CENTER
    }

    public static void show(int w, int h, Position position, String message, int delay) {
        show(w, h, 0, 0, com.google.gwt.user.client.Window.getClientWidth(),
                com.google.gwt.user.client.Window.getClientHeight(), position, message, delay);
    }

    public static void show(int w, int h, Window win, Position position, String message, int delay) {
        show(w, h, win.left(), win.top(), win.width(), win.height(), position, message, delay);
    }

    public static void show(int w, int h, BaseWidget container, Position position, String message, int delay) {
        show(w, h, container.absoluteLeft(), container.absoluteTop(), container.widthWithMargins(),
                container.heightWithMargins(), position, message, delay);
    }

    static void show(int w, int h, final int cx, final int cy, final int cw, final int ch, final Position position,
            String message, int delay) {

        final PopupPanel pp = new PopupPanel();
        pp.setAutoHideEnabled(true);
        pp.setSize(w, h);

        HTML messageHTML = new HTML(message);
        messageHTML.setPosition(Style.Position.ABSOLUTE);
        messageHTML.setFontSize(11);
        messageHTML.setFontFamily(DefaultStyles.FONT_FAMILY);
        messageHTML.setPaddingTop(2);
        messageHTML.setColour(RGB.WHITE);
        messageHTML.setTextAlignment(TextAlignment.CENTER);
        messageHTML.setTextShadow(1, 1, 0, RGB.BLACK);
        messageHTML.setVerticalAlign(VerticalAlign.MIDDLE);
        messageHTML.element().getStyle().setLineHeight(20, Unit.PX);

        final AbsolutePanel sp = new AbsolutePanel() {
            protected void doLayoutChildren() {
                super.doLayoutChildren();
                if (messageHTML.height() > height()) {
                    setHeight(messageHTML.height());
                    messageHTML.setTop(0);
                } else {
                    messageHTML.setTop((height() - messageHTML.height()) / 2);
                }
                if (messageHTML.width() > width()) {
                    setWidth(messageHTML.width());
                    messageHTML.setLeft(0);
                } else {
                    messageHTML.setLeft((width() - messageHTML.width()) / 2);
                }
            }
        };
        sp.fitToParent();
        sp.setOpacity(1.0);
        sp.setPaddingTop(3);
        sp.setBackgroundColour(RGB.GREY_333);
        sp.setBorder(1, RGB.GREY_EEE);
        sp.setBorderRadius(3);

        sp.add(messageHTML);

        pp.setContent(sp);
        pp.setPopupPositionAndShow(new PositionCallback() {

            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int x;
                int y;
                switch (position) {
                case TOP:
                    x = cx + (cw - offsetWidth) / 2;
                    y = cy;
                    break;
                case BOTTOM:
                    x = cx + (cw - offsetWidth) / 2;
                    y = cy + ch - offsetHeight;
                    break;
                default:
                    x = cx + (cw - offsetWidth) / 2;
                    y = cy + (ch - offsetHeight) / 2;
                    break;
                }
                pp.setPopupPosition(x, y);
            }
        });
        pp.show();

        Timer timer = new Timer() {
            @Override
            public void run() {
                Timer fadeOutTimer = new Timer() {
                    @Override
                    public void run() {
                        if (sp.opacity() > 0) {
                            double opacity = sp.opacity() - 0.05;
                            sp.setOpacity(opacity < 0.0 ? 0.0 : opacity);
                        } else {
                            pp.hide();
                            cancel();
                        }
                    }
                };
                fadeOutTimer.scheduleRepeating(100);
                cancel();
            }
        };
        timer.schedule(delay);
    }

}

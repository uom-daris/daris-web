package daris.web.client;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.SimplePanel;

public class TestBar extends ContainerWidget {

    public static final int HEIGHT = 32;

    public static final int FONT_SIZE = 11;
    
    public static final int SPACING = 5;

    private static class NavButton extends HTML {

        NavButton(String text, ClickHandler ch) {
            super(text);
            setColour(new RGB(0, 120, 215));
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setBorderRadius(3);
            setPaddingLeft(5);
            setPaddingRight(5);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(HEIGHT, Unit.PX);
            setOverflow(Overflow.HIDDEN);
            if (ch != null) {
                setCursor(Cursor.POINTER);
                addMouseOverHandler(new MouseOverHandler() {

                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        setBackgroundColour(RGBA.GREY_EEE);
                    }
                });
                addMouseOutHandler(new MouseOutHandler() {

                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        setBackgroundColour(RGBA.TRANSPARENT);
                    }
                });
                addClickHandler(ch);
            }
        }
    }

    private static class Separator extends HTML {

        Separator() {
            super(">");
            setFontSize(FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setHeight(HEIGHT);
            element().getStyle().setLineHeight(32, Unit.PX);
        }

    }

    private SimplePanel _sp;
    private HorizontalPanel _hp;

    public TestBar() {

        _sp = new SimplePanel();
        _sp.setHeight(32);
        _sp.setWidth100();
        _sp.setBackgroundColour(RGB.GREY_888);

        _hp = new HorizontalPanel();
        _hp.setSpacing(SPACING);
        _hp.setHeight(HEIGHT);

        _sp.setContent(_hp);

        initWidget(_sp);

        update();
    }

    private void update() {
        _hp.add(new NavButton("HOME", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));
        _hp.add(new Separator());
        _hp.add(new NavButton("Project 1128.1.100: Test Project 100", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));
        _hp.add(new Separator());
        _hp.add(new NavButton("Subject 12: Test Subject 12", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));
        _hp.add(new Separator());
        _hp.add(new NavButton("Ex-method 1: Test Method A B C", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

            }
        }));
        _hp.add(new Separator());
        _hp.add(new NavButton("Study 19: Test Study 19", null));
    }

}

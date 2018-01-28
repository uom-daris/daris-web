package daris.web.client.gui.widget;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import daris.web.client.gui.Resource;

public class LoadingMessage extends ContainerWidget {

    public static final arc.gui.image.Image DEFAULT_IMAGE = new arc.gui.image.Image(
            Resource.INSTANCE.loading56().getSafeUri().asString(), 24, 24);

    private AbsolutePanel _ap;
    private Image _image;
    private HTML _html;

    public LoadingMessage(String message) {
        this(DEFAULT_IMAGE, message);
    }

    public LoadingMessage(arc.gui.image.Image image, String message) {
        _image = new Image(image);
        _image.setPosition(Position.ABSOLUTE);
        _html = new HTML(message);
        _html.setFontSize(11);
        _html.setFontWeight(FontWeight.BOLD);
        _html.setPosition(Position.ABSOLUTE);
        _ap = new AbsolutePanel() {
            @Override
            protected void doLayoutChildren() {
                super.doLayoutChildren();
                _image.setLeft(width() / 2 - _image.width() / 2);
                _image.setTop(height() / 2 - _image.height() / 2);
                _html.setLeft(width() / 2 - _html.width() / 2);
                _html.setTop(_image.bottom());
            }
        };
        _ap.fitToParent();
        _ap.add(_image);
        _ap.add(_html);
        initWidget(_ap);
    }
}

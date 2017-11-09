package daris.web.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import daris.web.client.gui.Resource;

public class TrippleStateCheckBox extends AbsolutePanel {

    public static arc.gui.image.Image ICON_UNCHECKED = new arc.gui.image.Image(
            Resource.INSTANCE.checkboxUncheckedIcon().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_INTERMEDIATE = new arc.gui.image.Image(
            Resource.INSTANCE.checkboxIntermediateIcon().getSafeUri().asString(), 16, 16);
    public static arc.gui.image.Image ICON_CHECKED = new arc.gui.image.Image(
            Resource.INSTANCE.checkboxCheckedIcon().getSafeUri().asString(), 16, 16);

    public static enum State {
        CHECKED, UNCHECKED, INTERMEDIATE;
        public arc.gui.image.Image icon() {
            switch (this) {
            case CHECKED:
                return ICON_CHECKED;
            case UNCHECKED:
                return ICON_UNCHECKED;
            case INTERMEDIATE:
                return ICON_INTERMEDIATE;
            default:
                break;
            }
            return null;
        }

        public Image createIcon() {
            return new Image(icon(), 14, 14);
        }
    }

    public static interface StateChangeHandler {
        void stateChanged(State state);
    }

    private State _state;
    private arc.gui.gwt.widget.image.Image _icon;

    private List<StateChangeHandler> _schs;

    public TrippleStateCheckBox() {
        this(State.UNCHECKED);
    }

    public TrippleStateCheckBox(State state) {
        _schs = new ArrayList<StateChangeHandler>();

        _state = state;

        setMinWidth(16);
        setMinHeight(16);
        setWidth(16);
        setHeight(16);

        _icon = _state.createIcon();
        _icon.setPosition(Position.ABSOLUTE);
        _icon.setBackgroundColour(RGB.WHITE);
        _icon.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (_state == State.CHECKED || _state == State.INTERMEDIATE) {
                    setState(State.UNCHECKED, true);
                } else {
                    setState(State.CHECKED, true);
                }
            }
        });
        add(_icon);

    }

    public State state() {
        return _state;
    }

    protected void setState(State state, boolean notify) {
        if (state != _state) {
            _state = state;
            switch (state) {
            case UNCHECKED:
                _icon.setEnabledImage(ICON_UNCHECKED);
                break;
            case CHECKED:
                _icon.setEnabledImage(ICON_CHECKED);
                break;
            case INTERMEDIATE:
                _icon.setEnabledImage(ICON_INTERMEDIATE);
                break;
            default:
                break;
            }
            if (notify) {
                notifyOfStateChange();
            }
        }
    }

    public void check(boolean notify) {
        setState(State.CHECKED, notify);
    }

    public boolean isChecked() {
        return _state == State.CHECKED;
    }

    public void uncheck(boolean notify) {
        setState(State.UNCHECKED, notify);
    }

    public boolean isUnchecked() {
        return _state == State.UNCHECKED;
    }

    public void intermediate(boolean notify) {
        setState(State.INTERMEDIATE, notify);
    }

    public boolean isIntermediate() {
        return _state == State.INTERMEDIATE;
    }

    public void addStateChangeHandler(StateChangeHandler sch) {
        _schs.add(sch);
    }

    private void notifyOfStateChange() {
        for (StateChangeHandler sch : _schs) {
            sch.stateChanged(_state);
        }
    }

    protected void doLayoutChildren() {
        super.doLayoutChildren();
        int w = width();
        int h = height();
        int iw = _icon.width();
        int ih = _icon.height();
        _icon.setLeft(w == 0 ? 0 : w / 2 - iw / 2);
        _icon.setTop(h == 0 ? 0 : h / 2 - ih / 2);
    }

}

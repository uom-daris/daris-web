package daris.web.client.gui.widget;

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import daris.web.client.gui.DObjectExplorer;
import daris.web.client.model.object.DObjectRef;

public class DNavButtonBar extends ContainerWidget {

    private static class Separator extends HTML {
        Separator() {
            super(">");
            setFontSize(DStyles.NAV_BUTTON_FONT_SIZE);
            setFontWeight(FontWeight.BOLD);
            setHeight(DStyles.NAV_BUTTON_BAR_HEIGHT);
            element().getStyle().setLineHeight(DStyles.NAV_BUTTON_BAR_HEIGHT, Unit.PX);
        }
    }

    private DObjectExplorer _explorer;
    private SimplePanel _sp;
    private HorizontalPanel _hp;

    public DNavButtonBar(DObjectExplorer explorer) {

        _explorer = explorer;
        _sp = new SimplePanel();
        _sp.setHeight(DStyles.NAV_BUTTON_BAR_HEIGHT);
        _sp.setWidth100();
        _sp.setBorder(1, DStyles.NAV_BUTTON_BAR_BORDER_COLOUR);

        _hp = new HorizontalPanel();
        _hp.setSpacing(DStyles.NAV_BUTTON_SPACING);
        _hp.setHeight(DStyles.NAV_BUTTON_BAR_HEIGHT);

        _sp.setContent(_hp);

        initWidget(_sp);

    }

    protected DNavButton addButton(DObjectRef o, ClickHandler ch) {
        DNavButton button = new DNavButton(o, ch);
        _hp.add(button);
        return button;
    }

    protected void addSeparator() {
        _hp.add(new Separator());
    }

    public void update(List<DObjectRef> parents) {
        _hp.removeAll();
        int nbParents = parents == null ? 0 : parents.size();
        /*
         * home/root
         */
        addButton(null, (nbParents > 0) ? (event -> {
            _explorer.list();
        }) : null);
        /*
         * parents
         */
        if (nbParents > 0) {
            for (int i = 0; i < nbParents; i++) {
                DObjectRef p = parents.get(i);
                addSeparator();
                addButton(p, (i != nbParents - 1) ? (event -> {
                    _explorer.list(p);
                }) : null);
            }
        }
    }

}

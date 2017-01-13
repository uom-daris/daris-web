package daris.web.client.gui.object;

import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.panel.SimplePanel;

public class DObjectDetails extends ContainerWidget {

    DObjectDetails() {
        SimplePanel sp = new SimplePanel();
        sp.fitToParent();

        initWidget(sp);
    }

}

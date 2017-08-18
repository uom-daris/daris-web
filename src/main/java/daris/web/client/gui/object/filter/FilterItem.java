package daris.web.client.gui.object.filter;

import arc.gui.ValidatedInterfaceComponent;
import daris.web.client.model.object.filter.Filter;

public abstract class FilterItem<T extends Filter> extends ValidatedInterfaceComponent {

    public abstract T filter();

}

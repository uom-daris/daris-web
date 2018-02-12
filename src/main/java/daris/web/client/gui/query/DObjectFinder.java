package daris.web.client.gui.query;

import daris.web.client.gui.query.item.DObjectTypeFilterField;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.query.item.ProjectFilterField;
import daris.web.client.gui.query.item.StringFilterFieldGroup;
import daris.web.client.gui.query.item.TimePeriodFilterFieldGroup;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.query.sort.SortKey;

public class DObjectFinder extends QueryInterface {

    public DObjectFinder() {
        super("daris:pssd-object has value");
    }

    @Override
    protected void initializeQuery(DObjectQueryResultCollectionRef rc) {
        rc.addXPath("daris:pssd-object/description", "description");
        rc.addXPath("mtime", "last-modified");
        rc.addSortKey("mtime", SortKey.Order.DESCENDING);
        rc.addSortKey("daris:pssd-object/type", SortKey.Order.DESCENDING);
    }

    @Override
    protected void addFilterItems(FilterForm form) {
        form.add(new ProjectFilterField());
        form.add(new DObjectTypeFilterField());
        form.add(new StringFilterFieldGroup("daris:pssd-object/name", "Name", "Object name"));
        form.add(new StringFilterFieldGroup("daris:pssd-object/description", "Description", "Object description"));
        form.add(new TimePeriodFilterFieldGroup("mtime", "Last Modified", "Last modified"));
    }

    @Override
    protected String title() {
        return "Find DaRIS objects";
    }

}

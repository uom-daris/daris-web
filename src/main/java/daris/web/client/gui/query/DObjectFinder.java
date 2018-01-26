package daris.web.client.gui.query;

import java.util.Date;

import daris.web.client.gui.query.item.DObjectTypeFilterField;
import daris.web.client.gui.query.item.DateFilterFieldGroup;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.query.item.ProjectFilterField;
import daris.web.client.gui.query.item.StringFilterFieldGroup;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.query.SortOrder;

public class DObjectFinder extends QueryInterface {

    public DObjectFinder() {
        super("daris:pssd-object has value");
    }

    @Override
    protected void initializeQuery(DObjectQueryResultCollectionRef rc) {
        rc.addXPath("daris:pssd-object/description", "description");
        rc.addXPath("mtime", "last-modified");
        rc.addSortKey("mtime", SortOrder.DESCEND);
        rc.addSortKey("daris:pssd-object/type", SortOrder.ASCEND);
    }

    @Override
    protected void addFilterItems(FilterForm form) {
        form.add(new ProjectFilterField());
        form.add(new DObjectTypeFilterField());
        form.add(new StringFilterFieldGroup("daris:pssd-object/name", "name", "Object name"));
        form.add(new StringFilterFieldGroup("daris:pssd-object/description", "description", "Object description"));
        form.add(new DateFilterFieldGroup("mtime", "last modified", "Last modified", null, new Date(), new Date(), new Date(), true));
    }

    @Override
    protected String title() {
        return "Find DaRIS objects";
    }

}

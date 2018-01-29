package daris.web.client.gui.query;

import daris.web.client.gui.query.item.TimePeriodFilterFieldGroup;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.query.item.ProjectFilterField;
import daris.web.client.gui.query.item.StringFilterFieldGroup;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.query.SortOrder;

public class DicomStudyFinder extends QueryInterface {

    public DicomStudyFinder() {
        super("model='om.pssd.study' and mf-dicom-study has value");
    }

    protected void initializeQuery(DObjectQueryResultCollectionRef rc) {
        rc.addXPath("mf-dicom-study/sdate", "study-date");
        rc.addXPath("mf-dicom-study/ingest/date", "ingest-date");
        rc.addSortKey("mf-dicom-study/sdate", SortOrder.DESCEND);
        rc.addSortKey("cid", SortOrder.DESCEND);
    }

    protected void addFilterItems(FilterForm form) {
        form.add(new ProjectFilterField());
        form.add(new StringFilterFieldGroup("daris:pssd-object/name", "Name", "Object name"));
        form.add(new TimePeriodFilterFieldGroup("mf-dicom-study/sdate", "Study Date", "Study date"));
        form.add(new TimePeriodFilterFieldGroup("mf-dicom-study/ingest/date", "Ingest Date",
                "The date of ingest into DaRIS."));
    }

    @Override
    protected String title() {
        return "Find DICOM studies";
    }

}

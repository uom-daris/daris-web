package daris.web.client.gui.query;

import arc.mf.dtype.DictionaryEnumerationSource;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.IntegerType;
import arc.mf.expr.StandardOperators;
import daris.web.client.gui.query.item.EnumFilterField;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.query.item.IntegerFilterFieldGroup;
import daris.web.client.gui.query.item.ProjectFilterField;
import daris.web.client.gui.query.item.StringFilterFieldGroup;
import daris.web.client.gui.query.item.TimePeriodFilterFieldGroup;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.query.sort.SortKey;

public class DicomDatasetFinder extends QueryInterface {

    public DicomDatasetFinder() {
        super("model='om.pssd.dataset' and mf-dicom-series has value");
    }

    @Override
    protected void initializeQuery(DObjectQueryResultCollectionRef rc) {
        rc.addXPath("mf-dicom-series/sdate", "series-date");
        rc.addXPath("mf-dicom-series/modality", "modality");
        rc.addXPath("mf-dicom-series/protocol", "protocol");
        rc.addXPath("mf-dicom-series/description", "description");
        rc.addXPath("mf-dicom-series/size", "size");
        rc.addSortKey("mf-dicom-series/sdate", SortKey.Order.DESCENDING);
        rc.addSortKey("cid", SortKey.Order.DESCENDING);
    }

    @Override
    protected void addFilterItems(FilterForm form) {
        form.add(new ProjectFilterField());
        form.add(new StringFilterFieldGroup("daris:pssd-object/name", "Name", "Object name"));
        form.add(new TimePeriodFilterFieldGroup("mf-dicom-series/sdate", "Series Date", "Series date"));
        form.add(new EnumFilterField<String>("mf-dicom-series/modality", "Modality", "Modality",
                new EnumerationType<String>(new DictionaryEnumerationSource("daris:pssd.dicom.modality", false))));
        form.add(new StringFilterFieldGroup("mf-dicom-series/protocol", "Protocol", "Protocol"));
        form.add(new StringFilterFieldGroup("mf-dicom-series/description", "Description", "Description."));
        form.add(new IntegerFilterFieldGroup("mf-dicom-series/size", "Size", "Number of image instances.",
                StandardOperators.GREATER_THAN_OR_EQUAL, IntegerType.POSITIVE_ONE, 1, 1, 10000));
    }

    @Override
    protected String title() {
        return "Find DICOM datasets (series)";
    }

}

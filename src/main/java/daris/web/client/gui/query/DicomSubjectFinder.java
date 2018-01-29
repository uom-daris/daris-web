package daris.web.client.gui.query;

import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.query.item.DateFilterFieldGroup;
import daris.web.client.gui.query.item.EnumFilterField;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.query.item.ProjectFilterField;
import daris.web.client.gui.query.item.StringFilterFieldGroup;
import daris.web.client.gui.query.item.TimePeriodFilterFieldGroup;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.query.SortOrder;

public class DicomSubjectFinder extends QueryInterface {

    public DicomSubjectFinder() {
        super("model='om.pssd.subject' and (mf-dicom-patient has value or cid contains (mf-dicom-study has value))");
    }

    @Override
    protected void initializeQuery(DObjectQueryResultCollectionRef rc) {
        rc.addXPath("mf-dicom-patient/id", "patient-id");
        rc.addXPath("mf-dicom-patient/name[@type='first']", "patient-first-name");
        rc.addXPath("mf-dicom-patient/name[@type='last']", "patient-last-name");
        rc.addXPath("mf-dicom-patient/sex", "patient-sex");
        rc.addXPath("mf-dicom-patient/dob", "patient-birth-date");
        rc.addXPath("mtime", "last-modified");
        rc.addSortKey("mtime", SortOrder.DESCEND);
    }

    @Override
    protected void addFilterItems(FilterForm form) {
        form.add(new ProjectFilterField());
        form.add(new StringFilterFieldGroup("daris:pssd-object/name", "Name", "Subject name"));
        form.add(new StringFilterFieldGroup("mf-dicom-patient/id", "Patient ID", "Patient ID"));
        form.add(new StringFilterFieldGroup("mf-dicom-patient/name[@type='first']", "Patient First Name",
                "Patient first name"));
        form.add(new StringFilterFieldGroup("mf-dicom-patient/name[@type='last']", "Patient Last Name",
                "Patient last name"));
        form.add(new EnumFilterField<String>("mf-dicom-patient/sex", "Patient Sex", "Patient sex",
                new EnumerationType<String>(new String[] { "male", "female", "other" })));
        try {
            form.add(new DateFilterFieldGroup("mf-dicom-patient/dob", "Patient Birth Date", "Patient birth date",
                    false));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        form.add(new TimePeriodFilterFieldGroup("mtime", "Last Modified", "Last modified"));
    }

    @Override
    protected String title() {
        return "Find DICOM subjects";
    }

}

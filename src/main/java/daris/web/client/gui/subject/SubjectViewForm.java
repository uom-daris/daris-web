package daris.web.client.gui.subject;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectViewForm;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.subject.Subject;

public class SubjectViewForm extends DObjectViewForm<Subject> {

    public static final String PRIVATE_METADATA_TAB_NAME = "Private Metadata";
    public static final String PUBLIC_METADATA_TAB_NAME = "Public Metadata";

    public SubjectViewForm(Subject o) {
        super(o);
    }

    @Override
    protected void updateOtherTabs() {
        updatePrivateMetadataTab();
        updatePublicMetadataTab();
    }

    @Override
    protected void appendToInterfaceForm(Form form) {

        Subject subject = object();

        /*
         * method
         */
        Field<MethodRef> method = new Field<MethodRef>(new FieldDefinition("Method", "method", ConstantType.DEFAULT,
                "The Method to be used in creating this subject.", null, 1, 1));
        method.setValue(subject.method());
        form.add(method);

        /*
         * virtual
         */
        if (subject.isVirtual()) {
            Field<Boolean> virtual = new Field<Boolean>(new FieldDefinition("Virtual", "virtual",
                    BooleanType.DEFAULT_TRUE_FALSE, "Is the subject a virtual subject.", null, 0, 1));
            virtual.setValue(subject.isVirtual());
            form.add(virtual);
        }

        /*
         * data-use
         */
        if (subject.dataUse() != null) {
            Field<DataUse> dataUse = new Field<DataUse>(
                    new FieldDefinition("Data Use", "data-use", ConstantType.DEFAULT,
                            "Specifies whether this Subject requires over-riding of the Project data-use specification.  Can only narrow (e.g. extended to specific). "
                                    + " 1) 'specific' means use the data only for the original specific intent, "
                                    + " 2) 'extended' means use the data for related projects and "
                                    + " 3) 'unspecified' means use the data for any research",
                            null, 0, 1));
            dataUse.setValue(subject.dataUse());
            form.add(dataUse);
        }

    }

    private void updatePrivateMetadataTab() {

        Subject subject = object();
        if (subject.privateMetadata() == null) {
            removeTab(PRIVATE_METADATA_TAB_NAME);
            return;
        }
        Form metadataForm = XmlMetaForm.formFor(subject.privateMetadata(), FormEditMode.READ_ONLY);
        metadataForm.setMarginTop(10);
        metadataForm.setMarginLeft(10);
        metadataForm.setWidth100();
        metadataForm.render();
        putTab(PRIVATE_METADATA_TAB_NAME, "Private metadata", new ScrollPanel(metadataForm, ScrollPolicy.AUTO));
    }

    private void updatePublicMetadataTab() {

        Subject subject = object();
        if (subject.publicMetadata() == null) {
            removeTab(PUBLIC_METADATA_TAB_NAME);
            return;
        }
        Form metadataForm = XmlMetaForm.formFor(subject.publicMetadata(), FormEditMode.READ_ONLY);
        metadataForm.setMarginTop(10);
        metadataForm.setMarginLeft(10);
        metadataForm.setWidth100();
        metadataForm.render();
        putTab(PUBLIC_METADATA_TAB_NAME, "Public metadata", new ScrollPanel(metadataForm, ScrollPolicy.AUTO));
    }
}

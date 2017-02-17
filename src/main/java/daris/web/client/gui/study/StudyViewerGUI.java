package daris.web.client.gui.study;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.StringType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectViewerGUI;
import daris.web.client.model.study.Study;

public class StudyViewerGUI extends DObjectViewerGUI<Study> {

    public static final String METHOD_METADATA_TAB_NAME = "Method Metadata";

    public StudyViewerGUI(Study o) {
        super(o);
    }

    @Override
    protected void appendToInterfaceForm(Form interfaceForm) {
        Study study = object();
        /*
         * study type
         */
        Field<String> studyType = new Field<String>(
                new FieldDefinition("Study Type", "type", StringType.DEFAULT, "Study Type", null, 1, 1));
        studyType.setInitialValue(study.studyType());
        interfaceForm.add(studyType);

        /*
         * processed?
         */
        if (study.processed() != null) {
            Field<Boolean> processed = new Field<Boolean>(
                    new FieldDefinition("Processed", "processed", BooleanType.DEFAULT_YES_NO,
                            "Processed: is the Study intended to hold processed DataSets (true), non-processed DataSets (false) or unknown/mix (don't set).",
                            null, 0, 1));
            processed.setInitialValue(study.processed());
        }

        /*
         * method { id, step }
         */
        FieldGroup method = new FieldGroup(
                new FieldDefinition("Method", "method", DocType.DEFAULT, "method", null, 1, 1));
        Field<String> methodCid = new Field<String>(
                new FieldDefinition("ID", "id", ConstantType.DEFAULT, "id", null, 1, 1));
        methodCid.setInitialValue(study.exMethodCid());
        method.add(methodCid);

        Field<String> methodStep = new Field<String>(
                new FieldDefinition("Step", "step", ConstantType.DEFAULT, "step", null, 1, 1));
        methodStep.setInitialValue(study.stepPath());
        method.add(methodStep);

        interfaceForm.add(method);

    }

    protected void updateOtherTabs() {
        updateMethodMetadataTab();
    }

    private void updateMethodMetadataTab() {
        Study study = object();
        if (study.methodMetadata() == null) {
            removeTab(METHOD_METADATA_TAB_NAME);
            return;
        }
        Form metadataForm = XmlMetaForm.formFor(study.methodMetadata(), FormEditMode.READ_ONLY);
        metadataForm.setMarginTop(10);
        metadataForm.setMarginLeft(10);
        metadataForm.setWidth100();
        metadataForm.render();
        putTab(METHOD_METADATA_TAB_NAME, "Method metadata", new ScrollPanel(metadataForm, ScrollPolicy.AUTO));
    }

}

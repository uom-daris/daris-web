package daris.web.client.gui.dicom.exports;

import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FieldSet;
import arc.gui.form.FieldSetListener;
import arc.gui.form.FieldValidHandler;
import arc.gui.form.FieldValueValidator;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.Validity;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.IntegerType;
import arc.mf.dtype.StringType;
import arc.mf.object.BackgroundObjectMessageResponse;
import daris.web.client.gui.background.BackgroundServiceMonitor;
import daris.web.client.model.dicom.DicomAE;
import daris.web.client.model.dicom.exports.DicomSend;
import daris.web.client.model.dicom.exports.DicomSend.ElementName;
import daris.web.client.model.dicom.exports.DicomSend.ValueReference;
import daris.web.client.model.dicom.exports.DicomSendCalledAEEnum;
import daris.web.client.model.dicom.exports.DicomSendCallingAETitleEnum;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.util.SizeUtil;

@SuppressWarnings("rawtypes")
public class DicomSendForm extends ValidatedInterfaceComponent implements AsynchronousAction {

    private static enum ElementAction {
        // @formatter:off
        NONE("none"),
        ANONYMIZE("anonymize"),
        SET_VALUE("set value"),
        SET_VALUE_TO_SUBJECT_CID("set value to subject.cid"),
        SET_VALUE_TO_STUDY_CID("set value to study.cid"),
        SET_VALUE_TO_PATIENT_NAME("set value to patient.name"),
        SET_VALUE_TO_PATIENT_ID("set value to patient.id");
        // @formatter:on

        private String _stringValue;

        ElementAction(String stringValue) {
            _stringValue = stringValue;
        }

        @Override
        public final String toString() {
            return _stringValue;
        }
    }

    private VerticalPanel _vp;
    private Form _form;
    private HTML _status;
    private DicomSend _ds;
    private DObject _object;
    private CollectionSummary _summary;

    DicomSendForm(DObject object, CollectionSummary summary) {
        _ds = new DicomSend(object.citeableId(),null);
        _object = object;
        _summary = summary;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _form = new Form(FormEditMode.CREATE);
        _form.setPaddingTop(25);
        _form.setPaddingLeft(50);

        FieldGroup sourceFieldGroup = new FieldGroup(
                new FieldDefinition("Source", DocType.DEFAULT, "Source DICOM collection", null, 1, 1));

        Field<String> sourceCollectionField = new Field<String>(
                new FieldDefinition("Collection", ConstantType.DEFAULT, "Source collection", null, 1, 1));
        sourceCollectionField.setValue(_object.typeAndId(), false);
        sourceFieldGroup.add(sourceCollectionField);

        Field<String> sourceSummaryField = new Field<String>(
                new FieldDefinition("Summary", ConstantType.DEFAULT, "Source summary", null, 1, 1));
        sourceSummaryField.setValue("" + _summary.numberOfDatasets() + " DICOM series, "
                + SizeUtil.getHumanReadableSize(_summary.totalDicomDatasetSize()), false);
        sourceFieldGroup.add(sourceSummaryField);

        _form.add(sourceFieldGroup);

        /*
         * calling application entity
         */
        FieldGroup callingAEFieldGroup = new FieldGroup(
                new FieldDefinition("Calling Application Entity", DocType.DEFAULT, null, null, 1, 1));
        final Field<String> callingAETitleField = new Field<String>(new FieldDefinition("AE Title",
                new EnumerationType<String>(new DicomSendCallingAETitleEnum()), null, null, 1, 1));
        callingAETitleField.setRenderOptions(new FieldRenderOptions().setWidth(250));
        callingAETitleField.addListener(new FormItemListener<String>() {
            @Override
            public void itemValueChanged(FormItem<String> f) {
                _ds.setCallingAETitle(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {
            }
        });
        callingAEFieldGroup.add(callingAETitleField);
        _form.add(callingAEFieldGroup);
        /*
         * called application entity
         */
        FieldGroup calledAEFieldGroup = new FieldGroup(
                new FieldDefinition("Called Application Entity", DocType.DEFAULT, null, null, 1, 1));
        Field<DicomAE> calledAESelectField = new Field<DicomAE>(new FieldDefinition("Select",
                new EnumerationType<DicomAE>(new DicomSendCalledAEEnum()), "Select an application entity", null, 0, 1));
        calledAESelectField.setRenderOptions(new FieldRenderOptions().setWidth(250));
        calledAEFieldGroup.add(calledAESelectField);
        final Field<String> calledAETitleField = new Field<String>(
                new FieldDefinition("AE Title", StringType.DEFAULT, null, null, 1, 1));
        calledAETitleField.setRenderOptions(new FieldRenderOptions().setWidth(232));
        calledAETitleField.addListener(new FormItemListener<String>() {
            @Override
            public void itemValueChanged(FormItem<String> f) {
                _ds.setCalledAETitle(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {
            }
        });
        calledAEFieldGroup.add(calledAETitleField);
        final Field<String> calledAEHostField = new Field<String>(
                new FieldDefinition("Host", StringType.DEFAULT, null, null, 1, 1));
        calledAEHostField.setRenderOptions(new FieldRenderOptions().setWidth(232));
        calledAEHostField.addListener(new FormItemListener<String>() {
            @Override
            public void itemValueChanged(FormItem<String> f) {
                _ds.setCalledAEHost(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {
            }
        });
        calledAEFieldGroup.add(calledAEHostField);
        final Field<Integer> calledAEPortField = new Field<Integer>(
                new FieldDefinition("Port", new IntegerType(0, 65535), null, null, 1, 1));
        calledAEPortField.setRenderOptions(new FieldRenderOptions().setWidth(232));
        calledAEPortField.addListener(new FormItemListener<Integer>() {
            @Override
            public void itemValueChanged(FormItem<Integer> f) {
                _ds.setCalledAEPort(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Integer> f, Property property) {
            }
        });
        calledAEFieldGroup.add(calledAEPortField);
        calledAESelectField.addListener(new FormItemListener<DicomAE>() {
            @Override
            public void itemValueChanged(FormItem<DicomAE> f) {
                DicomAE ae = f.value();
                if (ae != null) {
                    calledAETitleField.setValue(ae.title());
                    calledAEHostField.setValue(ae.host());
                    calledAEPortField.setValue(ae.port());
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<DicomAE> f, Property property) {
            }
        });
        _form.add(calledAEFieldGroup);
        /*
         * override metadata
         */
        FieldGroup overrideFieldGroup = new FieldGroup(
                new FieldDefinition("Override", DocType.DEFAULT, "Override DICOM header elements.", null, 1, 1));
        // Patient Name
        FieldGroup patientNameFG = new FieldGroup(new FieldDefinition("Patient Name", DocType.DEFAULT,
                "The patient name element(0010,0010).", null, 0, 1));
        Field<ElementAction> patientNameAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        patientNameAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        patientNameFG.add(patientNameAction);
        Field<String> patientNameValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        patientNameValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        patientNameValue.setVisible(false);
        patientNameFG.add(patientNameValue);
        patientNameFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.PATIENT_NAME);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.PATIENT_NAME);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.PATIENT_NAME, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.PATIENT_NAME, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.PATIENT_NAME, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.PATIENT_NAME, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.PATIENT_NAME, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        patientNameAction.setInitialValue(ElementAction.ANONYMIZE, true);
        overrideFieldGroup.add(patientNameFG);
        // Patient Id
        FieldGroup patientIdFG = new FieldGroup(
                new FieldDefinition("Patient ID", DocType.DEFAULT, "The patient id element(0010,0020).", null, 0, 1));
        Field<ElementAction> patientIdAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        patientIdAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        patientIdAction.setInitialValue(ElementAction.NONE, false);
        patientIdFG.add(patientIdAction);
        Field<String> patientIdValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        patientIdValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        patientIdValue.setVisible(false);
        patientIdFG.add(patientIdValue);
        patientIdFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.PATIENT_ID);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.PATIENT_ID);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.PATIENT_ID, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.PATIENT_ID, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.PATIENT_ID, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.PATIENT_ID, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.PATIENT_ID, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        overrideFieldGroup.add(patientIdFG);
        // Study Id
        FieldGroup studyIdFG = new FieldGroup(
                new FieldDefinition("Study ID", DocType.DEFAULT, "The study id element(0020,0010).", null, 0, 1));
        Field<ElementAction> studyIdAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        studyIdAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        studyIdAction.setInitialValue(ElementAction.NONE, false);
        studyIdFG.add(studyIdAction);
        Field<String> studyIdValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        studyIdValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        studyIdValue.setVisible(false);
        studyIdFG.add(studyIdValue);
        studyIdFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.STUDY_ID);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.STUDY_ID);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.STUDY_ID, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.STUDY_ID, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.STUDY_ID, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.STUDY_ID, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.STUDY_ID, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        overrideFieldGroup.add(studyIdFG);
        // Performing Physician Name
        FieldGroup performingPhysicianNameFG = new FieldGroup(new FieldDefinition("Performing Physician Name",
                DocType.DEFAULT, "The performing physician name element(0008,1050).", null, 0, 1));
        Field<ElementAction> performingPhysicianNameAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        performingPhysicianNameAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        performingPhysicianNameAction.setInitialValue(ElementAction.NONE, false);
        performingPhysicianNameFG.add(performingPhysicianNameAction);
        Field<String> performingPhysicianNameValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        performingPhysicianNameValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        performingPhysicianNameValue.setVisible(false);
        performingPhysicianNameFG.add(performingPhysicianNameValue);
        performingPhysicianNameFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.PERFORMING_PHYSICIAN_NAME);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.PERFORMING_PHYSICIAN_NAME);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.PERFORMING_PHYSICIAN_NAME, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.PERFORMING_PHYSICIAN_NAME, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.PERFORMING_PHYSICIAN_NAME, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.PERFORMING_PHYSICIAN_NAME, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.PERFORMING_PHYSICIAN_NAME, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        overrideFieldGroup.add(performingPhysicianNameFG);
        // Referring Physician Name
        FieldGroup referringPhysicianNameFG = new FieldGroup(new FieldDefinition("Referring Physician Name",
                DocType.DEFAULT, "The referring physician name element(0008,0090).", null, 0, 1));
        Field<ElementAction> referringPhysicianNameAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        referringPhysicianNameAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        referringPhysicianNameAction.setInitialValue(ElementAction.NONE, false);
        referringPhysicianNameFG.add(referringPhysicianNameAction);
        Field<String> referringPhysicianNameValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        referringPhysicianNameValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        referringPhysicianNameValue.setVisible(false);
        referringPhysicianNameFG.add(referringPhysicianNameValue);
        referringPhysicianNameFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.REFERRING_PHYSICIAN_NAME);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.REFERRING_PHYSICIAN_NAME);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_NAME, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_NAME, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_NAME, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_NAME, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_NAME, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        overrideFieldGroup.add(referringPhysicianNameFG);
        // Referring Physician Phone
        FieldGroup referringPhysicianPhoneFG = new FieldGroup(new FieldDefinition("Referring Physician Phone",
                DocType.DEFAULT, "The referring physician phone element(0008,0094).", null, 0, 1));
        Field<ElementAction> referringPhysicianPhoneAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        referringPhysicianPhoneAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        referringPhysicianPhoneAction.setInitialValue(ElementAction.NONE, false);
        referringPhysicianPhoneFG.add(referringPhysicianPhoneAction);
        Field<String> referringPhysicianPhoneValue = new Field<String>(
                new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        referringPhysicianPhoneValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        referringPhysicianPhoneValue.setVisible(false);
        referringPhysicianPhoneFG.add(referringPhysicianPhoneValue);
        referringPhysicianPhoneFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {
                switch (action) {
                case NONE:
                    _ds.removeElement(ElementName.REFERRING_PHYSICIAN_PHONE);
                    break;
                case ANONYMIZE:
                    _ds.anonymizeElement(ElementName.REFERRING_PHYSICIAN_PHONE);
                    break;
                case SET_VALUE:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_PHONE, value);
                    break;
                case SET_VALUE_TO_SUBJECT_CID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_PHONE, ValueReference.SUBJECT_CID);
                    break;
                case SET_VALUE_TO_STUDY_CID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_PHONE, ValueReference.STUDY_CID);
                    break;
                case SET_VALUE_TO_PATIENT_NAME:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_PHONE, ValueReference.PATIENT_NAME);
                    break;
                case SET_VALUE_TO_PATIENT_ID:
                    _ds.setElement(ElementName.REFERRING_PHYSICIAN_PHONE, ValueReference.PATIENT_ID);
                    break;
                default:
                    break;
                }
            }
        });
        overrideFieldGroup.add(referringPhysicianPhoneFG);
        // Generic Element
        FieldGroup genericElementFG = new FieldGroup(new FieldDefinition("DICOM Element", "element", DocType.DEFAULT,
                "A generic DICOM element.", null, 0, 255));
        FieldValueValidator<String> validator = new FieldValueValidator<String>() {
            @Override
            public void validate(Field<String> f, FieldValidHandler vh) {
                String v = f.value();
                if (v != null && v.matches("[0-9a-fA-F]{8}")) {
                    vh.setValid();
                } else {
                    vh.setInvalid("Must be a 8 digit hexadecimal number.");
                }
            }
        };
        Field<String> geTag = new Field<String>(
                new FieldDefinition("Tag", "tag", new StringType(8, 8, 8), "The DICOM element tag.", null, 0, 1));
        geTag.setRenderOptions(new FieldRenderOptions().setWidth(182));
        geTag.addValueValidator(validator);
        genericElementFG.add(geTag);
        Field<ElementAction> geAction = new Field<ElementAction>(new FieldDefinition("Action",
                new EnumerationType<ElementAction>(ElementAction.values()), null, null, 0, 1));
        geAction.setRenderOptions(new FieldRenderOptions().setWidth(200));
        geAction.setInitialValue(ElementAction.NONE, false);
        genericElementFG.add(geAction);
        Field<String> geValue = new Field<String>(new FieldDefinition("Value", StringType.DEFAULT, null, null, 0, 1));
        geValue.setVisible(false);
        geValue.setRenderOptions(new FieldRenderOptions().setWidth(182));
        genericElementFG.add(geValue);
        genericElementFG.addListener(new DicomElementChangeListener() {
            @Override
            public void elementChanged(ElementAction action, String value) {

            }
        });
        overrideFieldGroup.add(genericElementFG);
        overrideFieldGroup.addListener(new FieldSetListener() {

            @Override
            public void addedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
            }

            @Override
            public void removedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
            }

            @Override
            public void updatedFields(FieldSet s) {
            }

            @Override
            public void updatedFieldValue(FieldSet overrideFG, FormItem f) {
                List<FormItem> geItems = overrideFG.fields("DICOM Element");
                if (geItems == null || geItems.isEmpty()) {
                    return;
                }
                for (FormItem geItem : geItems) {
                    FieldSet geFG = (FieldSet) geItem;
                    FormItem tagItem = geFG.field("Tag");
                    String tag = (String) tagItem.value();
                    if (tag == null || tag.trim().isEmpty() || !tagItem.valid().valid()) {
                        continue;
                    }
                    FormItem actionItem = geFG.field("Action");
                    ElementAction action = (ElementAction) actionItem.value();
                    FormItem valueItem = geFG.field("Value");
                    String value = (String) valueItem.value();

                    switch (action) {
                    case NONE:
                        _ds.removeElement(tag);
                        break;
                    case ANONYMIZE:
                        _ds.anonymizeElement(tag);
                        break;
                    case SET_VALUE:
                        if (value != null) {
                            _ds.setElement(tag, value);
                        }
                        break;
                    case SET_VALUE_TO_SUBJECT_CID:
                        _ds.setElement(tag, ValueReference.SUBJECT_CID);
                        break;
                    case SET_VALUE_TO_STUDY_CID:
                        _ds.setElement(tag, ValueReference.STUDY_CID);
                        break;
                    case SET_VALUE_TO_PATIENT_NAME:
                        _ds.setElement(tag, ValueReference.PATIENT_NAME);
                        break;
                    case SET_VALUE_TO_PATIENT_ID:
                        _ds.setElement(tag, ValueReference.PATIENT_ID);
                        break;
                    default:
                        break;
                    }
                }
            }

            @Override
            public void updatedFieldState(FieldSet s, FormItem f, Property property) {
            }
        });
        _form.add(overrideFieldGroup);
        addMustBeValid(_form);
        _form.render();
        _vp.add(new ScrollPanel(_form, ScrollPolicy.AUTO));
        _status = new HTML();
        _status.setTextAlign(TextAlign.CENTER);
        _status.setFontSize(11);
        _status.setHeight(22);
        _status.element().getStyle().setLineHeight(22, Unit.PX);
        _status.setBorder(1, RGB.GREY_BBB);
        _status.setColour(RGB.RED);
        _vp.add(_status);
        notifyOfChangeInState();
    }

    private static abstract class DicomElementChangeListener implements FieldSetListener {
        @Override
        public void addedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
        }

        @Override
        public void removedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
        }

        @Override
        public void updatedFields(FieldSet s) {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void updatedFieldValue(FieldSet s, FormItem f) {
            FormItem actionItem = s.field("Action");
            ElementAction action = (ElementAction) actionItem.value();
            FormItem valueItem = s.field("Value");
            String value = (String) valueItem.value();
            if (f == s.field("Action")) {
                if (action != ElementAction.SET_VALUE) {
                    ((Field<String>) valueItem).setValue(null);
                }
                valueItem.setVisible(action == ElementAction.SET_VALUE);
            }
            elementChanged(action, value);
        }

        @Override
        public void updatedFieldState(FieldSet s, FormItem f, Property property) {
        }

        public abstract void elementChanged(ElementAction action, String value);

    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(final ActionListener l) {
        _ds.send(new BackgroundObjectMessageResponse() {
            @Override
            public void responded(Long id) {
                l.executed(id != null);
                new BackgroundServiceMonitor(id).show(_form.window());
            }
        });
    }
}

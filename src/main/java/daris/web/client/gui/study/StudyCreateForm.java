package daris.web.client.gui.study;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FieldSet;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.form.FormListener;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.DictionaryEnumerationSource;
import arc.mf.dtype.DocType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.exmethod.ExMethodStudyStepEnum;
import daris.web.client.model.exmethod.ExMethodStudyStepRef;
import daris.web.client.model.object.DObjectBuilder;
import daris.web.client.model.study.StudyCreator;
import daris.web.client.model.study.StudyTypeEnum;
import daris.web.client.model.study.StudyUpdater;
import daris.web.client.model.study.messages.StudyCreate;
import daris.web.client.model.study.messages.StudyMetadataDescribe;

public class StudyCreateForm extends DObjectCreateForm<StudyCreator> {

    private Field<String> _studyTypeField;
    private ExMethodStudyStepEnum _stepEnum;
    private Field<ExMethodStudyStepRef> _stepField;

    private Integer _methodMetadataTabId = null;
    private Form _methodMetadataForm;

    public StudyCreateForm(StudyCreator creator) {
        super(creator);
        creator.setMetadataSetter(w -> {
            if (_methodMetadataForm != null) {
                w.push("meta");
                _methodMetadataForm.save(w);
                w.pop();
            }
        });

        _stepEnum.resolve(steps -> {
            if (steps != null && steps.size() == 1) {
                _stepField.setValue(steps.get(0));
            }
        });
    }

    private void updateMetadataTab() {
        if (_methodMetadataForm != null) {
            removeMustBeValid(_methodMetadataForm);
            _methodMetadataForm = null;
        }
        if (_methodMetadataTabId != null) {
            tabs.removeTabById(_methodMetadataTabId);
            _methodMetadataTabId = null;
        }
        XmlElement me = creator.methodMetadataForCreate();
        if (me == null) {
            return;
        }
        _methodMetadataForm = XmlMetaForm.formFor(me, FormEditMode.CREATE);
        _methodMetadataForm.setPaddingTop(15);
        _methodMetadataForm.setPaddingLeft(20);
        _methodMetadataForm.setPaddingRight(20);
        _methodMetadataForm.render();
        addMustBeValid(_methodMetadataForm);
        _methodMetadataTabId = tabs.addTab("Method Metadata", null,
                new ScrollPanel(_methodMetadataForm, ScrollPolicy.AUTO));

    }

    @Override
    public void execute(ActionListener l) {
        new StudyCreate(creator).send(id -> {
            l.executed(id != null);
        });
    }

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);
        _stepEnum = new ExMethodStudyStepEnum(creator.parentObject(), creator.studyType());
        _stepField = new Field<ExMethodStudyStepRef>(new FieldDefinition("Step", "Step",
                new EnumerationType<ExMethodStudyStepRef>(_stepEnum), "Step", null, 1, 1));
        _stepField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _stepField.setValue(creator.step(), false);
        _stepField.addListener(new FormItemListener<ExMethodStudyStepRef>() {

            @Override
            public void itemValueChanged(FormItem<ExMethodStudyStepRef> f) {
                ExMethodStudyStepRef step = f.value();
                if (!ObjectUtil.equals(step, creator.step())) {
                    creator.setStep(step);
                    _studyTypeField.setValue(step == null ? null : step.studyType());
                    if (step != null) {
                        new StudyMetadataDescribe(creator.parentObject().citeableId(), creator.step().path())
                                .send(me -> {
                                    creator.setMethodMetadataForCreate(me);
                                    updateMetadataTab();
                                });
                    } else {
                        creator.setMethodMetadataForCreate(null);
                        updateMetadataTab();
                    }
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<ExMethodStudyStepRef> f, Property property) {

            }
        });
        interfaceForm.add(_stepField);

        _studyTypeField = new Field<String>(new FieldDefinition("Study Type", "Study_Type",
                new EnumerationType<String>(new StudyTypeEnum(creator.parentObject().citeableId())), "Study type.",
                null, 1, 1));
        _studyTypeField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _studyTypeField.setValue(creator.studyType(), false);
        _studyTypeField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                String studyType = f.value();
                if (!ObjectUtil.equals(studyType, creator.studyType())) {
                    creator.setStudyType(studyType);
                    _stepEnum.setStudyType(studyType);
                    _stepEnum.resolve(steps -> {
                        if (steps != null && steps.size() == 1) {
                            _stepField.setValue(steps.get(0));
                        }
                        updateMetadataTab();
                    });
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        interfaceForm.add(_studyTypeField);

        /*
         * other-id
         */
        addOtherIdFields(interfaceForm, this.creator);

        Field<Boolean> processedField = new Field<Boolean>(new FieldDefinition("Processed",
                BooleanType.DEFAULT_TRUE_FALSE, "Is the dataset processed?", null, 0, 1));
        processedField.setInitialValue(creator.processed(), false);
        processedField.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                creator.setProcessed(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        interfaceForm.add(processedField);
    }

    static void addOtherIdFields(Form interfaceForm, DObjectBuilder builder) {

        List<SimpleEntry<String, String>> otherIds = null;
        if (builder instanceof StudyUpdater) {
            otherIds = ((StudyUpdater) builder).otherIds();
        } else if (builder instanceof StudyCreator) {
            otherIds = ((StudyCreator) builder).otherIds();
        }
//        FormItemListener<String> fil = new FormItemListener<String>() {
//
//            @SuppressWarnings("rawtypes")
//            @Override
//            public void itemValueChanged(FormItem<String> f) {
//                Form form = f.form();
//                List<FormItem> items = form.fieldsByName("other-id");
//                List<SimpleEntry<String, String>> otherIds = new ArrayList<SimpleEntry<String, String>>();
//                for (FormItem item : items) {
//                    FieldGroup fg = (FieldGroup) item;
//                    String type = fg.fieldsByName("type").get(0).valueAsString();
//                    String value = fg.fieldsByName("value").get(0).valueAsString();
//                    if (type != null && value != null) {
//                        SimpleEntry<String, String> entry = new SimpleEntry<String, String>(type, value);
//                        if (!otherIds.contains(entry)) {
//                            otherIds.add(entry);
//                        }
//                    }
//                }
//                if (builder instanceof StudyUpdater) {
//                    if (otherIds.isEmpty()) {
//                        ((StudyUpdater) builder).setOtherIds(null);
//                    } else {
//                        ((StudyUpdater) builder).setOtherIds(otherIds);
//                    }
//                } else if (builder instanceof StudyCreator) {
//                    if (otherIds.isEmpty()) {
//                        ((StudyCreator) builder).setOtherIds(null);
//                    } else {
//                        ((StudyCreator) builder).setOtherIds(otherIds);
//                    }
//                }
//            }
//
//            @Override
//            public void itemPropertyChanged(FormItem<String> f, Property property) {
//
//            }
//        };
        if (otherIds != null) {
            for (SimpleEntry<String, String> entry : otherIds) {
                FieldGroup otherIdFG = new FieldGroup(
                        new FieldDefinition("other-id", DocType.DEFAULT, null, null, 0, Integer.MAX_VALUE));
                Field<String> otherIdTypeField = new Field<String>(new FieldDefinition("type",
                        new EnumerationType<String>(
                                new DictionaryEnumerationSource("daris:pssd.study.other-id.types", false)),
                        "The type (authority) of this identifier.", null, 1, 1));
                otherIdTypeField.setInitialValue(entry.getKey());
                otherIdTypeField.setRenderOptions(new FieldRenderOptions().setWidth(338));
//                otherIdTypeField.addListener(fil);
                otherIdFG.add(otherIdTypeField);

                Field<String> otherIdValueField = new Field<String>(new FieldDefinition("value", StringType.DEFAULT,
                        "An arbitrary identifier for the Study supplied by some other authority.", null, 1, 1));
                otherIdValueField.setInitialValue(entry.getValue());
                otherIdValueField.setRenderOptions(new FieldRenderOptions().setWidth(320));
//                otherIdValueField.addListener(fil);
                otherIdFG.add(otherIdValueField);
                interfaceForm.add(otherIdFG);
            }
        } else {
            FieldGroup otherIdFG = new FieldGroup(
                    new FieldDefinition("other-id", DocType.DEFAULT, null, null, 0, Integer.MAX_VALUE));
            Field<String> otherIdTypeField = new Field<String>(new FieldDefinition("type",
                    new EnumerationType<String>(
                            new DictionaryEnumerationSource("daris:pssd.study.other-id.types", false)),
                    "The type (authority) of this identifier.", null, 1, 1));
            otherIdTypeField.setRenderOptions(new FieldRenderOptions().setWidth(338));
//            otherIdTypeField.addListener(fil);
            otherIdFG.add(otherIdTypeField);

            Field<String> otherIdValueField = new Field<String>(new FieldDefinition("value", StringType.DEFAULT,
                    "An arbitrary identifier for the Study supplied by some other authority.", null, 1, 1));
            otherIdValueField.setRenderOptions(new FieldRenderOptions().setWidth(320));
//            otherIdValueField.addListener(fil);
            otherIdFG.add(otherIdValueField);
            interfaceForm.add(otherIdFG);
        }

        interfaceForm.addListener(new FormListener() {

            @Override
            public void rendering(Form f) {

            }

            @Override
            public void rendered(Form f) {

            }

            @SuppressWarnings("rawtypes")
            @Override
            public void formValuesUpdated(Form f) {
                List<FormItem> is = f.fieldsByName("other-id");
                if (is != null && !is.isEmpty()) {
                    List<SimpleEntry<String, String>> otherIds = new ArrayList<SimpleEntry<String, String>>();
                    for (FormItem i : is) {
                        FieldSet fs = (FieldSet) i;
                        String type = fs.fieldByName("type").valueAsString();
                        String value = fs.fieldByName("value").valueAsString();
                        if (type != null && value != null) {
                            SimpleEntry<String, String> otherId = new SimpleEntry<String, String>(type, value);
                            otherIds.add(otherId);
                        }
                    }
                    if (builder instanceof StudyUpdater) {
                        if (otherIds.isEmpty()) {
                            ((StudyUpdater) builder).setOtherIds(null);
                        } else {
                            ((StudyUpdater) builder).setOtherIds(otherIds);
                        }
                    } else if (builder instanceof StudyCreator) {
                        if (otherIds.isEmpty()) {
                            ((StudyCreator) builder).setOtherIds(null);
                        } else {
                            ((StudyCreator) builder).setOtherIds(otherIds);
                        }
                    }
                }
            }

            @Override
            public void formStateUpdated(Form f, Property p) {
            }
        });
    
    }

}

package daris.web.client.gui.project;

import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FieldSet;
import arc.gui.form.FieldSetListener;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.ActionListener;
import arc.mf.client.xml.XmlElement;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.model.method.MethodEnum;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.Project;
import daris.web.client.model.project.ProjectUpdater;
import daris.web.client.model.project.messages.ProjectUpdate;

public class ProjectUpdateForm extends DObjectUpdateForm<Project> {

    private Integer _metadataTabId = null;
    private Form _metadataForm;

    public ProjectUpdateForm(Project project) {
        super(project);
        this.updater.setMetadataSetter(w -> {
            if (_metadataForm != null) {
                w.push("meta");
                _metadataForm.save(w);
                w.pop();
            }
        });
        updateMetadataTab();
    }

    private ProjectUpdater updater() {
        return (ProjectUpdater) updater;
    }

    private void updateMetadataTab() {
        if (_metadataForm != null) {
            removeMustBeValid(_metadataForm);
            _metadataForm = null;
        }
        if (_metadataTabId != null) {
            tabs.removeTabById(_metadataTabId);
            _metadataTabId = null;
        }
        XmlElement me = updater.metadataForEdit();
        if (me == null) {
            return;
        }
        _metadataForm = XmlMetaForm.formFor(me, FormEditMode.UPDATE);
        _metadataForm.setPaddingTop(15);
        _metadataForm.setPaddingLeft(20);
        _metadataForm.setPaddingRight(20);
        // _metadataForm.fitToParent();
        _metadataForm.render();
        addMustBeValid(_metadataForm);
        _metadataTabId = tabs.addTab("Metadata", null, new ScrollPanel(_metadataForm, ScrollPolicy.AUTO));

    }

    @SuppressWarnings({ "rawtypes" })
    protected void addToInterfaceForm(Form interfaceForm) {

        super.addToInterfaceForm(interfaceForm);

        FieldGroup methodFieldGroup = new FieldGroup();
        List<MethodRef> methods = updater().methods();
        if (methods != null && !methods.isEmpty()) {
            for (MethodRef method : methods) {
                Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "Method",
                        new EnumerationType<MethodRef>(new MethodEnum()), "Method", null, 1, 255));
                methodField.setRenderOptions(new FieldRenderOptions().setWidth100());
                methodField.setInitialValue(method, false);
                methodFieldGroup.add(methodField);
            }
        } else {
            Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "Method",
                    new EnumerationType<MethodRef>(new MethodEnum()), "Method", null, 1, 255));
            methodField.setRenderOptions(new FieldRenderOptions().setWidth100());
            methodFieldGroup.add(methodField);
        }
        methodFieldGroup.addListener(new FieldSetListener() {

            private void updateMethods(FieldSet s) {
                updater().clearMethods();
                List<FormItem> items = s.fields("Method");
                for (FormItem item : items) {
                    MethodRef m = (MethodRef) item.value();
                    if (m != null) {
                        updater().addMethod(m);
                    }
                }
            }

            @Override
            public void addedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
                ((Field) f).setRenderOptions(new FieldRenderOptions().setWidth100());
            }

            @Override
            public void removedField(FieldSet s, FormItem f, int idx, boolean lastUpdate) {
                updateMethods(s);
            }

            @Override
            public void updatedFields(FieldSet s) {
                updateMethods(s);
            }

            @Override
            public void updatedFieldValue(FieldSet s, FormItem f) {
                updateMethods(s);
            }

            @Override
            public void updatedFieldState(FieldSet s, FormItem f, Property property) {

            }
        });
        interfaceForm.add(methodFieldGroup);

        Field<DataUse> dataUseField = new Field<DataUse>(
                new FieldDefinition("Data Use", "Data_Use", new EnumerationType<DataUse>(DataUse.values()),
                        "Specifies the type of consent for the use of data for this project.", null, 1, 1));
        dataUseField.setInitialValue(updater().dataUse(), false);
        dataUseField.addListener(new FormItemListener<DataUse>() {

            @Override
            public void itemValueChanged(FormItem<DataUse> f) {
                updater().setDataUse(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<DataUse> f, Property property) {

            }
        });
        interfaceForm.add(dataUseField);
    }

    @Override
    public void execute(ActionListener l) {
        new ProjectUpdate(updater()).send(r -> {
            MessageBox.show(280, 100, window(), MessageBox.Position.CENTER,
                    "Project " + object.citeableId() + " has been updated.", 3);
        });
        l.executed(true);
    }

}

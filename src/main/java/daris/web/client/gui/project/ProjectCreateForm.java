package daris.web.client.gui.project;

import java.util.List;
import java.util.Set;

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
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.method.MethodEnum;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.ProjectCreator;
import daris.web.client.model.project.messages.ProjectCreate;

public class ProjectCreateForm extends DObjectCreateForm<ProjectCreator> {

    private Integer _metadataTabId = null;
    private Form _metadataForm;

    public ProjectCreateForm(ProjectCreator creator) {
        super(creator);
        creator.setMetadataSetter(w -> {
            if (_metadataForm != null) {
                w.push("meta");
                _metadataForm.save(w);
                w.pop();
            }
        });
        updateMetadataTab();
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
        XmlElement me = creator.metadataForCreate();
        if (me == null) {
            return;
        }
        _metadataForm = XmlMetaForm.formFor(me, FormEditMode.CREATE);
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

        Set<String> cidRootNames = creator.availableCidRootNames();
        if (cidRootNames != null && !cidRootNames.isEmpty()) {
            Field<String> citeableRootNameField = new Field<String>(
                    new FieldDefinition("Project ID Type", "Project_ID_Type",
                            cidRootNames.size() == 1 ? ConstantType.DEFAULT : new EnumerationType<String>(cidRootNames),
                            "Type of the project (identifier).", null, 1, 1));
            if (cidRootNames.size() == 1) {
                String cidRootName = cidRootNames.iterator().next();
                creator.setCidRootName(cidRootName);
                citeableRootNameField.setValue(cidRootName, false);
            } else {
                citeableRootNameField.addListener(new FormItemListener<String>() {

                    @Override
                    public void itemValueChanged(FormItem<String> f) {
                        creator.setCidRootName(f.value());
                    }

                    @Override
                    public void itemPropertyChanged(FormItem<String> f, Property property) {

                    }
                });
            }
            interfaceForm.add(citeableRootNameField);
        }
        Set<String> assetNamespaces = creator.availableAssetNamespaces();
        if (assetNamespaces != null && !assetNamespaces.isEmpty()) {
            Field<String> assetNamespaceField = new Field<String>(
                    new FieldDefinition("Root Asset Namespace", "Root_Asset_Namespace",
                            assetNamespaces.size() == 1 ? ConstantType.DEFAULT
                                    : new EnumerationType<String>(assetNamespaces),
                            "Root asset namespace", null, 1, 1));
            if (assetNamespaces.size() == 1) {
                String namespace = assetNamespaces.iterator().next();
                creator.setNamespace(namespace);
                assetNamespaceField.setValue(namespace, false);
            } else {
                assetNamespaceField.addListener(new FormItemListener<String>() {

                    @Override
                    public void itemValueChanged(FormItem<String> f) {
                        creator.setNamespace(f.value());
                    }

                    @Override
                    public void itemPropertyChanged(FormItem<String> f, Property property) {

                    }
                });
            }
            interfaceForm.add(assetNamespaceField);
        }

        super.addToInterfaceForm(interfaceForm);

        FieldGroup methodFieldGroup = new FieldGroup();
        Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "Method",
                new EnumerationType<MethodRef>(new MethodEnum()), "Method", null, 1, 255));
        methodField.setRenderOptions(new FieldRenderOptions().setWidth100());
        methodFieldGroup.add(methodField);
        methodFieldGroup.addListener(new FieldSetListener() {

            private void updateMethods(FieldSet s) {
                creator.clearMethods();
                List<FormItem> items = s.fields("Method");
                for (FormItem item : items) {
                    MethodRef m = (MethodRef) item.value();
                    if (m != null) {
                        creator.addMethod(m.citeableId());
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
        dataUseField.addListener(new FormItemListener<DataUse>() {

            @Override
            public void itemValueChanged(FormItem<DataUse> f) {
                creator.setDataUse(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<DataUse> f, Property property) {

            }
        });
        interfaceForm.add(dataUseField);

    }

    @Override
    public void execute(ActionListener l) {
        new ProjectCreate(creator).send(id -> {
            l.executed(id != null);
        });
    }

}

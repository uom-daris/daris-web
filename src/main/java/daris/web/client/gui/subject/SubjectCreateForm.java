package daris.web.client.gui.subject;

import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
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
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.method.MethodEnum;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.Project;
import daris.web.client.model.subject.SubjectCreator;
import daris.web.client.model.subject.messages.SubjectCreate;
import daris.web.client.model.subject.messages.SubjectMetadataDescribe;

public class SubjectCreateForm extends DObjectCreateForm<SubjectCreator> {
    private Integer _publicMetadataTabId = null;
    private Form _publicMetadataForm;

    private Integer _privateMetadataTabId = null;
    private Form _privateMetadataForm;

    public SubjectCreateForm(SubjectCreator creator) {
        super(creator);
        creator.setMetadataSetter(null);
        creator.setPublicMetadataSetter(w -> {
            if (_publicMetadataForm != null) {
                w.push("public");
                _publicMetadataForm.save(w);
                w.pop();
            }
        });
        creator.setPrivateMetadataSetter(w -> {
            if (_privateMetadataForm != null) {
                w.push("private");
                _privateMetadataForm.save(w);
                w.pop();
            }
        });
        updateMetadataTabs();
    }

    private void updatePrivateMetadataTab() {

        if (_privateMetadataForm != null) {
            removeMustBeValid(_privateMetadataForm);
            _privateMetadataForm = null;
        }
        if (_privateMetadataTabId != null) {
            tabs.removeTabById(_privateMetadataTabId);
            _privateMetadataTabId = null;
        }
        XmlElement me = creator.privateMetadataForCreate();
        if (creator.method() == null || me == null) {
            return;
        }
        _privateMetadataForm = XmlMetaForm.formFor(me, FormEditMode.CREATE);
        _privateMetadataForm.setPaddingTop(15);
        _privateMetadataForm.setPaddingLeft(20);
        _privateMetadataForm.setPaddingRight(20);
        _privateMetadataForm.render();
        addMustBeValid(_privateMetadataForm);
        _privateMetadataTabId = tabs.addTab("Private Metadata", null,
                new ScrollPanel(_privateMetadataForm, ScrollPolicy.AUTO));
    }

    private void updatePublicMetadataTab() {
        if (_publicMetadataForm != null) {
            removeMustBeValid(_publicMetadataForm);
            _publicMetadataForm = null;
        }
        if (_publicMetadataTabId != null) {
            tabs.removeTabById(_publicMetadataTabId);
            _publicMetadataTabId = null;
        }
        XmlElement me = creator.publicMetadataForCreate();
        if (creator.method() == null || me == null) {
            return;
        }
        _publicMetadataForm = XmlMetaForm.formFor(me, FormEditMode.CREATE);
        _publicMetadataForm.setPaddingTop(15);
        _publicMetadataForm.setPaddingLeft(20);
        _publicMetadataForm.setPaddingRight(20);
        _publicMetadataForm.render();
        addMustBeValid(_publicMetadataForm);
        _publicMetadataTabId = tabs.addTab("Public Metadata", null,
                new ScrollPanel(_publicMetadataForm, ScrollPolicy.AUTO));

    }

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);

        Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "Method",
                new EnumerationType<MethodRef>(new MethodEnum(creator.parentObject())), "Method", null, 1, 255));
        methodField.setRenderOptions(new FieldRenderOptions().setWidth100());
        if (creator.parentObject().resolved()) {
            List<MethodRef> methods = ((Project) creator.parentObject().referent()).methods();
            if (methods != null && methods.size() == 1) {
                creator.setMethod(methods.get(0));
                methodField.setValue(methods.get(0), false);
            }
        }
        methodField.addListener(new FormItemListener<MethodRef>() {

            @Override
            public void itemValueChanged(FormItem<MethodRef> f) {
                creator.setMethod(f.value());
                updateMetadataTabs();
            }

            @Override
            public void itemPropertyChanged(FormItem<MethodRef> f, Property property) {

            }
        });
        interfaceForm.add(methodField);

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

    private void updateMetadataTabs() {
        String methodId = creator.method();
        if (methodId == null) {
            creator.setPublicMetadataForCreate(null);
            creator.setPrivateMetadataForCreate(null);
            updatePublicMetadataTab();
            updatePrivateMetadataTab();
            return;
        }
        String projectId = creator.parentObject().citeableId();
        new SubjectMetadataDescribe(projectId, methodId).send(xe -> {
            creator.setPublicMetadataForCreate(xe == null ? null : xe.element("public"));
            creator.setPrivateMetadataForCreate(xe == null ? null : xe.element("private"));
            updatePublicMetadataTab();
            updatePrivateMetadataTab();
        });
    }

    @Override
    public void execute(ActionListener l) {
        new SubjectCreate(creator).send(id -> {
            l.executed(id != null);
        });
    }

}

package daris.web.client.gui.subject;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
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
import daris.web.client.model.project.DataUse;
import daris.web.client.model.subject.Subject;
import daris.web.client.model.subject.SubjectUpdater;
import daris.web.client.model.subject.messages.SubjectUpdate;

public class SubjectUpdateForm extends DObjectUpdateForm<Subject, SubjectUpdater> {
    private Integer _publicMetadataTabId = null;
    private Form _publicMetadataForm;

    private Integer _privateMetadataTabId = null;
    private Form _privateMetadataForm;

    public SubjectUpdateForm(Subject subject) {
        super(subject);
        updater.setMetadataSetter(null);
        updater.setPublicMetadataSetter(w -> {
            if (_publicMetadataForm != null) {
                w.push("public");
                _publicMetadataForm.save(w);
                w.pop();
            }
        });
        updater.setPrivateMetadataSetter(w -> {
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
        XmlElement me = updater.privateMetadataForEdit();
        if (me == null) {
            return;
        }
        _privateMetadataForm = XmlMetaForm.formFor(me, FormEditMode.UPDATE);
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
        XmlElement me = updater.publicMetadataForEdit();
        if (me == null) {
            return;
        }
        _publicMetadataForm = XmlMetaForm.formFor(me, FormEditMode.UPDATE);
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

        Field<DataUse> dataUseField = new Field<DataUse>(
                new FieldDefinition("Data Use", "Data_Use", new EnumerationType<DataUse>(DataUse.values()),
                        "Specifies the type of consent for the use of data for this project.", null, 1, 1));
        dataUseField.addListener(new FormItemListener<DataUse>() {

            @Override
            public void itemValueChanged(FormItem<DataUse> f) {
                updater.setDataUse(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<DataUse> f, Property property) {

            }
        });
        dataUseField.setInitialValue(updater.dataUse(), false);
        interfaceForm.add(dataUseField);

    }

    private void updateMetadataTabs() {
        updatePublicMetadataTab();
        updatePrivateMetadataTab();
    }

    @Override
    public void execute(ActionListener l) {
        new SubjectUpdate(updater).send(r -> {
            // TODO fade out message
        });
        l.executed(true);
    }

}

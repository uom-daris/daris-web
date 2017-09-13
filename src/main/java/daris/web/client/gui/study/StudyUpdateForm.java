package daris.web.client.gui.study;

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
import arc.mf.dtype.BooleanType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.model.study.Study;
import daris.web.client.model.study.StudyUpdater;
import daris.web.client.model.study.messages.StudyUpdate;

public class StudyUpdateForm extends DObjectUpdateForm<Study, StudyUpdater> {

    private Integer _metadataTabId = null;
    private Form _metadataForm;

    private Integer _methodMetadataTabId = null;
    private Form _methodMetadataForm;

    public StudyUpdateForm(Study obj) {
        super(obj);
        updater.setMetadataSetter(w -> {
            if (_metadataForm != null) {
                w.push("meta");
                _metadataForm.save(w);
                w.pop();
            }
        });
        updateMetadataTab();
        updater.setMethodMetadataSetter(w -> {
            if (_methodMetadataForm != null) {
                w.push("meta");
                _methodMetadataForm.save(w);
                w.pop();
            }
        });
        updateMethodMetadataTab();
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
        _metadataForm.render();
        addMustBeValid(_metadataForm);
        _metadataTabId = tabs.addTab("Metadata", null, new ScrollPanel(_metadataForm, ScrollPolicy.AUTO));

    }

    private void updateMethodMetadataTab() {
        if (_methodMetadataForm != null) {
            removeMustBeValid(_methodMetadataForm);
            _methodMetadataForm = null;
        }
        if (_methodMetadataTabId != null) {
            tabs.removeTabById(_methodMetadataTabId);
            _methodMetadataTabId = null;
        }
        XmlElement me = updater.methodMetadataForEdit();
        if (me == null) {
            return;
        }
        _methodMetadataForm = XmlMetaForm.formFor(me, FormEditMode.UPDATE);
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
        new StudyUpdate(updater).send(r -> {
            // TODO fade out message
        });
        l.executed(true);
    }

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);

        Field<Boolean> processedField = new Field<Boolean>(new FieldDefinition("Processed",
                BooleanType.DEFAULT_TRUE_FALSE, "Is the dataset processed?", null, 0, 1));
        processedField.setInitialValue(updater.processed(), false);
        processedField.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                updater.setProcessed(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        interfaceForm.add(processedField);
    }

}

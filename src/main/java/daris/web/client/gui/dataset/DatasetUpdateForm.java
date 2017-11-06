package daris.web.client.gui.dataset;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.xml.XmlElement;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectUpdateForm;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DatasetUpdater;
import daris.web.client.model.dataset.DerivedDataset;
import daris.web.client.model.dataset.PrimaryDataset;
import daris.web.client.model.object.TypeEnum;
import daris.web.client.util.StringUtils;

public abstract class DatasetUpdateForm<T extends Dataset> extends DObjectUpdateForm<T> {

    private FileForm _fileForm;

    private Field<String> _filenameField;
    private Field<String> _typeField;
    private Field<String> _ctypeField;

    private Integer _metadataTabId = null;
    private Form _metadataForm;

    protected DatasetUpdateForm(T o) {
        super(o);
        updater.setMetadataSetter(w -> {
            if (_metadataForm != null) {
                w.push("meta");
                _metadataForm.save(w);
                w.pop();
            }
        });
        updateMetadataTab();
    }

    private DatasetUpdater<?> updater() {
        return (DatasetUpdater<?>) updater;
    }

    protected void addToContainer(VerticalPanel container) {
        _fileForm = new FileForm(false);
        _fileForm.setEmptyMessage(
                "Drag and drop files here to replace existing content of the dataset. Leaving it empty to keep the content unchanged.");
        container.add(_fileForm.widget());
        addMustBeValid(_fileForm);
        _fileForm.addChangeListener(() -> {
            if (_fileForm.valid().valid()) {
                updater().setFiles(_fileForm.files());
                // TODO
                int nbFiles = updater().numberOfFiles();
                if (nbFiles == 1) {

                } else if (nbFiles > 1) {

                }
            }
        });
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

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);

        _filenameField = new Field<String>(new FieldDefinition("File Name", "File_Name",
                StringUtils.createStringTypeForFileName(), "Original file name if known.", null, 0, 1));
        _filenameField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _filenameField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                updater().setFilename(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _filenameField.setInitialValue(updater().filename(), false);
        interfaceForm.add(_filenameField);

        _typeField = new Field<String>(new FieldDefinition("Type", "Type", new EnumerationType<String>(new TypeEnum()),
                "MIME type of the object.", null, 0, 1));
        _typeField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _typeField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                updater().setType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _typeField.setInitialValue(updater().type(), false);
        interfaceForm.add(_typeField);

        _ctypeField = new Field<String>(new FieldDefinition("Content Type", "Content_Type",
                new EnumerationType<String>(new TypeEnum()), "MIME type of the content.", null, 0, 1));
        _ctypeField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _ctypeField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                updater().setContentType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _ctypeField.setInitialValue(updater().contentType(), false);
        interfaceForm.add(_ctypeField);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Dataset> DatasetUpdateForm<T> create(T obj) {
        if (obj instanceof DerivedDataset) {
            return (DatasetUpdateForm<T>) new DerivedDatasetUpdateForm((DerivedDataset) obj);
        } else if (obj instanceof PrimaryDataset) {
            return (DatasetUpdateForm<T>) new PrimaryDatasetUpdateForm((PrimaryDataset) obj);
        } else {
            throw new AssertionError("Unkown dataset type: " + obj.getClass().getCanonicalName());
        }
    }

}

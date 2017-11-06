package daris.web.client.gui.dataset;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.dataset.DatasetCreator;
import daris.web.client.model.object.TypeEnum;

public abstract class DatasetCreateForm<T extends DatasetCreator> extends DObjectCreateForm<T> {

    private FileForm _fileForm;
// TODO filename Field
    private Field<String> _typeField;
    private Field<String> _ctypeField;

    protected DatasetCreateForm(T dc) {
        super(dc);
    }

    protected void addToContainer(VerticalPanel container) {
        _fileForm = new FileForm(true);
        container.add(_fileForm.widget());
        addMustBeValid(_fileForm);
        _fileForm.addChangeListener(() -> {
            if (_fileForm.valid().valid()) {
                creator.setFiles(_fileForm.files());
                // TODO
            }
        });
    }

    protected void addToInterfaceForm(Form interfaceForm) {

        Field<String> pid = new Field<String>(new FieldDefinition("Parent Study ID", "pid", ConstantType.DEFAULT,
                "Identifier of the parent study.", null, 1, 1));
        pid.setValue(creator.parentObject().citeableId(), false);
        interfaceForm.add(pid);

        super.addToInterfaceForm(interfaceForm);

        _typeField = new Field<String>(new FieldDefinition("Type", "Type", new EnumerationType<String>(new TypeEnum()),
                "MIME type of the object.", null, 0, 1));
        _typeField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _typeField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _typeField.setInitialValue(this.creator.type(), false);
        interfaceForm.add(_typeField);

        _ctypeField = new Field<String>(new FieldDefinition("Content Type", "Content_Type",
                new EnumerationType<String>(new TypeEnum()), "MIME type of the content.", null, 0, 1));
        _ctypeField.setRenderOptions(new FieldRenderOptions().setWidth(350));
        _ctypeField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setContentType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _ctypeField.setInitialValue(this.creator.contentType(), false);
        interfaceForm.add(_ctypeField);
    }

}

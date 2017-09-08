package daris.web.client.gui.dataset.action;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.dataset.FileForm;
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.dataset.DatasetCreator;

public abstract class DatasetCreateForm<T extends DatasetCreator> extends DObjectCreateForm<T> {

    private FileForm _fileForm;

    protected DatasetCreateForm(T dc) {
        super(dc);
    }

    protected void addToContainer(VerticalPanel container) {
        _fileForm = new FileForm();
        container.add(_fileForm.widget());
        addMustBeValid(_fileForm);
        _fileForm.addChangeListener(() -> {
            if (_fileForm.valid().valid()) {
                creator.setFiles(_fileForm.files());
            }
        });
    }

    protected void addToInterfaceForm(Form interfaceForm) {

        Field<String> pid = new Field<String>(
                new FieldDefinition("Parent Study", "pid", ConstantType.DEFAULT, null, null, 1, 1));
        pid.setValue(creator.parentObject().citeableId(), false);
        interfaceForm.add(pid);

        super.addToInterfaceForm(interfaceForm);

        Field<String> type = new Field<String>(
                new FieldDefinition("Type", "type", new EnumerationType<String>(), null, null, 0, 1));
        type.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        type.setInitialValue(this.creator.type(), false);
        interfaceForm.add(type);

        Field<String> ctype = new Field<String>(
                new FieldDefinition("Content Type", "ctype", new EnumerationType<String>(), null, null, 0, 1));
        ctype.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setContentType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        ctype.setInitialValue(this.creator.contentType(), false);
        interfaceForm.add(ctype);
    }

}

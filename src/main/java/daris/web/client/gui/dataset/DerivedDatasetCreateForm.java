package daris.web.client.gui.dataset;

import java.util.Map;
import java.util.Set;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.mf.client.util.ActionListener;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import daris.web.client.gui.object.upload.FileUploadTaskManager;
import daris.web.client.model.dataset.DerivedDatasetCreator;
import daris.web.client.model.dataset.messages.DerivedDatasetCreateTask;

public class DerivedDatasetCreateForm extends DatasetCreateForm<DerivedDatasetCreator> {

    public DerivedDatasetCreateForm(DerivedDatasetCreator dc) {
        super(dc);
    }

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);
        if (creator.hasInputs()) {
            Map<String, String> inputs = creator.inputs();
            Set<String> cids = inputs.keySet();
            for (String cid : cids) {
                String vid = inputs.get(cid);
                Field<String> inputField = new Field<String>(new FieldDefinition("Input", "Input", ConstantType.DEFAULT,
                        "Identifier of the input object.", null, 0, 1));
                inputField.setValue(cid + "(vid=" + vid + ")", false);
                interfaceForm.add(inputField);
            }
        }
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

    @Override
    public void execute(ActionListener l) {
        l.executed(true);
        new DerivedDatasetCreateTask(creator).execute(r -> {
            if (r != null) {
                System.out.println("created " + r.referentType() + " " + r.citeableId());
                // TODO display a message box.
            }
        }, FileUploadTaskManager.get());
        FileUploadTaskManager.get().show(((BaseWidget) gui()).window());

    }
}

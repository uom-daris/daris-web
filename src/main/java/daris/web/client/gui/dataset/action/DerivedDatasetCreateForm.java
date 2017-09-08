package daris.web.client.gui.dataset.action;

import java.util.Map;
import java.util.Set;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.gwt.widget.BaseWidget;
import arc.mf.client.util.ActionListener;
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
                Field<String> inputField = new Field<String>(
                        new FieldDefinition("Input", "input", ConstantType.DEFAULT, null, null, 0, 1));
                inputField.setValue(cid + "(vid=" + vid + ")", false);
                interfaceForm.add(inputField);
            }
        }
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

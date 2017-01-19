package daris.web.client.gui.project;

import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.object.DObjectViewerGUI;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.Project;

public class ProjectViewerGUI extends DObjectViewerGUI<Project> {

    public ProjectViewerGUI(Project o) {
        super(o);
    }

    @Override
    protected void appendToInterfaceForm(Form form) {
        Project project = object();
        List<MethodRef> methods = project.methods();
        if (methods != null) {
            for (MethodRef m : methods) {
                Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "method",
                        ConstantType.DEFAULT, null, null, 0, Integer.MAX_VALUE));
                methodField.setValue(m, false);
                form.add(methodField);
            }
        }

        DataUse dataUse = project.dataUse();
        if (dataUse != null) {
            addDataUseField(dataUse, form);
        }
    }

    public static void addDataUseField(DataUse dataUse, Form form) {
        Field<DataUse> dataUseField = new Field<DataUse>(
                new FieldDefinition("Data Use", "data-use", new EnumerationType<DataUse>(DataUse.values()),
                        "Specifies the type of consent for the use of data for this project: "
                                + "<br> 1) 'specific' means use the data only for the original specific intent, "
                                + "<br> 2) 'extended' means use the data for related projects and "
                                + "<br> 3) 'unspecified' means use the data for any research",
                        null, 1, 1));
        dataUseField.setValue(dataUse);
        form.add(dataUseField);
    }

}

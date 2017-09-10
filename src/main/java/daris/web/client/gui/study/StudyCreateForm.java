package daris.web.client.gui.study;

import arc.gui.form.Field;
import arc.gui.form.Form;
import arc.mf.client.util.ActionListener;
import daris.web.client.gui.object.DObjectCreateForm;
import daris.web.client.model.exmethod.ExMethodStudyStepRef;
import daris.web.client.model.study.StudyCreator;

public class StudyCreateForm extends DObjectCreateForm<StudyCreator> {

    private Field<String> _studyTypeField;
    private Field<ExMethodStudyStepRef> _stepField;

    protected StudyCreateForm(StudyCreator creator) {
        super(creator);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute(ActionListener l) {
        // TODO Auto-generated method stub

    }

    protected void addToInterfaceForm(Form interfaceForm) {
        super.addToInterfaceForm(interfaceForm);
        
        
    }

}

package daris.web.client.gui.object;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import daris.web.client.gui.dataset.DatasetEditor;
import daris.web.client.gui.exmethod.ExMethodEditor;
import daris.web.client.gui.project.ProjectEditorGUI;
import daris.web.client.gui.study.StudyEditor;
import daris.web.client.gui.subject.SubjectEditor;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.object.DObject;
import daris.web.client.model.project.Project;
import daris.web.client.model.study.Study;
import daris.web.client.model.subject.Subject;

public class DObjectEditorGUI<T extends DObject> extends ValidatedInterfaceComponent {

    private T _o;

    protected DObjectEditorGUI(T o) {
        _o = o;
    }

    public T object() {
        return _o;
    }

    @Override
    public Widget gui() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static DObjectEditorGUI create(DObject object) {
        switch (object.type()) {
        case PROJECT:
            return new ProjectEditorGUI((Project) object);
        case SUBJECT:
            return new SubjectEditor((Subject) object);
        case EX_METHOD:
            return new ExMethodEditor((ExMethod) object);
        case STUDY:
            return new StudyEditor((Study) object);
        case DATASET:
            return new DatasetEditor((Dataset) object);
        default:
            throw new AssertionError("Unknown object type: " + object.type());
        }
    }

}

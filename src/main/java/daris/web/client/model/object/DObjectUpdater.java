package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DatasetUpdater;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.exmethod.ExMethodUpdater;
import daris.web.client.model.project.Project;
import daris.web.client.model.project.ProjectUpdater;
import daris.web.client.model.study.Study;
import daris.web.client.model.study.StudyUpdater;
import daris.web.client.model.subject.Subject;
import daris.web.client.model.subject.SubjectUpdater;

public abstract class DObjectUpdater<T extends DObject> extends DObjectBuilder {

    private T _obj;
    private XmlElement _metadataForEdit;

    protected DObjectUpdater(T obj) {
        _obj = obj;
        setName(_obj.name());
        setDescription(_obj.description());
        _metadataForEdit = obj.metadataForEdit();
    }

    public T object() {
        return _obj;
    }

    public XmlElement metadataForEdit() {
        return _metadataForEdit;
    }

    public abstract String serviceName();

    public abstract void serviceArgs(XmlWriter w);

    @SuppressWarnings("unchecked")
    public static <T extends DObject> DObjectUpdater<T> create(T obj) {
        switch (obj.objectType()) {
        case PROJECT:
            return (DObjectUpdater<T>) new ProjectUpdater((Project) obj);
        case SUBJECT:
            return (DObjectUpdater<T>) new SubjectUpdater((Subject) obj);
        case EX_METHOD:
            return (DObjectUpdater<T>) new ExMethodUpdater((ExMethod) obj);
        case STUDY:
            return (DObjectUpdater<T>) new StudyUpdater((Study) obj);
        case DATASET:
            return (DObjectUpdater<T>) DatasetUpdater.create((Dataset) obj);
        default:
            throw new AssertionError("Unknown object type: " + obj.objectType());
        }
    }

}

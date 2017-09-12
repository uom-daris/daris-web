package daris.web.client.model.study;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.exmethod.ExMethodStudyStepRef;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;

public class StudyCreator extends DObjectCreator {
    private String _studyType;
    private ExMethodStudyStepRef _step;
    private Boolean _processed;
    private XmlElement _methodMetadataForCreate;

    public StudyCreator(DObjectRef po) {
        super(po);
    }

    public XmlElement methodMetadataForCreate() {
        return _methodMetadataForCreate;
    }

    public void setMethodMetadataForCreate(XmlElement methodMetadataForCreate) {
        _methodMetadataForCreate = methodMetadataForCreate;
    }

    public void setStudyType(String studyType) {
        _studyType = studyType;
    }

    public void setStep(ExMethodStudyStepRef step) {
        _step = step;
        if (_step != null) {
            if (_step.studyType() != null) {
                setStudyType(_step.studyType());
            }
        }
    }

    public void setProcessed(boolean processed) {
        _processed = processed;
    }

    @Override
    public String serviceName() {
        return "om.pssd.study.create";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        w.add("pid", parentObject().citeableId());
        if (allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", allowIncompleteMeta());
        }
        if (fillInIdNumber()) {
            w.add("fillin", true);
        }
        if (name() != null) {
            w.add("name", name());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (_processed != null) {
            w.add("processed", _processed);
        }
        if (_step != null) {
            w.add("step", _step.path());
        }
        if (_studyType != null) {
            w.add("type", _studyType);
        }
        if (this.metadataSetter != null) {
            this.metadataSetter.setMetadata(w);
        }
    }

    public String studyType() {
        return _studyType;
    }

    public ExMethodStudyStepRef step() {
        return _step;
    }

    public Boolean processed() {
        return _processed;
    }
}

package daris.web.client.model.study;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;

public class StudyCreator extends DObjectCreator {
    private String _studyType;
    private String _step;
    private Boolean _processed;

    protected StudyCreator(DObjectRef po) {
        super(po);
    }

    public void setStudyType(String studyType) {
        _studyType = studyType;
    }

    public void setStep(String step) {
        _step = step;
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
            w.add("step", _step);
        }
        if (_studyType != null) {
            w.add("type", _studyType);
        }
        if (this.metadataSetter != null) {
            this.metadataSetter.setMetadata(w);
        }
    }

}

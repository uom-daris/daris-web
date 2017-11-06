package daris.web.client.model.study;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

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
    private List<SimpleEntry<String, String>> _otherIds;

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

    public void setOtherIds(List<SimpleEntry<String, String>> otherIds) {
        _otherIds = otherIds;
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
        if (otherIds() != null) {
            List<SimpleEntry<String, String>> otherIds = otherIds();
            for (SimpleEntry<String, String> otherId : otherIds) {
                w.add("other-id", new String[] { "type", otherId.getKey() }, otherId.getValue());
            }
        }
        if (processed() != null) {
            w.add("processed", processed());
        }
        if (step() != null) {
            w.add("step", step().path());
        }
        if (studyType() != null) {
            w.add("type", studyType());
        }
        if (metadataSetter() != null) {
            metadataSetter().setMetadata(w);
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

    public List<SimpleEntry<String, String>> otherIds() {
        return _otherIds;
    }
}

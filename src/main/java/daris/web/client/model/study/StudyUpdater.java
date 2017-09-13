package daris.web.client.model.study;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectUpdater;
import daris.web.client.model.object.MetadataSetter;

public class StudyUpdater extends DObjectUpdater<Study> {

    private Boolean _processed;
    private XmlElement _methodMetadataForEdit;
    private MetadataSetter _methodMetadataSetter;

    public StudyUpdater(Study obj) {
        super(obj);
        _processed = obj.processed();
        _methodMetadataForEdit = obj.methodMetadataForEdit();
    }

    public XmlElement methodMetadataForEdit() {
        return _methodMetadataForEdit;
    }

    public void setMethodMetadataForEdit(XmlElement methodMetadataForEdit) {
        _methodMetadataForEdit = methodMetadataForEdit;
    }

    public MetadataSetter methodMetadataSetter() {
        return _methodMetadataSetter;
    }

    public void setMethodMetadataSetter(MetadataSetter metadataSetter) {
        _methodMetadataSetter = metadataSetter;
    }

    @Override
    public String serviceName() {
        return "om.pssd.study.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        w.add("id", object().citeableId());
        if (name() != null) {
            w.add("name", name());
        }
        if (allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", true);
        }
        if (allowInvalidMeta()) {
            w.add("allow-invalid-meta", true);
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (this.metadataSetter != null) {
            metadataSetter.setMetadata(w);
        }
        if (_methodMetadataSetter != null) {
            _methodMetadataSetter.setMetadata(w);
        }
        if (processed() != null) {
            w.add("processed", processed());
        }
    }

    public Boolean processed() {
        return _processed;
    }

    public void setProcessed(Boolean processed) {
        _processed = processed;
    }
}

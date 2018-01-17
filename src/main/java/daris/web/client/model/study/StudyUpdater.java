package daris.web.client.model.study;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectUpdater;
import daris.web.client.model.object.MetadataSetter;

public class StudyUpdater extends DObjectUpdater<Study> {

    private Boolean _processed;
    private XmlElement _methodMetadataForEdit;
    private MetadataSetter _methodMetadataSetter;
    private List<SimpleEntry<String, String>> _otherIds;

    public StudyUpdater(Study obj) {
        super(obj);
        _processed = obj.processed();
        _methodMetadataForEdit = obj.methodMetadataForEdit();
        _otherIds = obj.otherIds();
    }

    public XmlElement methodMetadataForEdit() {
        return _methodMetadataForEdit;
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
        if (metadataSetter() != null) {
            metadataSetter().setMetadata(w);
        }
        if (_methodMetadataSetter != null) {
            _methodMetadataSetter.setMetadata(w);
        }
        if (processed() != null) {
            w.add("processed", processed());
        }
        if (otherIds() != null) {
            List<SimpleEntry<String, String>> otherIds = otherIds();
            for (SimpleEntry<String, String> otherId : otherIds) {
                w.add("other-id", new String[] { "type", otherId.getKey() }, otherId.getValue());
            }
        }
    }

    public Boolean processed() {
        return _processed;
    }

    public void setProcessed(Boolean processed) {
        _processed = processed;
    }

    public List<SimpleEntry<String, String>> otherIds() {
        return _otherIds;
    }

    public void setOtherIds(List<SimpleEntry<String, String>> otherIds) {
        _otherIds = otherIds;
    }
}

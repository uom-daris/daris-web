package daris.web.client.model.subject;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.MetadataSetter;
import daris.web.client.model.project.DataUse;

public class SubjectCreator extends DObjectCreator {

    private DataUse _dataUse;
    private String _methodId;
    private MetadataSetter _privateMetadataSetter;
    private MetadataSetter _publicMetadataSetter;

    protected SubjectCreator(DObjectRef project) {
        super(project);
    }

    @Override
    public String serviceName() {
        return "om.pssd.subject.create";
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
        if (_methodId != null) {
            w.add("method", _methodId);
        }
        if (_dataUse != null) {
            w.add("data-use", _dataUse);
        }
        if (_privateMetadataSetter != null) {
            _privateMetadataSetter.setMetadata(w);
        }
        if (_publicMetadataSetter != null) {
            _publicMetadataSetter.setMetadata(w);
        }
    }

    public void setPrivateMetadataSetter(MetadataSetter metadataSetter) {
        _privateMetadataSetter = metadataSetter;
    }

    public void setPublicMetadataSetter(MetadataSetter metadataSetter) {
        _publicMetadataSetter = metadataSetter;
    }

}

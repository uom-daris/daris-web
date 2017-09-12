package daris.web.client.model.subject;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.MetadataSetter;
import daris.web.client.model.project.DataUse;

public class SubjectCreator extends DObjectCreator {

    private DataUse _dataUse;
    private String _methodId;
    private XmlElement _privateMetadataForCreate;
    private MetadataSetter _privateMetadataSetter;

    private XmlElement _publicMetadataForCreate;
    private MetadataSetter _publicMetadataSetter;

    public SubjectCreator(DObjectRef project) {
        super(project);
    }

    public void setDataUse(DataUse dataUse) {
        _dataUse = dataUse;
    }

    public void setMethod(String methodId) {
        _methodId = methodId;
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

    public XmlElement privateMetadataForCreate() {
        return _privateMetadataForCreate;
    }

    public XmlElement publicMetadataForCreate() {
        return _publicMetadataForCreate;
    }

    public void setPrivateMetadataForCreate(XmlElement privateMetadataForCreate) {
        _privateMetadataForCreate = privateMetadataForCreate;
    }

    public void setPublicMetadataForCreate(XmlElement publicMetadataForCreate) {
        _publicMetadataForCreate = publicMetadataForCreate;
    }

    public void setMethod(MethodRef method) {
        _methodId = method == null ? null : method.citeableId();
    }

    public String method() {
        return _methodId;
    }

}

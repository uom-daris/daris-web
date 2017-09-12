package daris.web.client.model.subject;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectUpdater;
import daris.web.client.model.object.MetadataSetter;
import daris.web.client.model.project.DataUse;

public class SubjectUpdater extends DObjectUpdater<Subject> {

    private DataUse _dataUse;

    private XmlElement _publicMetadataForEdit;
    private XmlElement _privateMetadataForEdit;

    private MetadataSetter _publicMetadataSetter;
    private MetadataSetter _privateMetadataSetter;

    public SubjectUpdater(Subject obj) {
        super(obj);
        _dataUse = obj.dataUse();
        _publicMetadataForEdit = obj.publicMetadataForEdit();
        _privateMetadataForEdit = obj.privateMetadataForEdit();

    }

    @Override
    public String serviceName() {
        return "om.pssd.subject.update";
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
        if (dataUse() != null) {
            w.add("data-use", dataUse());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (_publicMetadataSetter != null) {
            _publicMetadataSetter.setMetadata(w);
        }
        if (_privateMetadataSetter != null) {
            _privateMetadataSetter.setMetadata(w);
        }
    }

    public void setPublicMetadataSetter(MetadataSetter metadataSetter) {
        _publicMetadataSetter = metadataSetter;
    }

    public void setPrivateMetadataSetter(MetadataSetter metadataSetter) {
        _privateMetadataSetter = metadataSetter;
    }

    public void setPublicMetadataForEdit(XmlElement me) {
        _publicMetadataForEdit = me;
    }

    public void setPrivateMetadataForEdit(XmlElement me) {
        _privateMetadataForEdit = me;
    }

    public XmlElement privateMetadataForEdit() {
        return _privateMetadataForEdit;
    }

    public XmlElement publicMetadataForEdit() {
        return _publicMetadataForEdit;
    }

    public void setDataUse(DataUse dataUse) {
        _dataUse = dataUse;
    }

    public DataUse dataUse() {
        return _dataUse;
    }

}

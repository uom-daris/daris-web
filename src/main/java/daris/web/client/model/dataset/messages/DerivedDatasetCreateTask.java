package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.upload.FileSetUploadTask;

public class DerivedDatasetCreateTask extends FileSetUploadTask<DObjectRef> {

    private String _name;
    private String _description;

    public DerivedDatasetCreateTask(String name, String description) {
        _name = name;
        _description = description;
    }

    @Override
    protected String consumeServiceName() {
        return "om.pssd.dataset.derivation.create";
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        if (_name != null) {
            w.add("name", _name);
        }
        if (_description != null) {
            w.add("description", _description);
        }
    }

    @Override
    protected DObjectRef instantiate(XmlElement xe) {
        return new DObjectRef(xe.value("id"), 0);
    }

}
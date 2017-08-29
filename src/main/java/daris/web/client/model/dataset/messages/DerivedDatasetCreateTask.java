package daris.web.client.model.dataset.messages;

import java.util.List;

import arc.mf.client.file.LocalFile;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.upload.FileUploadTask;

public class DerivedDatasetCreateTask extends FileUploadTask<DObjectRef> {

    private String _name;
    private String _description;

    public DerivedDatasetCreateTask(List<LocalFile> files, String name, String description) {
        super(files);
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
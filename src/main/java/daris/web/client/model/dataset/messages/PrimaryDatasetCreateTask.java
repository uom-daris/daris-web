package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.PrimaryDatasetCreator;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.upload.FileUploadTask;

public class PrimaryDatasetCreateTask extends FileUploadTask<DObjectRef> {

    private PrimaryDatasetCreator _c;

    public PrimaryDatasetCreateTask(PrimaryDatasetCreator c) {
        super(c.files());
        _c = c;
        setArchiveType(_c.archiveType());
        setName("upload primary dataset");
    }

    @Override
    protected String consumeServiceName() {
        return _c.serviceName();
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        _c.serviceArgs(w);
    }

    @Override
    protected DObjectRef instantiate(XmlElement xe) {
        return new DObjectRef(xe.value("id"), 0);
    }

}

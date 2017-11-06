package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.PrimaryDatasetUpdater;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.imports.FileUploadTask;

public class PrimaryDatasetUpdateTask extends FileUploadTask<DObjectRef> {

    private PrimaryDatasetUpdater _du;

    public PrimaryDatasetUpdateTask(PrimaryDatasetUpdater du) {
        super(du.files());
        _du = du;
        setArchiveType(_du.archiveType());
        setName("update primary dataset");
    }

    @Override
    protected String consumeServiceName() {
        return _du.serviceName();
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        _du.serviceArgs(w);
    }

    @Override
    protected DObjectRef instantiate(XmlElement xe) {
        return new DObjectRef(xe.value("id"), 0);
    }

}

package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.DerivedDatasetUpdater;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.imports.FileUploadTask;

public class DerivedDatasetUpdateTask extends FileUploadTask<DObjectRef> {

    private DerivedDatasetUpdater _du;

    public DerivedDatasetUpdateTask(DerivedDatasetUpdater du) {
        super(du.files());
        _du = du;
        setName("update derived dataset");
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
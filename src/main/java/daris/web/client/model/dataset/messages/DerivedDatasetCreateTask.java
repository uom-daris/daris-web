package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.DerivedDatasetCreator;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.imports.FileUploadTask;

public class DerivedDatasetCreateTask extends FileUploadTask<DObjectRef> {

    private DerivedDatasetCreator _dc;

    public DerivedDatasetCreateTask(DerivedDatasetCreator dc) {
        super(dc.files());
        _dc = dc;
        setName("create derived dataset");
    }

    @Override
    protected String consumeServiceName() {
        return _dc.serviceName();
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        _dc.serviceArgs(w);
    }

    @Override
    protected DObjectRef instantiate(XmlElement xe) {
        return new DObjectRef(xe.value("id"), 0);
    }

}
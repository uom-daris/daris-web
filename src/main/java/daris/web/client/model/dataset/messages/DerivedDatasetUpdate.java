package daris.web.client.model.dataset.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.dataset.DerivedDatasetUpdater;

public class DerivedDatasetUpdate extends ObjectMessage<Null> {

    private DerivedDatasetUpdater _du;

    public DerivedDatasetUpdate(DerivedDatasetUpdater du) {
        _du = du;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        _du.serviceArgs(w);
    }

    @Override
    protected String messageServiceName() {
        return _du.serviceName();
    }

    @Override
    protected Null instantiate(XmlElement xe) throws Throwable {
        return new Null();
    }

    @Override
    protected String objectTypeName() {
        return _du.object().objectType().name();
    }

    @Override
    protected String idToString() {
        return _du.object().citeableId();
    }

}

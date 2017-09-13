package daris.web.client.model.exmethod.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.exmethod.ExMethodUpdater;
import daris.web.client.model.object.DObject;

public class ExMethodUpdate extends ObjectMessage<Void> {

    private ExMethodUpdater _updater;

    public ExMethodUpdate(ExMethodUpdater creator) {
        _updater = creator;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        _updater.serviceArgs(w);
    }

    @Override
    protected String messageServiceName() {
        return _updater.serviceName();
    }

    @Override
    protected Void instantiate(XmlElement xe) throws Throwable {
        return null;
    }

    @Override
    protected String objectTypeName() {
        return DObject.Type.EX_METHOD.toString();
    }

    @Override
    protected String idToString() {
        return _updater.object().citeableId();
    }

}

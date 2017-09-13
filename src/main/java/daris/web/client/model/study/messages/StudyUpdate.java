package daris.web.client.model.study.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.study.StudyUpdater;

public class StudyUpdate extends ObjectMessage<Void> {

    private StudyUpdater _updater;

    public StudyUpdate(StudyUpdater creator) {
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
        return DObject.Type.STUDY.toString();
    }

    @Override
    protected String idToString() {
        return _updater.object().citeableId();
    }

}

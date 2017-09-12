package daris.web.client.model.project.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.project.ProjectUpdater;

public class ProjectUpdate extends ObjectMessage<Void> {

    private ProjectUpdater _updater;

    public ProjectUpdate(ProjectUpdater creator) {
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
        return DObject.Type.PROJECT.toString();
    }

    @Override
    protected String idToString() {
        return _updater.object().citeableId();
    }

}

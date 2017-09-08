package daris.web.client.model.project.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.project.ProjectCreator;

public class ProjectCreate extends ObjectMessage<String> {

    private ProjectCreator _creator;

    public ProjectCreate(ProjectCreator creator) {
        _creator = creator;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        _creator.serviceArgs(w);
    }

    @Override
    protected String messageServiceName() {
        return _creator.serviceName();
    }

    @Override
    protected String instantiate(XmlElement xe) throws Throwable {
        return xe == null ? null : xe.value("id");
    }

    @Override
    protected String objectTypeName() {
        return DObject.Type.PROJECT.toString();
    }

    @Override
    protected String idToString() {
        return null;
    }

}

package daris.web.client.model.subject.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.subject.SubjectCreator;

public class SubjectCreate extends ObjectMessage<String> {

    private SubjectCreator _creator;

    public SubjectCreate(SubjectCreator creator) {
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
        return DObject.Type.SUBJECT.toString();
    }

    @Override
    protected String idToString() {
        return null;
    }

}

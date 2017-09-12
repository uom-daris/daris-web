package daris.web.client.model.study.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.study.StudyCreator;

public class StudyCreate extends ObjectMessage<String> {

    private StudyCreator _creator;

    public StudyCreate(StudyCreator creator) {
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
        return DObject.Type.STUDY.toString();
    }

    @Override
    protected String idToString() {
        return null;
    }

}

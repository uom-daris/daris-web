package daris.web.client.model.study.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;

public class StudyMetadataDescribe extends ObjectMessage<XmlElement> {

    private String _exmCID;
    private String _stepPath;

    public StudyMetadataDescribe(String exmCID, String stepPath) {
        _exmCID = exmCID;
        _stepPath = stepPath;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("pid", _exmCID);
        w.add("step", _stepPath);
    }

    @Override
    protected String messageServiceName() {
        return "om.pssd.study.metadata.describe";
    }

    @Override
    protected XmlElement instantiate(XmlElement xe) throws Throwable {
        return xe == null ? null : xe.element("method");
    }

    @Override
    protected String objectTypeName() {
        return null;
    }

    @Override
    protected String idToString() {
        return _exmCID + "_" + _stepPath;
    }

}

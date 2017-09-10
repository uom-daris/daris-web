package daris.web.client.model.study.messages;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;

public class StudyTypeList extends ObjectMessage<List<String>> {
    private String _exmCid;

    public StudyTypeList(String exmCid) {
        _exmCid = exmCid;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_exmCid != null) {
            w.add("id", _exmCid);
        }
    }

    @Override
    protected String messageServiceName() {
        if (_exmCid != null) {
            return "om.pssd.ex-method.study.type.list";
        } else {
            return "om.pssd.study.type.describe";
        }
    }

    @Override
    protected List<String> instantiate(XmlElement xe) throws Throwable {
        return xe.values("type/name");
    }

    @Override
    protected String objectTypeName() {
        return "study type";
    }

    @Override
    protected String idToString() {
        return _exmCid;
    }

}
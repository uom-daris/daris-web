package daris.web.client.model.study;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class Study extends DObject {

    private String _exMethodCid;
    private String _studyType;
    private String _stepPath;
    private XmlElement _methodMeta;
    private XmlElement _methodMetaForEdit;
    private Boolean _processed;
    private List<SimpleEntry<String, String>> _otherIds;

    public Study(XmlElement oe) {
        super(oe);
        _studyType = oe.value("type");
        // processed is optional
        String processed = oe.value("processed");
        if (processed != null) {
            _processed = Boolean.parseBoolean(processed);
        }
        _exMethodCid = oe.value("method/id");
        _stepPath = oe.value("method/step");
        XmlElement mme = oe.element("method/meta");
        if (mme != null) {
            if (mme.element("metadata") != null) {
                _methodMetaForEdit = mme;
            } else {
                _methodMeta = mme;
            }
        }

        List<XmlElement> oies = oe.elements("other-id");
        if (oies != null && !oies.isEmpty()) {
            _otherIds = new ArrayList<SimpleEntry<String, String>>(oies.size());
            for (XmlElement oie : oies) {
                String type = oie.value("@type");
                String oid = oie.value();
                SimpleEntry<String, String> entry = new SimpleEntry<String, String>(type, oid);
                _otherIds.add(entry);
            }
        }
    }

    @Override
    public Type objectType() {
        return DObject.Type.STUDY;
    }

    public String exMethodCid() {
        return _exMethodCid;
    }

    public String studyType() {
        return _studyType;
    }

    public String stepPath() {
        return _stepPath;
    }

    public XmlElement methodMetadata() {

        return _methodMeta;
    }

    public Boolean processed() {
        return _processed;
    }

    public XmlElement methodMetadataForEdit() {

        return _methodMetaForEdit;
    }

    public boolean hasMethodMetadata() {
        return (_methodMeta != null && _methodMeta.hasElements())
                || (_methodMetaForEdit != null && _methodMetaForEdit.hasElements());
    }

    public List<SimpleEntry<String, String>> otherIds() {
        return _otherIds;
    }

}

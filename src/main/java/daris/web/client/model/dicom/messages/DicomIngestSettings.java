package daris.web.client.model.dicom.messages;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.archive.ArchiveType;
import daris.web.client.model.object.DObjectRef;

public class DicomIngestSettings {

    public static final String ENGINE = "nig.dicom";
    public static final String TYPE_AAR = "application/arc-archive";

    private Boolean _anonymize;
    private String _engine;
    private Map<String, String> _args;
    private Boolean _wait;
    private String _type;

    public DicomIngestSettings(DObjectRef po) {
        this(po.citeableId());
    }

    public DicomIngestSettings(String cid) {
        _engine = ENGINE;
        _args = new LinkedHashMap<String, String>();
        setArg("nig.dicom.id.ignore-non-digits", "true");
        setArg("nig.dicom.subject.create", "true");
        if (cid != null) {
            setArg("nig.dicom.id.citable", cid);
        }
        setArg("nig.dicom.write.mf-dicom-patient", "true");
        _type = TYPE_AAR;
        _wait = false;
    }

    public Boolean anonymize() {
        return _anonymize;
    }

    public DicomIngestSettings setAnonymize(Boolean anonymize) {
        _anonymize = anonymize;
        return this;
    }

    public DicomIngestSettings setWait(Boolean wait) {
        _wait = wait;
        return this;
    }

    public DicomIngestSettings setArchiveType(ArchiveType type) {
        if (type != null) {
            _type = type.mimeType();
        }
        return this;
    }

    public DicomIngestSettings setArg(String name, String value) {
        _args.put(name, value);
        return this;
    }

    public DicomIngestSettings setCiteableId(String cid) {
        return setArg("nig.dicom.id.citable", cid);
    }

    public DicomIngestSettings setMetaSetService(String service) {
        return setArg("nig.dicom.subject.meta.set-service", service);
    }

    public Boolean encryptPatient() {
        if (_args.containsKey("nig.dicom.use.encrypted.patient")) {
            return Boolean.parseBoolean(_args.get("nig.dicom.use.encrypted.patient"));
        } else {
            return null;
        }
    }

    public DicomIngestSettings setEncryptPatient(boolean encryptPatient) {
        return setArg("nig.dicom.use.encrypted.patient", Boolean.toString(encryptPatient));
    }

    public void saveServiceArgs(XmlWriter w) {
        w.add("engine", _engine);
        if (_anonymize != null) {
            w.add("anonymize", _anonymize);
        }
        if (_wait != null) {
            w.add("wait", _wait);
        }
        if (_type != null) {
            w.add("type", _type);
        }
        Set<String> argNames = _args.keySet();
        for (String argName : argNames) {
            w.add("arg", new String[] { "name", argName }, _args.get(argName));
        }
    }

}

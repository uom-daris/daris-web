package daris.web.client.model.exmethod;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class ExMethodStudyStepRef extends ObjectRef<ExMethodStudyStep> {

    private String _exmCid;
    private String _proute;
    private String _path;
    private String _name;
    private String _studyType;
    private String _dicomModality;

    public ExMethodStudyStepRef(String exMethodCid, String path, String name, String studyType, String dicomModality) {
        _exmCid = exMethodCid;
        _path = path;
        _name = name;
        _studyType = studyType;
        _dicomModality = dicomModality;
    }

    public ExMethodStudyStepRef(String exMethodCid, String path) {
        this(exMethodCid, path, null, null, null);
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("id", _exmCid);
        w.add("step", _path);
    }

    @Override
    protected String resolveServiceName() {
        return "om.pssd.ex-method.step.describe";
    }

    @Override
    protected ExMethodStudyStep instantiate(XmlElement xe) throws Throwable {
        if (xe != null) {
            XmlElement se = xe.element("step");
            if (se != null) {
                _name = se.value("name");
                _studyType = se.value("study/type");
                _dicomModality = se.value("study/dicom/modality");
                String state = se.value("status/state");
                String notes = se.value("status/notes");
                List<XmlElement> meta = se.elements("study/metadata");
                return new ExMethodStudyStep(_exmCid, _proute, _path, _name,
                        state == null ? null : State.fromString(state), notes, _studyType, meta, true);
            }
        }
        return null;
    }

    @Override
    public String referentTypeName() {
        return "step";
    }

    @Override
    public String idToString() {
        return _exmCid + "_" + _path;
    }

    public String name() {
        return _name;
    }

    public String path() {
        return _path;
    }

    public String exMethodCID() {
        return _exmCid;
    }

    public String studyType() {
        return _studyType;
    }

    public String dicoModality() {
        return _dicomModality;
    }

    @Override
    public String toString() {
        return _path + ": " + _name + " (type: " + _studyType + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof ExMethodStudyStepRef)) {
            ExMethodStudyStepRef so = (ExMethodStudyStepRef) o;
            return _exmCid.equals(so.exMethodCID()) && _path.equals(so.path());
        }
        return false;
    }

}

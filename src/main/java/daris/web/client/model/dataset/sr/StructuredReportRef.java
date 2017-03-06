package daris.web.client.model.dataset.sr;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;
import daris.web.client.model.dataset.DicomDataset;
import daris.web.client.model.object.DObjectRef;

public class StructuredReportRef extends ObjectRef<StructuredReport> {

    public static final String SERVICE_NAME = "daris.dicom.sr.get";

    private String _cid;

    public StructuredReportRef(String cid) {
        _cid = cid;
    }

    public StructuredReportRef(DObjectRef o) {
        this(o.citeableId());
    }

    public StructuredReportRef(DicomDataset o) {
        this(o.citeableId());
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("cid", _cid);
    }

    @Override
    protected String resolveServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected StructuredReport instantiate(XmlElement xe) throws Throwable {
        if (xe != null) {
            XmlElement se = xe.element("dicom-structured-report");
            if (se != null) {
                return new StructuredReport(se);
            }
        }
        return null;
    }

    @Override
    public String referentTypeName() {
        return "DICOM Structured Report";
    }

    @Override
    public String idToString() {
        return _cid;
    }

    public String cid() {
        return _cid;
    }

}

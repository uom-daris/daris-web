package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class DObjectSummaryRef extends ObjectRef<DObjectSummary> {

    private DObjectRef _o;

    private Boolean _contentExists;
    private Integer _nbDatasets;
    private Integer _nbDicomDatasets;

    public DObjectSummaryRef(DObjectRef o) {
        super(DObjectSummary.summaryOf(o));
        _o = o;
        _contentExists = referent().contentExists();
        _nbDatasets = referent().numberOfDatasets();
        _nbDicomDatasets = referent().numberOfDicomDatasets();
        if (_contentExists == null || _nbDatasets == null || _nbDicomDatasets == null) {
            super.reset();
        }
    }

    @Override
    public void reset() {
        _contentExists = null;
        _nbDatasets = null;
        _nbDicomDatasets = null;
        super.reset();
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        if (referent() == null || referent().contentExists() == null) {
            /*
             * check if the object has content
             */
            w.push("service", new String[] { "name", "daris.asset.content.exists" });
            w.add("cid", _o.citeableId());
            w.pop();
        }

        if (referent() == null || referent().numberOfDatasets() == null) {
            /*
             * check if the object contain any dataset
             */
            w.push("service", new String[] { "name", "daris.collection.dataset.count" });
            w.add("cid", _o.citeableId());
            w.pop();
        }

        if (referent() == null || referent().numberOfDicomDatasets() == null) {
            /*
             * check if the object contains any dicom dataset
             */
            w.push("service", new String[] { "name", "daris.collection.dicom.dataset.count" });
            w.add("cid", _o.citeableId());
            w.pop();
        }
    }

    @Override
    protected String resolveServiceName() {
        return "service.execute";
    }

    @Override
    protected DObjectSummary instantiate(XmlElement xe) throws Throwable {

        if (_contentExists == null) {
            _contentExists = xe.booleanValue(
                    "reply[@service='daris.asset.content.exists']/response/exists[@cid='" + _o.citeableId() + "']");
        }

        if (_nbDatasets == null) {
            _nbDatasets = xe.intValue("reply[@service='daris.collection.dataset.count']/response/value");
        }

        if (_nbDicomDatasets == null) {
            _nbDicomDatasets = xe.intValue("reply[@service='daris.collection.dicom.dataset.count']/response/value");
        }
        return new DObjectSummary(_contentExists, _nbDatasets, _nbDicomDatasets);
    }

    @Override
    public String referentTypeName() {
        return "object menu pre-conditions";
    }

    @Override
    public String idToString() {
        return _o.citeableId();
    }

}

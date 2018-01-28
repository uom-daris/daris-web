package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class CollectionSummaryRef extends ObjectRef<CollectionSummary> {

    private String _cid;
    private String _where;

    public CollectionSummaryRef(DObjectRef o) {
        this(null, o.citeableId());
    }

    public CollectionSummaryRef(String where) {
        this(where, null);
    }

    protected CollectionSummaryRef(String where, String cid) {
        _where = where;
        _cid = cid;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        if (_where != null) {
            w.add("where", _where);
        }
        if (_cid != null) {
            w.add("cid", _cid);
        }
        w.add("async", true);
    }

    @Override
    protected String resolveServiceName() {
        return "daris.collection.summary.get";
    }

    @Override
    protected CollectionSummary instantiate(XmlElement xe) throws Throwable {
        return new CollectionSummary(xe);
    }

    @Override
    public String referentTypeName() {
        return "collection summary";
    }

    @Override
    public String idToString() {
        return _cid == null ? null : _cid;
    }

}

package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class CollectionSummaryRef extends ObjectRef<CollectionSummary> {

    private DObjectRef _o;

    public CollectionSummaryRef(DObjectRef o) {
        _o = o;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("cid", _o.citeableId());
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
        return _o.citeableId();
    }

}

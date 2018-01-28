package daris.web.client.model.query;

import java.util.Set;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObjectRef;

public class DObjectQueryResultCollectionRef extends QueryResultCollectionRef<DObjectRef> {

    public DObjectQueryResultCollectionRef(String... wheres) {
        super(wheres);
        addXPath("cid", "cid");
        addXPath("daris:pssd-object/name", "name");
        addXPath("daris:pssd-object/type", "type");
    }

    @Override
    protected DObjectRef instantiate(XmlElement ae) throws Throwable {
        DObjectRef o = new DObjectRef(ae.value("cid"), ae.value("@id"), null, ae.value("name"), -1, false);
        if (hasXPaths()) {
            Set<XPath> xpaths = xpaths();
            for (XPath xpath : xpaths) {
                if (action() == Action.GET_VALUES) {
                    o.setXValue(xpath, ae.values(xpath.ename()));
                } else {
                    o.setXValue(xpath, ae.value(xpath.ename()));
                }
            }
        }
        return o;
    }

}

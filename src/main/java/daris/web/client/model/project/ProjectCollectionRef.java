package daris.web.client.model.project;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.OrderedCollectionRef;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class ProjectCollectionRef extends OrderedCollectionRef<DObjectRef> {

    public static final int DEFAULT_PAGE_SIZE = 100;

    private int _pageSize;

    ProjectCollectionRef() {
        _pageSize = DEFAULT_PAGE_SIZE;
        setCountMembers(true);
    }

    public int defaultPagingSize() {
        return _pageSize;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {
        w.add("where", "model='om.pssd.project'");
        w.add("xpath", new String[] { "ename", "cid" }, "cid");
        w.add("xpath", new String[] { "ename", "name" }, "daris:pssd-object/name");
        w.add("action", "get-value");
        w.add("idx", start + 1);
        w.add("size", size);
        w.push("sort");
        w.add("key", "cid");
        w.pop();
    }

    @Override
    protected String resolveServiceName() {
        return "asset.query";
    }

    @Override
    protected DObjectRef instantiate(XmlElement ae) throws Throwable {
        return new DObjectRef(ae.value("cid"), ae.value("@id"), null, ae.value("name"), -1, false);
    }

    @Override
    protected String referentTypeName() {
        return DObject.Type.PROJECT.toString();
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "asset" };
    }

}

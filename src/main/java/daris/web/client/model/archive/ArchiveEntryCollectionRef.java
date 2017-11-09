package daris.web.client.model.archive;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.OrderedCollectionRef;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.object.DObject;

public class ArchiveEntryCollectionRef extends OrderedCollectionRef<ArchiveEntry> {
    public static int PAGE_SIZE_INFINITY = -1;
    public static int PAGE_SIZE_DEFAULT = 50;

    private DObject _obj;

    private int _pageSize = PAGE_SIZE_DEFAULT;

    public ArchiveEntryCollectionRef(DObject obj) {
        _obj = obj;
        setCountMembers(true);
    }

    public String assetId() {
        return _obj.assetId();
    }

    public String assetMimeType() {
        return (_obj instanceof Dataset) ? ((Dataset) _obj).mimeType() : null;
    }

    public String citeableId() {
        return _obj.citeableId();
    }
    
    public DObject object(){
        return _obj;
    }

    @Override
    public int defaultPagingSize() {
        return _pageSize;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, long start, int size, boolean count) {
        if (assetId() != null) {
            w.add("id", assetId());
        } else {
            w.add("cid", citeableId());
        }
        w.add("idx", start + 1);
        w.add("size", size);
    }

    @Override
    protected String resolveServiceName() {
        return "daris.archive.content.list";
    }

    @Override
    protected ArchiveEntry instantiate(XmlElement ee) throws Throwable {
        if (ee != null) {
            return new ArchiveEntry(ee);
        }
        return null;
    }

    @Override
    protected String referentTypeName() {
        return "archive entry";
    }

    @Override
    protected String[] objectElementNames() {
        return new String[] { "entry" };
    }

    @Override
    public boolean supportsPaging() {
        return true;
    }
}

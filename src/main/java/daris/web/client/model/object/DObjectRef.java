package daris.web.client.model.object;

import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;
import arc.mf.object.lock.LockToken;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject.Type;

public class DObjectRef extends ObjectRef<DObject> implements Comparable<DObjectRef> {

    private String _citeableId;
    private String _assetId;
    private String _proute;
    private String _name;
    private int _numberOfChildren = -1;
    private boolean _foredit;
    private boolean _testleaf;

    private boolean _resolved = false;

    public DObjectRef(String citeableId, int numberOfChildren) {
        this(citeableId, null, null, null, numberOfChildren, false, true);
    }

    public DObjectRef(String citeableId, String assetId, String proute, String name, int numberOfChildren,
            boolean foredit, boolean testleaf) {
        _citeableId = citeableId;
        _assetId = assetId;
        _proute = proute;
        _name = name;
        _numberOfChildren = numberOfChildren;
        _foredit = foredit;
        _testleaf = testleaf;
    }

    public DObjectRef(XmlElement oe) {
        _citeableId = oe.value("@cid");
        _assetId = oe.value("@id");
        _proute = oe.value("@proute");
        _name = oe.value("@name");
        try {
            _numberOfChildren = oe.intValue("@nbc");
        } catch (Throwable e) {
        }
    }

    public DObjectRef(DObject o) {
        super(o);
        _citeableId = o.citeableId();
        _assetId = o.assetId();
        _proute = o.proute();
        _name = o.name();
        _numberOfChildren = o.numberOfChildren();
        _foredit = o.metadataForEdit() != null;
        _testleaf = o.numberOfChildren() >= 0;
    }

    @Override
    public boolean resolved() {

        if (_resolved == false) {
            return false;
        }
        return super.resolved();
    }

    public String citeableId() {
        return _citeableId;
    }

    public String name() {
        return _name;
    }

    public String assetId() {
        return _assetId;
    }

    public String proute() {
        return _proute;
    }

    public int numberOfChildren() {
        return _numberOfChildren;
    }

    public void setTestleaf(boolean testleaf) {
        _testleaf = testleaf;
    }

    public void setForEdit(boolean forEdit) {
        _foredit = forEdit;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {

        resolveServiceArgs(w, false);
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w, boolean lock) {

        if (_proute != null) {
            w.add("id", new String[] { "proute", _proute }, _citeableId);
        } else {
            w.add("id", _citeableId);
        }
        w.add("foredit", _foredit);
        w.add("isleaf", _testleaf);

        if (lock) {
            // Can only lock the latest version.
            w.add("lock", new String[] { "type", "transient", "timeout", "60", "descend", "false" }, true);
        }
    }

    @Override
    public boolean supportLocking() {

        return true;
    }

    @Override
    protected String resolveServiceName() {
        return "om.pssd.object.describe";
    }

    @Override
    protected DObject instantiate(XmlElement xe) throws Throwable {

        DObject o = DObject.create(xe.element("object"));
        if (o != null) {
            _assetId = o.assetId();
            _proute = o.proute();
            _name = o.name();
            _numberOfChildren = o.numberOfChildren();
            _resolved = true;
        }
        return o;
    }

    @Override
    protected LockToken instantiateLockToken(XmlElement xe) throws Throwable {

        // object lock does not have an id -- use the object citeable id.
        String id = xe.value("object/id");

        XmlElement le = xe.element("object/lock");
        if (le == null) {
            return null;
        }

        return new DObjectLockToken(id);
    }

    public DObject.Type referentType() {
        if (referent() != null) {
            return referent().type();
        } else {
            return CiteableIdUtils.type(_citeableId);
        }
    }

    @Override
    public String referentTypeName() {
        return referentType().toString();
    }

    @Override
    public String idToString() {
        return _citeableId;
    }

    @Override
    public boolean equals(Object o) {

        if (o != null) {
            if (o instanceof DObjectRef) {
                DObjectRef r = (DObjectRef) o;
                return ObjectUtil.equals(_citeableId, r.citeableId()) && ObjectUtil.equals(_proute, r.proute());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {

        StringBuilder sb = new StringBuilder();
        sb.append(_citeableId);
        if (_proute != null) {
            sb.append("_");
            sb.append(_proute);
        }
        return sb.toString().hashCode();
    }

    @Override
    public int compareTo(DObjectRef o) {

        if (o == null) {
            return 1;
        }
        if (_citeableId != null) {
            if (o.citeableId() != null) {
                return CiteableIdUtils.compare(_citeableId, o.citeableId());
            } else {
                return -1;
            }
        } else {
            if (o.citeableId() != null) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return _citeableId;
    }

    public boolean isProject() {
        if (referent() != null) {
            return referent().type() == Type.PROJECT;
        } else {
            return CiteableIdUtils.isProject(citeableId());
        }
    }

    public boolean isSubject() {
        if (referent() != null) {
            return referent().type() == Type.SUBJECT;
        } else {
            return CiteableIdUtils.isSubject(citeableId());
        }
    }

    public boolean isExMethod() {
        if (referent() != null) {
            return referent().type() == Type.EX_METHOD;
        } else {
            return CiteableIdUtils.isExMethod(citeableId());
        }
    }

    public boolean isStudy() {
        if (referent() != null) {
            return referent().type() == Type.STUDY;
        } else {
            return CiteableIdUtils.isStudy(citeableId());
        }
    }

    public boolean isDataset() {
        if (referent() != null) {
            return referent().type() == Type.DATASET;
        } else {
            return CiteableIdUtils.isDataset(citeableId());
        }
    }

    public DObject.Type childType() {
        DObject.Type type = referentType();
        if (type != null && DObject.Type.DATASET != type) {
            switch (type) {
            case PROJECT:
                return DObject.Type.SUBJECT;
            case SUBJECT:
                return DObject.Type.EX_METHOD;
            case EX_METHOD:
                return DObject.Type.STUDY;
            case STUDY:
                return DObject.Type.DATASET;
            default:
                break;
            }
        }
        return null;
    }

    public String childTypeName() {
        DObject.Type childType = childType();
        if (childType != null) {
            return childType.toString();
        }
        return null;
    }

}

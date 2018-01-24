package daris.web.client.model.object;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;
import arc.mf.object.lock.LockToken;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject.Type;
import daris.web.client.model.query.HasXValue;
import daris.web.client.model.query.IsQueryResult;
import daris.web.client.model.query.XPath;
import daris.web.client.model.query.XValue;

public class DObjectRef extends ObjectRef<DObject>
        implements HasCiteableId, HasAssetId, HasName, HasXValue, IsQueryResult, Comparable<DObjectRef> {

    private String _citeableId;
    private String _assetId;
    private String _proute;
    private String _name;
    private int _numberOfChildren = -1;
    private boolean _foredit;

    private boolean _resolved = false;

    private DObjectRef _parent = null;

    public DObjectRef(String citeableId, int numberOfChildren) {
        this(citeableId, null, null, null, numberOfChildren, false);
    }

    public DObjectRef(String citeableId, String assetId, String proute, String name, int numberOfChildren,
            boolean foredit) {
        _citeableId = citeableId;
        _assetId = assetId;
        _proute = proute;
        _name = name;
        _numberOfChildren = numberOfChildren;
        _foredit = foredit;
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
    }

    @Override
    public boolean resolved() {

        if (_resolved == false) {
            return false;
        }
        return super.resolved();
    }

    @Override
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

    public void setForEdit(boolean forEdit) {
        if (_foredit != forEdit) {
            _foredit = forEdit;
            cancel();
            reset();
        }
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
        w.add("isleaf", true);

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
            return referent().objectType();
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
        StringBuilder sb = new StringBuilder(_citeableId);
        if(name()!=null) {
            sb.append(": ");
            sb.append(name());
        }
        return sb.toString();
    }

    public boolean isProject() {
        if (referent() != null) {
            return referent().objectType() == Type.PROJECT;
        } else {
            return CiteableIdUtils.isProject(citeableId());
        }
    }

    public boolean isSubject() {
        if (referent() != null) {
            return referent().objectType() == Type.SUBJECT;
        } else {
            return CiteableIdUtils.isSubject(citeableId());
        }
    }

    public boolean isExMethod() {
        if (referent() != null) {
            return referent().objectType() == Type.EX_METHOD;
        } else {
            return CiteableIdUtils.isExMethod(citeableId());
        }
    }

    public boolean isStudy() {
        if (referent() != null) {
            return referent().objectType() == Type.STUDY;
        } else {
            return CiteableIdUtils.isStudy(citeableId());
        }
    }

    public boolean isDataset() {
        if (referent() != null) {
            return referent().objectType() == Type.DATASET;
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

    public DObjectRef parent() {
        if (isProject()) {
            _parent = null;
        } else {
            if (_parent == null) {
                _parent = new DObjectRef(CiteableIdUtils.parent(_citeableId), -1);
            }
        }
        return _parent;
    }

    public boolean forEdit() {
        return _foredit;
    }

    public boolean isDirectParentOf(DObjectRef o) {
        if (o != null) {
            return CiteableIdUtils.isDirectParent(_citeableId, o.citeableId());
        }
        return false;
    }

    public String typeAndId() {
        return referentTypeName() + " " + citeableId();
    }

    public String projectCiteableId() {
        if (isProject()) {
            return _citeableId;
        } else {
            int n = CiteableIdUtils.depth(citeableId()) - CiteableIdUtils.PROJECT_CID_DEPTH;
            return CiteableIdUtils.parent(_citeableId, n);
        }
    }

    private Map<XPath, XValue> _xvalues;

    @Override
    public Collection<XValue> xvalues() {
        return _xvalues.values();
    }

    @Override
    public XValue xvalue(XPath xpath) {
        if (_xvalues != null) {
            return _xvalues.get(xpath);
        }
        return null;
    }

    @Override
    public void setXValue(XPath xpath, Collection<String> values) {
        if (_xvalues == null) {
            _xvalues = new LinkedHashMap<XPath, XValue>();
        }
        _xvalues.put(xpath, new XValue(xpath, values));
    }

    @Override
    public void setXValue(XPath xpath, String value) {
        if (_xvalues == null) {
            _xvalues = new LinkedHashMap<XPath, XValue>();
        }
        _xvalues.put(xpath, new XValue(xpath, value));
    }

    @Override
    public boolean hasXValues() {
        return _xvalues != null && !_xvalues.isEmpty();
    }

}

package daris.web.client.model.object;

import java.util.Collection;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.project.Project;
import daris.web.client.model.study.Study;
import daris.web.client.model.subject.Subject;

public abstract class DObject {

    public static enum Type {
        PROJECT, SUBJECT, EX_METHOD, STUDY, DATASET;

        @Override
        public String toString() {
            return name().toLowerCase().replace('_', '-');
        }

        public static Type fromString(String str) {
            if (str != null) {
                Type[] vs = values();
                for (Type v : vs) {
                    if (v.toString().equalsIgnoreCase(str)) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    private String _citeableId;
    private String _assetId;
    private String _proute;
    private String _vid;
    private boolean _editable;
    private int _version = 0;
    private boolean _isleaf;

    private String _name;
    private String _description;
    private String _namespace;
    private String _filename;

    private int _nbChildren = -1;

    private XmlElement _meta;
    private XmlElement _metaForEdit;

    private Collection<String> _tags;

    /**
     * 
     * @param oe
     *            The result of service om.pssd.object.describe.
     */
    protected DObject(XmlElement oe) {
        _citeableId = oe.value("id");
        _assetId = oe.value("id/@asset");
        _proute = oe.value("id/@proute");
        _vid = oe.value("@vid");
        try {
            _editable = oe.booleanValue("@editable", false);
        } catch (Throwable e) {
            _editable = false;
        }
        try {
            _version = oe.intValue("@version", 0);
        } catch (Throwable e) {
            _version = 0;
        }
        _name = oe.value("name");
        _description = oe.value("description");
        _namespace = oe.value("namespace");
        _filename = oe.value("filename");
        try {
            _isleaf = oe.booleanValue("isleaf", false);
        } catch (Throwable e) {
            _isleaf = false;
        }
        try {
            _nbChildren = oe.intValue("number-of-children", -1);
        } catch (Throwable e) {
            _nbChildren = -1;
        }
        XmlElement me = oe.element("meta");
        if (me != null) {
            if (me.element("metadata") != null) {
                _metaForEdit = me;
            } else {
                _meta = me;
            }
        }
        _tags = oe.values("tag");

    }

    public abstract DObject.Type type();

    public String citeableId() {
        return _citeableId;
    }

    public String assetId() {
        return _assetId;
    }

    public String filename() {
        return _filename;
    }

    public String name() {
        return _name;
    }

    public String description() {
        return _description;
    }

    public String namespace() {
        return _namespace;
    }

    public Collection<String> tags() {
        return _tags;
    }

    public boolean hasTags() {
        return _tags != null && !_tags.isEmpty();
    }

    public XmlElement metadata() {
        return _meta;
    }

    public XmlElement metadataForEdit() {
        return _metaForEdit;
    }

    public int numberOfChildren() {
        return _nbChildren;
    }

    public boolean isleaf() {
        return _isleaf;
    }

    public boolean editable() {
        return _editable;
    }

    public int version() {
        return _version;
    }

    public String vid() {
        return _vid;
    }

    public String proute() {
        return _proute;
    }

    public static DObject create(XmlElement oe) {
        DObject.Type type = DObject.Type.fromString(oe.value("@type"));
        switch (type) {
        case PROJECT:
            return new Project(oe);
        case SUBJECT:
            return new Subject(oe);
        case EX_METHOD:
            return new ExMethod(oe);
        case STUDY:
            return new Study(oe);
        case DATASET:
            return new Dataset(oe);
        default:
            throw new AssertionError("Unknown object type: " + oe.value("@type"));
        }
    }

}

package daris.web.client.model.subject;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.object.DObject;
import daris.web.client.model.project.DataUse;

public class Subject extends DObject {

    private boolean _virtual;
    private DataUse _dataUse;
    private MethodRef _method;
    private XmlElement _privateMeta;
    private XmlElement _publicMeta;
    private XmlElement _privateMetaForEdit;
    private XmlElement _publicMetaForEdit;

    public Subject(XmlElement oe) {

        super(oe);

        try {
            _virtual = oe.booleanValue("virtual", false);
        } catch (Throwable e) {

        }
        _dataUse = DataUse.fromString(oe.value("data-use"));
        XmlElement me = oe.element("method");
        if (me != null) {
            _method = new MethodRef(me.value("id"), me.value("name"), me.value("description"));
        }

        XmlElement mePrivate = oe.element("private");
        if (mePrivate != null) {
            if (mePrivate.element("metadata") == null) {
                _privateMeta = mePrivate;
            } else {
                _privateMetaForEdit = mePrivate;
            }
        }

        XmlElement mePublic = oe.element("public");
        if (mePublic != null) {
            if (mePublic.element("metadata") == null) {
                _publicMeta = mePublic;
            } else {
                _publicMetaForEdit = mePublic;
            }
        }

    }

    @Override
    public Type objectType() {
        return DObject.Type.SUBJECT;
    }

    public boolean isVirtual() {
        return _virtual;
    }

    public DataUse dataUse() {
        return _dataUse;
    }

    public XmlElement publicMetadata() {
        return _publicMeta;
    }

    public XmlElement privateMetadata() {
        return _privateMeta;
    }

    public XmlElement publicMetadataForEdit() {
        return _publicMetaForEdit;
    }

    public XmlElement privateMetadataForEdit() {
        return _privateMetaForEdit;
    }

    public MethodRef method() {
        return _method;
    }

}

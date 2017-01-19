package daris.web.client.model.method;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class MethodRef extends ObjectRef<Method> {

    private String _cid;
    private String _name;
    private String _description;

    public MethodRef(String cid, String name, String description) {

        _cid = cid;
        _name = name;
        _description = description;
    }

    @Override
    public String referentTypeName() {

        return Method.TYPE_NAME;
    }

    public String citeableId() {
        return _cid;
    }

    public String name() {

        return _name;
    }

    public String description() {

        return _description;
    }

    @Override
    public String toString() {

        return _cid + ": " + _name;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("cid", _cid);
        w.add("expand", true);
    }

    @Override
    protected String resolveServiceName() {
        return "om.pssd.method.describe";
    }

    @Override
    protected Method instantiate(XmlElement xe) throws Throwable {
        Method method = new Method(xe.element("method"));
        _name = method.name();
        _description = method.description();
        return method;
    }

    @Override
    public String idToString() {
        return _cid;
    }

}

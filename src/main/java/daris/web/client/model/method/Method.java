package daris.web.client.model.method;

import java.util.List;
import java.util.Vector;

import arc.mf.client.xml.XmlElement;

public class Method {

    public static final String TYPE_NAME = "method";

    private static Step instantiateStep(XmlElement se) {

        int id;
        try {
            id = se.intValue("@id");
        } catch (Throwable e) {
            id = 0;
        }
        String name = se.value("name");
        String description = se.value("description");

        // Sub-method? If so, the method may be inline, or a reference
        // to some external method.
        XmlElement me = se.element("method");
        if (me != null) {
            if (me.element("step") == null) {
                MethodReferenceStep mrs = new MethodReferenceStep(id, name, description);
                mrs.setReferencedMethod(me.value("id"));
                return mrs;
            }
            Method m = new Method(me);
            m.setSteps(instantiateSteps(me.elements("step")));
            MethodStep ms = new MethodStep(id, name, description, m);
            return ms;
        }

        // Branch?
        XmlElement be = se.element("branch");
        if (be != null) {
            String ts = be.value("@type");
            int type;
            if (ts.equalsIgnoreCase("or")) {
                type = BranchStep.BRANCH_ONE;
            } else {
                type = BranchStep.BRANCH_ALL;
            }
            BranchStep bs = new BranchStep(id, name, description, type);
            List<XmlElement> mes = be.elements("method");
            if (mes != null) {
                for (int i = 0; i < mes.size(); i++) {
                    XmlElement mse = mes.get(i);
                    Method m = new Method(mse);
                    m.setSteps(instantiateSteps(mse.elements("step")));
                    bs.addMethod(m);
                }
            }
            return bs;
        }

        // Must be an action step of one kind of another.
        XmlElement sse = se.element("study");
        if (sse != null) {
            String type = sse.value("type");
            // TODO: populate study meta
            // List<XmlElement> ediableStudyMeta = sse.elements("metadata");
            StudyActionStep as = new StudyActionStep(id, name, description, type, null);
            return as;
        }
        // Must be a subject action
        // TODO: subject meta and rsubjectmeta
        SubjectActionStep as = new SubjectActionStep(id, name, description, null, null);
        return as;

    }

    private static List<Step> instantiateSteps(List<XmlElement> ses) {

        if (ses == null) {
            return null;
        }
        List<Step> steps = new Vector<Step>(ses.size());
        for (XmlElement se : ses) {
            steps.add(instantiateStep(se));
        }
        return steps;
    }

    private String _assetId;
    private String _cid;
    private String _proute;
    private boolean _editable;
    private int _version;
    private String _name;
    private String _description;
    private String _namespace;

    private List<Step> _steps;
    private String _author;

    public Method(XmlElement xe) {
        _cid = xe.value("id");
        if (_cid == null) {
            _cid = xe.value("@id");
        }
        _proute = xe.value("@proute");
        if (_proute == null) {
            _proute = xe.value("id/@proute");
        }
        _editable = false;
        try {
            _version = xe.intValue("@version", 0);
        } catch (Throwable e) {
        }
        _name = xe.value("name");
        _description = xe.value("description");
        _namespace = xe.value("namespace");

        XmlElement me = null;
        if (xe.name().equals("object")) {
            // xe is the result of om.pssd.object.describe
            me = xe.element("method");
        } else if (xe.name().equals("method")) {
            // xe is the result of om.pssd.method.describe or
            // (om.pssd.object.describe ex-method)
            me = xe;
        }
        if (me == null) {
            throw new AssertionError(" No method element found.");
        }
        _author = me.value("author");
        List<XmlElement> ses = me.elements("step");
        if (ses != null) {
            setSteps(instantiateSteps(ses));
        }
    }

    public void setSteps(List<Step> steps) {

        _steps = steps;
    }

    public List<Step> steps() {

        return _steps;
    }

    public String author() {

        return _author;
    }

    public String assetId() {
        return _assetId;
    }

    public String citeableId() {
        return _cid;
    }

    public String proute() {
        return _proute;
    }

    public boolean editable() {
        return _editable;
    }

    public int version() {
        return _version;
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
}

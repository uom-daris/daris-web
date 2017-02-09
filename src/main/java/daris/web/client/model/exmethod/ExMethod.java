package daris.web.client.model.exmethod;

import java.util.List;
import java.util.Vector;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.method.Method;
import daris.web.client.model.object.DObject;

public class ExMethod extends DObject {

    private Method _method;
    private XmlElement _methodElement;
    private List<ExMethodStep> _steps;
    private State _state;

    public ExMethod(XmlElement oe) {

        super(oe);

        try {
            _state = State.fromString(oe.stringValue("state", State.incomplete.toString()));
        } catch (Throwable e) {
            _state = State.incomplete;
        }
        _method = null;
        _methodElement = oe.element("method");
        if (_methodElement != null) {
            _method = new Method(_methodElement);
        }
        _steps = null;
        List<XmlElement> ses = oe.elements("step");
        if (ses != null) {
            _steps = new Vector<ExMethodStep>(ses.size());
            for (XmlElement se : ses) {
                String stepPath = se.value("@path");
                State state = State.fromString(se.value("state"));
                String notes = se.value("notes");
                ExMethodStep ems = new ExMethodStep(citeableId(), proute(), stepPath, null, state, notes, false);
                _steps.add(ems);
            }
        }
    }

    public Method method() {

        return _method;
    }

    public XmlElement methodElement() {

        return _methodElement;
    }

    public ExMethodStep step(String stepPath) {

        if (_steps == null) {
            return null;
        }

        for (ExMethodStep ems : _steps) {
            if (ems.stepPath().equals(stepPath)) {
                return ems;
            }
        }
        return null;
    }

    public List<ExMethodStep> steps() {

        return _steps;
    }

    public State state() {

        return _state;
    }

    @Override
    public Type type() {
        return DObject.Type.EX_METHOD;
    }

}

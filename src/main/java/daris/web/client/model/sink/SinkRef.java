package daris.web.client.model.sink;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class SinkRef extends ObjectRef<Sink> {

    private String _name;

    public SinkRef(String name) {
        _name = name;
    }

    public String name() {
        return _name;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("service", new String[] { "name", "sink.type.describe" });
        w.push("service", new String[] { "name", "sink.describe" });
        w.add("name", _name);
        w.pop();
        w.push("service", new String[] { "name", "user.self.settings.get" });
        w.add("app", "sink." + _name);
        w.pop();
    }

    @Override
    protected String resolveServiceName() {
        return "service.execute";
    }

    @Override
    protected Sink instantiate(XmlElement xe) throws Throwable {
        XmlElement se = xe.element("reply[@service='sink.describe']/response/sink");
        String typeName = se.value("destination/type");
        XmlElement ste = xe.element("reply[@service='sink.type.describe']/response/sink[@type='" + typeName + "']");
        Sink sink = new Sink(se, ste);
        // args in user.self.settings.
        XmlElement us = xe
                .element("reply[@service='user.self.settings.get']/response/settings[@app='sink." + _name + "']");
        if (us != null) {
            List<XmlElement> aes = us.elements("arg");
            if (aes != null) {
                for (XmlElement ae : aes) {
                    String argName = ae.value("@name");
                    String argValue = ae.value();
                    sink.setArg(argName, argValue);
                }
            }
        }
        return sink;
    }

    @Override
    public String referentTypeName() {
        return "sink";
    }

    @Override
    public String idToString() {
        return _name;
    }

}

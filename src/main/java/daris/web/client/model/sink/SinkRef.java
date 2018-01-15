package daris.web.client.model.sink;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class SinkRef extends ObjectRef<Sink> {

    private String _name;

    public SinkRef(String name) {
        _name = name;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("service", new String[] { "name", "sink.type.describe" });
        w.push("service", new String[] { "name", "sink.describe" });
        w.add("name", _name);
        w.pop();

    }

    @Override
    protected String resolveServiceName() {
        return "service.execute";
    }

    @Override
    protected Sink instantiate(XmlElement xe) throws Throwable {
        XmlElement se = xe.element("reply[@service='sink.describe']/response/sink");
        String type = se.value("destination/type");
        XmlElement te = xe.element("reply[@service='sink.type.describe']/response/sink[@type='" + type + "']");
        return new Sink(se, te);
    }

    @Override
    public String referentTypeName() {
        return Sink.TYPE_NAME;
    }

    @Override
    public String idToString() {
        return _name;
    }

}

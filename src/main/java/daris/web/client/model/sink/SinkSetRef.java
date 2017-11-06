package daris.web.client.model.sink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class SinkSetRef extends ObjectRef<List<Sink>> {

    public static final SinkSetRef DARIS_SINKS = new SinkSetRef("^" + SinkConstants.SINK_TYPE_NAME_PREFIX + ".+", null);

    private String _sinkTypeNameRegex;
    private String _sinkNameRegex;

    public SinkSetRef(String sinkTypeNameRegex, String sinkNameRegex) {
        _sinkTypeNameRegex = sinkTypeNameRegex;
        _sinkNameRegex = sinkNameRegex;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("service", new String[] { "name", "sink.type.describe" });
        w.add("service", new String[] { "name", "sink.describe" });
    }

    @Override
    protected String resolveServiceName() {
        return "service.execute";
    }

    @Override
    protected List<Sink> instantiate(XmlElement xe) throws Throwable {
        Collection<String> sinkNames = xe.values("reply[@service='sink.describe']/response/sink/@name");
        if (sinkNames != null && !sinkNames.isEmpty()) {
            List<Sink> sinks = new ArrayList<Sink>(sinkNames.size());
            for (String sinkName : sinkNames) {
                if (_sinkNameRegex == null || sinkName.matches(_sinkNameRegex)) {
                    XmlElement se = xe
                            .element("reply[@service='sink.describe']/response/sink[@name='" + sinkName + "']");
                    String typeName = se.value("destination/type");
                    if (_sinkTypeNameRegex == null || typeName.matches(_sinkTypeNameRegex)) {
                        XmlElement ste = xe.element(
                                "reply[@service='sink.type.describe']/response/sink[@type='" + typeName + "']");
                        sinks.add(new Sink(se, ste));
                    }
                }
            }
            if (!sinks.isEmpty()) {
                return sinks;
            }
        }
        return null;
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return null;
    }

}

package daris.web.client.model.sink.exports;

import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlWriter;
import arc.mf.object.BackgroundObjectMessage;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.sink.Sink;

public class AssetSinkSend extends BackgroundObjectMessage {

    private String _where;
    private Sink _sink;

    public AssetSinkSend(String where, Sink sink) {
        _where = where;
        _sink = sink;
        setDescription("send data to sink: " + sink.name());
    }

    public AssetSinkSend(DObjectRef o, Sink sink) {
        this("cid='" + o.citeableId() + "' or cid starts with '" + o.citeableId() + "'", sink);
    }

    public AssetSinkSend(DObject o, Sink sink) {
        this("cid='" + o.citeableId() + "' or cid starts with '" + o.citeableId() + "'", sink);
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        w.add("where", "(" + _where + ") and asset has content");
        w.push("sink");
        w.add("name", _sink.name());
        Map<String, String> args = _sink.args();
        if (args != null) {
            Set<String> argNames = args.keySet();
            for (String argName : argNames) {
                String argValue = args.get(argName);
                if (argValue != null) {
                    w.add("arg", new String[] { "name", argName }, argValue);
                }
            }
        }
        w.pop();
    }

    @Override
    protected String messageServiceName() {
        return "asset.sink.send";
    }

    @Override
    protected String objectTypeName() {
        return null;
    }

    @Override
    protected String idToString() {
        return null;
    }

}

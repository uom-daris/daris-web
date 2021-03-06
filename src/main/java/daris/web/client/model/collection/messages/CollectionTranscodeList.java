package daris.web.client.model.collection.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;

public class CollectionTranscodeList extends ObjectMessage<Map<String, List<String>>> {

    public static final String SERVICE_NAME = "daris.collection.transcode.list";

    private String _cid;
    private String _where;

    public CollectionTranscodeList(String cid, String where) {
        _cid = cid;
        _where = null;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_cid != null) {
            w.add("cid", _cid);
        }
        if (_where != null) {
            w.add("where", _where);
        }
    }

    @Override
    protected String messageServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected Map<String, List<String>> instantiate(XmlElement xe) throws Throwable {
        return instantiateAvailableTranscodes(xe);
    }

    @Override
    protected String objectTypeName() {
        DObject.Type type = CiteableIdUtils.type(_cid);
        return type == null ? null : type.toString();
    }

    @Override
    protected String idToString() {
        return _cid == null ? null : _cid;
    }

    public static Map<String, List<String>> instantiateAvailableTranscodes(XmlElement xe) throws Throwable {
        List<XmlElement> tes = xe.elements("transcode");
        if (tes != null && !tes.isEmpty()) {
            Map<String, List<String>> transcodes = new TreeMap<String, List<String>>();
            for (XmlElement te : tes) {
                String from = te.value("@from");
                List<String> tos = new ArrayList<String>(te.values("to"));
                transcodes.put(from, tos);
            }
            return transcodes;
        }
        return null;
    }

}

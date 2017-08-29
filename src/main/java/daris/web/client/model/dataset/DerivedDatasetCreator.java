package daris.web.client.model.dataset;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectRef;

public class DerivedDatasetCreator extends DatasetCreator {

    private Map<String, String> _inputs;

    @SafeVarargs
    public DerivedDatasetCreator(DObjectRef parent, SimpleEntry<String, String>... inputs) {
        super(parent);
        if (inputs != null && inputs.length > 0) {
            _inputs = new HashMap<String, String>();
            for (SimpleEntry<String, String> input : inputs) {
                _inputs.put(input.getKey(), input.getValue());
            }
        }
    }

    public Map<String, String> inputs() {
        return _inputs;
    }

    public void setInputs(Map<String, String> inputs) {
        _inputs = inputs;
    }

    public void addInput(String cid, String vid) {
        if (_inputs == null) {
            _inputs = new HashMap<String, String>();
        }
        _inputs.put(cid, vid);
    }

    @Override
    public String serviceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        // TODO Auto-generated method stub

    }

}

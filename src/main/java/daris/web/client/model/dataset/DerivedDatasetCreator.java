package daris.web.client.model.dataset;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectRef;

public class DerivedDatasetCreator extends DatasetCreator {

    private Map<String, String> _inputs;
    private Boolean _processed;

    public DerivedDatasetCreator(DObjectRef parent, Map<String, String> inputs) {
        super(parent);
        _inputs = inputs;
    }

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

    public DerivedDatasetCreator(Dataset input) {
        this(new DObjectRef(CiteableIdUtils.parent(input.citeableId()), -1),
                new SimpleEntry<String, String>(input.citeableId(), input.vid()));
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
        return "om.pssd.dataset.derivation.create";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        w.add("pid", parentObject().citeableId());
        if (allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", allowIncompleteMeta());
        }
        if (fillInIdNumber()) {
            w.add("fillin", true);
        }
        if (name() != null) {
            w.add("name", name());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (type() != null) {
            w.add("type", type());
        }
        if (contentType() != null) {
            w.add("ctype", contentType());
        }
        if (logicalContentType() != null) {
            w.add("lctype", logicalContentType());
        }
        if (hasInputs()) {
            Set<String> inputCids = _inputs.keySet();
            for (String inputCid : inputCids) {
                w.add("input", new String[] { "vid", _inputs.get(inputCid) }, inputCid);
            }
        }
        if (_processed != null) {
            w.add("processed", _processed);
        }
        if (methodStep() != null) {
            w.push("method");
            w.add("step", methodStep());
            if (methodId() != null) {
                w.add("id", methodId());
            }
            w.pop();
        }
        if (numberOfFiles() == 1 && archiveType() == null) {
            w.add("filename", files().iterator().next().file.name());
        }
    }

    public boolean hasInputs() {
        return _inputs != null && !_inputs.isEmpty();
    }

    public void setProcessed(Boolean processed) {
        _processed = processed;
    }

    public Boolean processed() {
        return _processed;
    }

}

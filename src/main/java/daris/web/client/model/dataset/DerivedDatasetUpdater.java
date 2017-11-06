package daris.web.client.model.dataset;

import java.util.List;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.dataset.DerivedDataset.Input;

public class DerivedDatasetUpdater extends DatasetUpdater<DerivedDataset> {

    private Boolean _processed;
    private Boolean _anonymized;

    public DerivedDatasetUpdater(DerivedDataset obj) {
        super(obj);
        _processed = obj.processed();
        _anonymized = obj.anonymized();
    }

    @Override
    public String serviceName() {
        return "om.pssd.dataset.derivation.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        if (this.allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", this.allowIncompleteMeta());
        }
        if (this.anonymized() != null && this.anonymized()) {
            w.add("anonymized", this.anonymized());
        }
        if (this.contentType() != null) {
            w.add("ctype", this.contentType());
        }
        if (this.description() != null) {
            w.add("description", this.description());
        }
        if (this.filename() != null) {
            w.add("filename", this.filename());
        }
        w.add("id", this.object().citeableId());
        if (this.logicalContentType() != null) {
            w.add("lctype", this.logicalContentType());
        }
        if (object().hasInputs()) {
            List<Input> inputs = object().inputs();
            for (Input input : inputs) {
                w.add("input", new String[] { "vid", input.vid() }, input.citeableId());
            }
        }
        if (metadataSetter() != null) {
            metadataSetter().setMetadata(w);
        }
        if (object().methodCid() != null) {
            w.push("method");
            w.add("id", object().methodCid());
            if (object().methodStep() != null) {
                w.add("step", object().methodStep());
            }
            w.pop();
        }
        if (name() != null) {
            w.add("name", name());
        }
        if (processed() != null) {
            w.add("processed", processed());
        }
        if (type() != null) {
            w.add("type", type());
        }
    }

    public Boolean processed() {
        return _processed;
    }

    public void setProcessed(Boolean processed) {
        _processed = processed;
    }

    public Boolean anonymized() {
        return _anonymized;
    }

    public void setAnonymized(Boolean anonymized) {
        _anonymized = anonymized;
    }

}

package daris.web.client.model.dataset;

import arc.mf.client.xml.XmlWriter;

public class PrimaryDatasetUpdater extends DatasetUpdater<PrimaryDataset> {

    public PrimaryDatasetUpdater(PrimaryDataset obj) {
        super(obj);
    }

    @Override
    public String serviceName() {
        return "om.pssd.dataset.primary.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        if (this.allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", this.allowIncompleteMeta());
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
        if (object().subjectCid() != null) {
            w.add("subject", new String[] { "state", object().subjectState() }, object().subjectCid());
        }
        if (type() != null) {
            w.add("type", type());
        }
    }

}

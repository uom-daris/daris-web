package daris.web.client.model.dataset;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectRef;

public class PrimaryDatasetCreator extends DatasetCreator {
    private String _subjectId;

    public PrimaryDatasetCreator(DObjectRef study) {
        super(study);
    }

    public String subjectId() {
        return _subjectId;
    }

    public void setSubject(String subjectId) {
        _subjectId = subjectId;
    }

    @Override
    public String serviceName() {
        return "om.pssd.dataset.primary.create";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        w.add("pid", parentObject().citeableId());
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
        if (subjectId() != null) {
            w.push("subject");
            w.add("id", subjectId());
            w.pop();
        }
    }

}

package daris.web.client.model.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.object.DObjectUpdater;

public class ProjectUpdater extends DObjectUpdater<Project> {

    private DataUse _dataUse;
    private List<MethodRef> _methods;

    public ProjectUpdater(Project obj) {
        super(obj);
        _methods = new ArrayList<MethodRef>();
        List<MethodRef> methods = obj.methods();
        if (methods != null && !methods.isEmpty()) {
            _methods.addAll(methods);

        }
        _dataUse = obj.dataUse();
    }

    public List<MethodRef> methods() {
        return _methods;
    }

    public void clearMethods() {
        _methods.clear();
    }

    public void addMethod(MethodRef method) {
        if (!_methods.contains(method)) {
            _methods.add(method);
        }
    }

    public DataUse dataUse() {
        return _dataUse;
    }

    public void setDataUse(DataUse dataUse) {
        _dataUse = dataUse;
    }

    public void setMethods(Collection<MethodRef> methods) {
        _methods.clear();
        if (methods != null) {
            _methods.addAll(methods);
        }
    }

    @Override
    public String serviceName() {
        return "om.pssd.project.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {

        w.add("id", object().citeableId());
        if (name() != null) {
            w.add("name", name());
        }
        if (allowIncompleteMeta()) {
            w.add("allow-incomplete-meta", true);
        }
        if (allowInvalidMeta()) {
            w.add("allow-invalid-meta", true);
        }
        if (dataUse() != null) {
            w.add("data-use", dataUse());
        }
        if (description() != null) {
            w.add("description", description());
        }
        if (this.metadataSetter != null) {
            this.metadataSetter.setMetadata(w);
        }
        if (!_methods.isEmpty()) {
            for (MethodRef method : _methods) {
                w.push("method");
                w.add("id", method.citeableId());
                w.pop();
            }
        }
    }

}

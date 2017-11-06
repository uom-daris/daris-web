package daris.web.client.model.project;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectCreator;

public class ProjectCreator extends DObjectCreator {

    private Set<String> _availableCidRootNames;
    private String _cidRootName;
    private Set<String> _availableNamespaces;
    private String _namespace;
    private Map<String, String> _methods; // ID -> Notes
    private DataUse _dataUse;

    public ProjectCreator() {
        super(null);
    }

    public void setCidRootName(String cidRootName) {
        _cidRootName = cidRootName;
    }

    public void setNamespace(String namespace) {
        _namespace = namespace;
    }

    public void addMethod(String methodId) {
        if (_methods == null || !_methods.containsKey(methodId)) {
            addMethod(methodId, null);
        }
    }

    public void addMethod(String methodId, String methodNotes) {
        if (_methods == null) {
            _methods = new HashMap<String, String>();
        }
        _methods.put(methodId, methodNotes);
    }

    public void clearMethods() {
        if (_methods != null) {
            _methods.clear();
        }
    }

    public void setDataUse(DataUse dataUse) {
        _dataUse = dataUse;
    }

    public ProjectCreator setAvailableAssetNamespaces(Set<String> namespaces) {
        _availableNamespaces = namespaces;
        return this;
    }

    public ProjectCreator setAvailableCidRootNames(Set<String> cidRootNames) {
        _availableCidRootNames = cidRootNames;
        return this;
    }

    @Override
    public String serviceName() {
        return "om.pssd.project.create";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
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
        if (_cidRootName != null) {
            w.add("cid-root-name", _cidRootName);
        }
        if (_namespace != null) {
            w.add("namespace", _namespace);
        }
        if (_methods != null) {
            Set<String> methodIds = _methods.keySet();
            for (String methodId : methodIds) {
                w.push("method");
                w.add("id", methodId);
                String notes = _methods.get(methodId);
                if (notes != null) {
                    w.add("notes", notes);
                }
                w.pop();
            }
        }
        if (_dataUse != null) {
            w.add("data-use", _dataUse);
        }
        if (metadataSetter() != null) {
            metadataSetter().setMetadata(w);
        }
    }

    public Set<String> availableCidRootNames() {
        return _availableCidRootNames;
    }

    public Set<String> availableAssetNamespaces() {
        return _availableNamespaces;
    }

}

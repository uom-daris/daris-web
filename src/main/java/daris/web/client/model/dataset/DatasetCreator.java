package daris.web.client.model.dataset;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.file.LocalFile;
import daris.web.client.model.archive.ArchiveType;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;

public abstract class DatasetCreator extends DObjectCreator {

    private String _ctype;
    private String _lctype;
    private String _methodId;
    private String _methodStep;
    private List<LocalFile> _files;
    private ArchiveType _atype;

    public DatasetCreator(DObjectRef parent) {
        super(parent);
        _files = new ArrayList<LocalFile>();
    }

    public String contentType() {
        return _ctype;
    }

    public void setContentType(String ctype) {
        _ctype = ctype;
    }

    public String logicalContentType() {
        return _lctype;
    }

    public void setLogicalContentType(String lctype) {
        _lctype = lctype;
    }

    public void setFiles(List<LocalFile> files) {
        if (files == null || files.isEmpty()) {
            _files.clear();
        } else {
            _files.addAll(files);
        }
    }

    public boolean addFile(LocalFile file) {
        if (!contains(file)) {
            _files.add(file);
            return true;
        }
        return false;
    }

    private boolean contains(LocalFile file) {
        if (file != null) {
            for (LocalFile f : _files) {
                if (f.path() != null && f.path().equals(file.path())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<LocalFile> files() {
        return _files;
    }

    public boolean hasFiles() {
        return _files != null && !_files.isEmpty();
    }

    public ArchiveType archiveType() {
        return _atype;
    }

    public void setArchiveType(ArchiveType atype) {
        _atype = atype;
    }

    public String methodId() {
        return _methodId;
    }

    public String methodStep() {
        return _methodStep;
    }

    public void setMethod(String id, String step) {
        _methodId = id;
        _methodStep = step;
    }

}

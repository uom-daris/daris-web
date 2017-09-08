package daris.web.client.model.dataset;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import arc.mf.client.file.LocalFile;
import daris.web.client.model.archive.ArchiveType;
import daris.web.client.model.object.DObjectCreator;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.upload.FileEntry;

public abstract class DatasetCreator extends DObjectCreator {

    private String _type;
    private String _ctype;
    private String _lctype;
    private String _methodId;
    private String _methodStep;
    private Map<String, FileEntry> _files;

    private ArchiveType _atype;

    public DatasetCreator(DObjectRef parent) {
        super(parent);
        _files = new TreeMap<String, FileEntry>();
    }

    public String type() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
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

    public void addFile(LocalFile file, String dstPath) {
        assert !file.isDirectory();
        addFile(new FileEntry(file, dstPath));
    }

    public void addFile(FileEntry file) {
        if (file != null) {
            assert !file.file.isDirectory();
            _files.put(file.dstPath, file);
            if (_files.size() > 1 && _atype == null) {
                _atype = ArchiveType.AAR;
            }
        }
    }

    public void setFiles(Map<String, FileEntry> files) {
        if (files != null) {
            _files = files;
        } else {
            _files.clear();
        }
    }

    public Collection<FileEntry> files() {
        return _files.values();
    }

    public boolean hasFiles() {
        return _files != null && !_files.isEmpty();
    }

    public int numberOfFiles() {
        return _files == null ? 0 : _files.size();
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

    public void setMethodStep(String step) {
        _methodStep = step;
    }

}

package daris.web.client.model.dataset;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import daris.web.client.model.archive.ArchiveType;
import daris.web.client.model.object.DObjectUpdater;
import daris.web.client.model.object.imports.FileEntry;

public abstract class DatasetUpdater<T extends Dataset> extends DObjectUpdater<T> {

    private String _mimeType;
    private String _ctype;
    private String _lctype;
    private String _filename;
    private Map<String, FileEntry> _files;
    private ArchiveType _atype;

    protected DatasetUpdater(T obj) {
        super(obj);
        _mimeType = obj.mimeType();
        _ctype = obj.content() == null ? null : obj.content().type();
        _lctype = obj.content() == null ? null : obj.content().ltype();
        _filename = obj.filename();
        _files = new TreeMap<String, FileEntry>();
    }

    public void setFiles(Map<String, FileEntry> files) {
        if (files != null) {
            _files = files;
        } else {
            _files.clear();
        }
        if (_atype == null) {
            if (_files.size() > 1) {
                setArchiveType(ArchiveType.AAR);
            }
        } else {
            if (_files.size() == 1) {
                setArchiveType(null);
            }
        }
        if (_filename == null) {
            if (_files.size() == 1) {
                setFilename(_files.values().iterator().next().filename());
            }
        } else {
            if (_files.size() > 1) {
                setFilename(null);
            }
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

    public String mimeType() {
        return _mimeType;
    }

    public void setMimeType(String type) {
        _mimeType = type;
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

    public String filename() {
        return _filename;
    }

    public void setFilename(String filename) {
        _filename = filename;
    }

    public ArchiveType archiveType() {
        return _atype;
    }

    public void setArchiveType(ArchiveType atype) {
        _atype = atype;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Dataset> DatasetUpdater<T> create(T dataset) {
        if (dataset instanceof DerivedDataset) {
            return (DatasetUpdater<T>) new DerivedDatasetUpdater((DerivedDataset) dataset);
        } else if (dataset instanceof PrimaryDataset) {
            return (DatasetUpdater<T>) new PrimaryDatasetUpdater((PrimaryDataset) dataset);
        } else {
            throw new AssertionError("Unknown dataset class: " + dataset.getClass().getCanonicalName());
        }
    }

}

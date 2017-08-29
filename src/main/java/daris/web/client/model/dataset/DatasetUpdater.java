package daris.web.client.model.dataset;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.file.LocalFile;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.DObjectUpdater;

public abstract class DatasetUpdater extends DObjectUpdater {

    private String _type;
    private String _ctype;
    private String _lctype;
    private String _filename;
    private List<LocalFile> _files;

    protected DatasetUpdater(DObjectRef obj) {
        super(obj);
        _files = new ArrayList<LocalFile>();
    }

    public void setFiles(List<LocalFile> files) {
        if (files == null || files.isEmpty()) {
            _files.clear();
        } else {
            _files.addAll(files);
        }
    }

    public void addFile(LocalFile file) {
        _files.add(file);
    }

    public List<LocalFile> files() {
        return _files;
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

    public String filename() {
        return _filename;
    }

    public void setFilename(String filename) {
        _filename = filename;
    }

}

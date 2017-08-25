package daris.web.client.model.object.upload;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.file.FileHandler;
import arc.mf.client.file.LocalFile;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.util.PathUtils;

public abstract class FileSetUploadTask<T> {

    public static interface UploadProgressListener {
        void uploaded(int uploaded, int total, LocalFile uploading);
    }

    public static interface ConsumeListener<T> {
        void consumed(T result);
    }

    public static class FileEntry {
        public final LocalFile file;
        public final String dstPath;

        FileEntry(LocalFile file, String dstPath) {
            this.file = file;
            this.dstPath = dstPath;
        }
    }

    private LinkedList<FileEntry> _entries;
    private List<UploadProgressListener> _upls;
    private String _tmpDir;
    private int _uploaded = 0;
    private int _total = 0;
    private ObjectMessageResponse<T> _rh;

    protected FileSetUploadTask() {
        _entries = new LinkedList<FileEntry>();
        _tmpDir = null;
        _uploaded = 0;
        _total = 0;
    }

    public void add(LocalFile f, String basePath) {
        if (f.isDirectory()) {
            f.files(null, 0, Integer.MAX_VALUE, new FileHandler() {

                @Override
                public void process(long start, long end, long total, List<LocalFile> files) {
                    if (files != null) {
                        for (LocalFile file : files) {
                            add(file, basePath);
                        }
                    }
                }
            });
        } else {
            _entries.add(new FileEntry(f, PathUtils.relativePath(f.path(), basePath)));
        }
    }

    public void addUploadProgressListener(UploadProgressListener upl) {
        if (_upls == null) {
            _upls = new ArrayList<UploadProgressListener>();
        }
        _upls.add(upl);
    }

    private void notifyOfUploadProgress(int uploaded, int total, LocalFile file) {
        if (_upls != null) {
            for (UploadProgressListener upl : _upls) {
                upl.uploaded(uploaded, total, file);
            }
        }
    }

    public void execute(ObjectMessageResponse<T> rh) {
        _rh = rh;
        _uploaded = 0;
        _total = _entries.size();
        if (_entries.isEmpty()) {
            notifyOfUploadProgress(0, 0, null);
            if (_rh != null) {
                _rh.responded(null);
            }
            return;
        }
        Session.execute("daris.tmp.directory.create", new ServiceResponseHandler() {
            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                _tmpDir = xe.value("path");
                uploadFile(_entries.poll());
            }
        });
    }

    private void uploadFile(FileEntry entry) {
        notifyOfUploadProgress(_uploaded, _total, entry.file);
        XmlStringWriter w = new XmlStringWriter();
        w.add("directory", _tmpDir);
        w.add("path", entry.dstPath);
        w.add("if-exists", "overwrite");
        w.add("cleanup-on-error", true);
        Session.execute("daris.tmp.directory.file.upload", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                _uploaded++;
                _total--;
                FileEntry entry = _entries.poll();
                if (entry == null) {
                    consumeTmpDirectory();
                } else {
                    uploadFile(entry);
                }
            }
        });
    }

    private void consumeTmpDirectory() {
        XmlStringWriter w = new XmlStringWriter();
        w.add("path", _tmpDir);
        w.push("service", new String[] { "name", consumeServiceName() });
        consumeServiceArgs(w);
        w.pop();
        Session.execute("daris.tmp.directory.consume", w.document(), (xe, outputs) -> {
            T o = instantiate(xe);
            if (_rh != null) {
                _rh.responded(o);
            }
        });
    }

    protected abstract String consumeServiceName();

    protected abstract void consumeServiceArgs(XmlWriter w);

    protected abstract T instantiate(XmlElement xe);

}

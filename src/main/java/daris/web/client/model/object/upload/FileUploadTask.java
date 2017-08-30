package daris.web.client.model.object.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.RemoteServer;
import arc.mf.client.ResponseHandler;
import arc.mf.client.ServiceRequest;
import arc.mf.client.file.LocalFile;
import arc.mf.client.util.ListUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.model.archive.ArchiveType;

public abstract class FileUploadTask<T> {

    public static interface Listener {
        void updated(FileUploadTask<?> task);
    }

    public static enum State {
        INITIAL, UPLOADING, CONSUMING, ABORTED, SUCCEEDED, FAILED
    }

    private static long _idGen = 1;

    private static long nextId() {
        return _idGen++;
    }

    private long _id;
    private String _name;

    private LinkedList<FileEntry> _entries;
    private List<Listener> _ls;
    private String _tmpDir;
    private int _uploaded = 0;
    private int _total = 0;
    private LocalFile _uploading = null;
    private ObjectMessageResponse<T> _rh;
    private ArchiveType _atype;
    private State _state;
    private ServiceRequest _sr;
    private Throwable _thrown;

    protected FileUploadTask(Collection<FileEntry> files) {
        _id = nextId();
        _entries = new LinkedList<FileEntry>();
        if (files != null && !files.isEmpty()) {
            _entries.addAll(files);
        }
        _tmpDir = null;
        _uploaded = 0;
        _total = (_entries == null || _entries.isEmpty()) ? 0 : _entries.size();
        _uploading = null;
        _atype = null;
        _state = State.INITIAL;
    }

    public long id() {
        return _id;
    }

    public String name() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setArchiveType(ArchiveType atype) {
        _atype = atype;
    }

    public ArchiveType archiveType() {
        return _atype;
    }

    public void addListener(Listener l) {
        if (_ls == null) {
            _ls = new ArrayList<Listener>();
        }
        _ls.add(l);
    }

    private void notifyOfUpdate() {
        if (_ls != null) {
            for (Listener l : _ls) {
                l.updated(this);
            }
        }
    }

    public void execute(ObjectMessageResponse<T> rh, FileUploadTaskMonitor monitor) {
        if (monitor != null) {
            monitor.startMonitor(this);
        }
        setState(State.UPLOADING, true);
        _rh = rh;
        _uploaded = 0;
        _total = _entries.size();
        if (_entries.isEmpty()) {
            if (_rh != null) {
                _rh.responded(null);
            }
            setState(State.SUCCEEDED, true);
            return;
        }
        if (_entries.size() == 1) {
            if (_atype == null) {
                _uploading = _entries.get(0).file;
                setState(State.CONSUMING, true);
                XmlStringWriter w = new XmlStringWriter();
                consumeServiceArgs(w);
                _sr = RemoteServer.execute(null, consumeServiceName(), w.document(), ListUtil.list(_uploading), 0,
                        /* ProgressHandler */null, new ResponseHandler() {

                            @Override
                            public void processResponse(XmlElement xe, List<Output> outputs) {
                                if (rh != null) {
                                    rh.responded(instantiate(xe));
                                }
                                _uploaded++;
                                setState(State.SUCCEEDED, true);
                            }

                            @Override
                            public void processError(Throwable se) {
                                _thrown = se;
                                if (rh != null) {
                                    rh.responded(null);
                                }
                                setState(State.FAILED, true);
                            }
                        });
                return;
            }
        } else if (_entries.size() > 1) {
            if (_atype == null) {
                _atype = ArchiveType.AAR;
            }
        }
        _sr = RemoteServer.execute("daris.tmp.directory.create", new ResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) {
                _tmpDir = xe.value("path");
                uploadFile(_entries.poll());
            }

            @Override
            public void processError(Throwable se) {
                _thrown = se;
                if (rh != null) {
                    rh.responded(null);
                }
                setState(State.FAILED, true);
            }
        });
    }

    private void setState(State state, boolean fireEvent) {
        if (_state != state) {
            _state = state;
            notifyOfUpdate();
        }
    }

    private void uploadFile(FileEntry entry) {
        _uploading = entry.file;
        notifyOfUpdate();
        XmlStringWriter w = new XmlStringWriter();
        w.add("directory", _tmpDir);
        w.add("path", entry.dstPath);
        w.add("if-exists", "overwrite");
        w.add("cleanup-on-error", true);
        _sr = RemoteServer.execute(null, "daris.tmp.directory.file.upload", w.document(), ListUtil.list(entry.file), 0,
                /* ProgressHandler */null, new ResponseHandler() {

                    @Override
                    public void processResponse(XmlElement xe, List<Output> outputs) {
                        _uploaded++;
                        _total--;
                        FileEntry entry = _entries.poll();
                        if (entry == null) {
                            consumeTmpDirectory();
                        } else {
                            uploadFile(entry);
                        }
                    }

                    @Override
                    public void processError(Throwable se) {
                        _thrown = se;
                        if (_rh != null) {
                            _rh.responded(null);
                        }
                        setState(State.FAILED, true);
                    }
                });
    }

    private void consumeTmpDirectory() {
        setState(State.CONSUMING, true);
        XmlStringWriter w = new XmlStringWriter();
        if (_atype != null) {
            w.add("atype", _atype.toString());
        }
        w.add("path", _tmpDir);
        w.push("service", new String[] { "name", consumeServiceName() });
        consumeServiceArgs(w);
        w.pop();
        _sr = RemoteServer.execute("daris.tmp.directory.consume", w.document(), new ResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) {
                T o = instantiate(xe);
                if (_rh != null) {
                    _rh.responded(o);
                }
                setState(State.SUCCEEDED, true);
            }

            @Override
            public void processError(Throwable se) {
                _thrown = se;
                if (_rh != null) {
                    _rh.responded(null);
                }
                setState(State.FAILED, true);
            }
        });
    }

    public int total() {
        return _total;
    }

    public int uploaded() {
        return _uploaded;
    }

    public LocalFile uploading() {
        return _uploading;
    }

    public State state() {
        return _state;
    }

    public void abort() {
        if (_sr != null) {
            _sr.abort();
        }
        _thrown = new Exception("Aborted.");
        if (_rh != null) {
            _rh.responded(null);
        }
        setState(State.ABORTED, true);
    }

    public Throwable thrown() {
        return _thrown;
    }

    public double progress() {
        State state = state();
        double progress = 0.0;
        double uploadProgress = uploadProgress();
        switch (state) {
        case INITIAL:
            progress = 0.0;
            break;
        case UPLOADING:
            progress = uploadProgress / 2.0;
            break;
        case CONSUMING:
            progress = 0.5;
            break;
        case SUCCEEDED:
            progress = 1.0;
            break;
        case ABORTED:
        case FAILED:
            if (uploadProgress < 1.0) {
                progress = uploadProgress;
            } else {
                progress = 0.5;
            }
            break;
        default:
            break;
        }
        return progress;
    }

    public String statusMessage() {

        State state = state();
        StringBuilder sb = new StringBuilder();
        switch (state) {
        case INITIAL:
            sb.append("pending");
            break;
        case UPLOADING:
            sb.append("uploading ");
            sb.append(uploaded() + 1);
            sb.append("/");
            sb.append(total());
            sb.append(" files: ");
            if (uploading() != null) {
                sb.append(uploading().name());
            }
            break;
        case CONSUMING:
            sb.append("executing " + consumeServiceName());
            break;
        case SUCCEEDED:
            sb.append("completed");
            break;
        case ABORTED:
            sb.append("aborted");
            break;
        case FAILED:
            sb.append("error occured");
            break;
        default:
            break;
        }
        return sb.toString();
    }

    public double uploadProgress() {
        if (total() == 0) {
            return 0.0;
        } else {
            return ((double) uploaded()) / ((double) total());
        }
    }

    protected abstract String consumeServiceName();

    protected abstract void consumeServiceArgs(XmlWriter w);

    protected abstract T instantiate(XmlElement xe);

    public boolean isFinished() {
        return _state == State.ABORTED || _state == State.FAILED || _state == State.SUCCEEDED;
    }

}

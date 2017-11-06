package daris.web.client.gui.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.file.FileFilter;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.file.FileHandler;
import arc.mf.client.file.LocalFile;
import arc.mf.client.file.LocalFile.Filter;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import daris.web.client.gui.Resource;
import daris.web.client.model.object.imports.FileEntry;
import daris.web.client.util.PathUtils;

public class FileForm extends ValidatedInterfaceComponent {
    public static final arc.gui.image.Image ICON_DIRECTORY = new arc.gui.image.Image(
            Resource.INSTANCE.folder16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_FILE = new arc.gui.image.Image(
            Resource.INSTANCE.document32().getSafeUri().asString(), 16, 16);

    private Map<String, FileEntry> _files;

    private VerticalPanel _vp;
    private ListGrid<FileEntry> _fileList;
    private HTML _fileListStatus;
    private int _addingFiles = 0;
    private FileFilter _fileFilter;
    private boolean _mandatory;

    public FileForm(boolean mandatory) {
        _mandatory = mandatory;
        _files = new TreeMap<String, FileEntry>();

        _vp = new VerticalPanel();
        _vp.setWidth100();
        _vp.setHeight(170);

        _fileList = new ListGrid<FileEntry>(ScrollPolicy.AUTO);
        _fileList.fitToParent();
        _fileList.setEmptyMessage("No files. Drag and drop files or directories here!");
        _fileList.enableDropTarget(false);
        _fileList.setDropHandler(new DropHandler() {

            @Override
            public DropCheck checkCanDrop(Object data) {
                if (data != null && (data instanceof LocalFile)) {
                    return DropCheck.CAN;
                }
                return DropCheck.CANNOT;
            }

            @Override
            public void drop(BaseWidget target, List<Object> data, DropListener dl) {
                if (data != null) {
                    for (Object o : data) {
                        if (o instanceof LocalFile) {
                            LocalFile f = (LocalFile) o;
                            if (f.isDirectory()) {
                                addDirectory(f, PathUtils.trimLeadingSlash(f.path()));
                            } else {
                                if (_fileFilter == null || _fileFilter.accept(f)) {
                                    addFile(f, f.name());
                                    updateFileList();
                                    notifyOfChangeInState();
                                }
                            }
                        }
                    }
                }
                dl.dropped(DropCheck.CAN);
            }
        });
        _fileList.addColumnDefn("isDir", null, null, (f, isDir) -> {
            return ((Boolean) isDir) ? new arc.gui.gwt.widget.image.Image(ICON_DIRECTORY)
                    : new arc.gui.gwt.widget.image.Image(ICON_FILE);
        }).setWidth(20);
        _fileList.addColumnDefn("path", "File").setWidth(800);
        _fileList.setMultiSelect(true);

        _vp.add(_fileList);

        _fileListStatus = new HTML();
        _fileListStatus.setWidth100();
        _fileListStatus.setHeight(20);
        _fileListStatus.setFontSize(10);
        _fileListStatus.setPaddingRight(15);
        _fileListStatus.setTextAlign(TextAlign.RIGHT);
        _fileListStatus.setPaddingTop(3);
        _fileListStatus.setBackgroundImage(new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
                ListGridHeader.HEADER_COLOUR_LIGHT, ListGridHeader.HEADER_COLOUR_DARK));
        _fileListStatus.setHTML("" + _files.size() + " files.");
        _vp.add(_fileListStatus);

        updateFileList();

    }

    public FileForm setFileFilter(FileFilter filter) {
        if (filter != null && !filter.equals(_fileFilter)) {
            _fileFilter = filter;
            if (!_files.isEmpty()) {
                boolean removed = false;
                Set<Map.Entry<String, FileEntry>> entries = _files.entrySet();
                for (Iterator<Map.Entry<String, FileEntry>> it = entries.iterator(); it.hasNext();) {
                    Map.Entry<String, FileEntry> entry = it.next();
                    if (!_fileFilter.accept(entry.getValue().file)) {
                        it.remove();
                        removed = true;
                    }
                }
                if (removed) {
                    updateFileList();
                }
            }
        } else {
            _fileFilter = filter;
        }
        return this;
    }

    private void updateFileList() {
        Collection<FileEntry> files = _files.values();
        List<ListGridEntry<FileEntry>> entries = new ArrayList<ListGridEntry<FileEntry>>();
        for (FileEntry file : files) {
            ListGridEntry<FileEntry> entry = new ListGridEntry<FileEntry>(file);
            entry.set("path", file.dstPath);
            entry.set("isDir", file.file.isDirectory());
            entries.add(entry);
        }
        _fileList.setData(entries);
        _fileListStatus.setHTML("" + files.size() + " files.");

    }

    private void addFile(LocalFile file, String dstPath) {
        if (_fileFilter == null || _fileFilter.accept(file)) {
            _files.put(dstPath, new FileEntry(file, dstPath));
        }
    }

    private void addDirectory(LocalFile dir, String base) {
        _addingFiles++;
        dir.files(Filter.FILES, 0, Integer.MAX_VALUE, new FileHandler() {
            @Override
            public void process(long start, long end, long total, List<LocalFile> files) {
                if (files != null && !files.isEmpty()) {
                    for (LocalFile file : files) {
                        if (!file.isDirectory()) {
                            addFile(file, base.isEmpty() ? file.name() : (base + "/" + file.name()));
                        }
                    }
                    updateFileList();
                }
                _addingFiles--;
                notifyOfChangeInState();
            }
        });
        _addingFiles++;
        dir.files(Filter.DIRECTORIES, 0, Integer.MAX_VALUE, new FileHandler() {
            @Override
            public void process(long start, long end, long total, List<LocalFile> files) {
                if (files != null && !files.isEmpty()) {
                    for (LocalFile file : files) {
                        if (file.isDirectory()) {
                            addDirectory(file, base.isEmpty() ? file.name() : (base + "/" + file.name()));
                        }
                    }
                }
                _addingFiles--;
                notifyOfChangeInState();
            }
        });
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_addingFiles > 0) {
                return new IsNotValid("Adding files... Please wait...");
            }
            if (_mandatory) {
                if (_files.isEmpty()) {
                    return new IsNotValid("No files added.");
                }
            }
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public BaseWidget widget() {
        return _vp;
    }

    public FileForm fitToParent() {
        _vp.fitToParent();
        return this;
    }

    public FileForm setHeight(int h) {
        _vp.setHeight(h);
        return this;
    }

    public FileForm setWidth(int w) {
        _vp.setWidth(w);
        return this;
    }

    public FileForm setWidth100() {
        _vp.setWidth100();
        return this;
    }

    public FileForm setHeight100() {
        _vp.setHeight100();
        return this;
    }

    public Map<String, FileEntry> files() {
        return _files;
    }

    public void setEmptyMessage(String message) {
        _fileList.setEmptyMessage(message);
    }
}

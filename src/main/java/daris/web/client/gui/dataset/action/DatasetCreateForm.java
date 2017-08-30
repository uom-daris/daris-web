package daris.web.client.gui.dataset.action;

import java.util.ArrayList;
import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.file.FileHandler;
import arc.mf.client.file.LocalFile;
import arc.mf.client.file.LocalFile.Filter;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.action.DObjectCreateForm;
import daris.web.client.model.dataset.DatasetCreator;
import daris.web.client.model.object.upload.FileEntry;
import daris.web.client.util.PathUtils;

public abstract class DatasetCreateForm<T extends DatasetCreator> extends DObjectCreateForm<T> {

    public static final arc.gui.image.Image ICON_DIRECTORY = new arc.gui.image.Image(
            Resource.INSTANCE.folder16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_FILE = new arc.gui.image.Image(
            Resource.INSTANCE.document32().getSafeUri().asString(), 16, 16);

    private ListGrid<FileEntry> _fileList;

    protected DatasetCreateForm(T dc) {
        super(dc);
    }

    protected void addToContainer(VerticalPanel container) {
        _fileList = new ListGrid<FileEntry>(ScrollPolicy.AUTO);
        initFileList();
        updateFileList();
        container.add(_fileList);
    }

    protected void addToInterfaceForm(Form interfaceForm) {

        super.addToInterfaceForm(interfaceForm);

        Field<String> ctype = new Field<String>(
                new FieldDefinition("Content Type", "ctype", new EnumerationType<String>(), null, null, 0, 1));
        ctype.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setContentType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        ctype.setInitialValue(this.creator.contentType(), false);
        interfaceForm.add(ctype);

    }

    private void initFileList() {

        _fileList.setHeight(150);
        _fileList.setWidth100();
        _fileList.setEmptyMessage("No files. Drag and drop files or directories here!");
        _fileList.enableDropTarget(false);
        _fileList.setDropHandler(new DropHandler() {

            @Override
            public DropCheck checkCanDrop(Object data) {
                if (data != null && (data instanceof LocalFile)) {
                    return DropCheck.CAN;
                } else {
                    return DropCheck.CANNOT;
                }
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
                                creator.addFile(f, f.name());
                                updateFileList();
                                notifyOfChangeInState();
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
    }

    private void addDirectory(LocalFile dir, String base) {
        dir.files(Filter.FILES, 0, Integer.MAX_VALUE, new FileHandler() {
            @Override
            public void process(long start, long end, long total, List<LocalFile> files) {
                if (files != null && !files.isEmpty()) {
                    for (LocalFile file : files) {
                        if (!file.isDirectory()) {
                            creator.addFile(file, base.isEmpty() ? file.name() : (base + "/" + file.name()));
                        }
                    }
                    updateFileList();
                    notifyOfChangeInState();
                }
            }
        });
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
            }
        });
    }

    private void updateFileList() {
        List<FileEntry> files = creator.files();
        List<ListGridEntry<FileEntry>> entries = new ArrayList<ListGridEntry<FileEntry>>();
        for (FileEntry file : files) {
            ListGridEntry<FileEntry> entry = new ListGridEntry<FileEntry>(file);
            entry.set("path", file.dstPath);
            entry.set("isDir", file.file.isDirectory());
            entries.add(entry);
        }
        _fileList.setData(entries);
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (!creator.hasFiles()) {
                return new IsNotValid("No files added.");
            }
        }
        return v;
    }

}

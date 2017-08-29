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
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.file.LocalFile;
import arc.mf.client.util.ActionListener;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.action.DObjectCreateForm;
import daris.web.client.model.dataset.DatasetCreator;

public abstract class DatasetCreateForm<T extends DatasetCreator> extends DObjectCreateForm<T> {

    public static final arc.gui.image.Image ICON_DIRECTORY = new arc.gui.image.Image(
            Resource.INSTANCE.folder16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_FILE = new arc.gui.image.Image(
            Resource.INSTANCE.document32().getSafeUri().asString(), 16, 16);

    private ListGrid<LocalFile> _fileList;

    protected DatasetCreateForm(T dc) {
        super(dc);

        VerticalPanel fileVP = new VerticalPanel();
        fileVP.setHeight(150);
        fileVP.setWidth100();

        HTML fileTitle = new HTML("Content files");
        fileTitle.setHeight(22);
        fileTitle.setWidth100();
        fileVP.add(fileTitle);

        _fileList = new ListGrid<LocalFile>(ScrollPolicy.AUTO);
        initFileList();
        updateFileList();
        fileVP.add(_fileList);

        this.container.add(fileVP);

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

        _fileList.fitToParent();
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
                boolean dropped = false;
                if (data != null) {
                    for (Object o : data) {
                        if (o instanceof LocalFile) {
                            LocalFile f = (LocalFile) o;
                            dropped = creator.addFile(f);
                        }
                    }
                }
                if (dropped) {
                    updateFileList();
                    dl.dropped(DropCheck.CAN);
                } else {
                    dl.dropped(DropCheck.CANNOT);
                }
            }
        });
        _fileList.addColumnDefn("isDir", null, null, (f, isDir) -> {
            return ((Boolean) isDir) ? new arc.gui.gwt.widget.image.Image(ICON_DIRECTORY)
                    : new arc.gui.gwt.widget.image.Image(ICON_FILE);
        }).setWidth(20);
        _fileList.addColumnDefn("path", "File/Directory Path").setWidth(380);
    }

    private void updateFileList() {
        List<LocalFile> files = creator.files();
        List<ListGridEntry<LocalFile>> entries = new ArrayList<ListGridEntry<LocalFile>>();
        for (LocalFile file : files) {
            ListGridEntry<LocalFile> entry = new ListGridEntry<LocalFile>(file);
            entry.set("path", file.path());
            entry.set("isDir", file.isDirectory());
            entries.add(entry);
        }
        _fileList.setData(entries);
    }

}

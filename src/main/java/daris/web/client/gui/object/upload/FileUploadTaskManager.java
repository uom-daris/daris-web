package daris.web.client.gui.object.upload;

import java.util.ArrayList;
import java.util.List;

import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.table.Table.Row;
import arc.gui.gwt.widget.window.Window;
import arc.gui.window.WindowProperties;
import daris.web.client.gui.widget.ProgressBar;
import daris.web.client.model.object.upload.FileUploadTask;
import daris.web.client.model.object.upload.FileUploadTaskMonitor;

public class FileUploadTaskManager implements FileUploadTaskMonitor, FileUploadTask.Listener {

    private List<FileUploadTask<?>> _tasks;

    private VerticalPanel _vp;

    private ListGrid<FileUploadTask<?>> _taskList;

    private SimplePanel _bbSP;

    private arc.gui.gwt.widget.window.Window _win;

    private FileUploadTaskManager() {
        _vp = new VerticalPanel();
        _vp.fitToParent();

        _taskList = new ListGrid<FileUploadTask<?>>(ScrollPolicy.AUTO);
        initTaskList();
        _vp.add(_taskList);

        _bbSP = new SimplePanel();
        _bbSP.setHeight(32);
        _bbSP.setWidth100();
        _vp.add(_bbSP);

        updateButtons();

    }

    private void initTaskList() {
        _taskList.fitToParent();
        _taskList.setEmptyMessage("No upload tasks.");
        _taskList.addColumnDefn("id", "ID");
        _taskList.addColumnDefn("name", "Name");
        _taskList.addColumnDefn("progress", "Progress", null, (task, progress) -> {
            FileUploadTask<?> t = (FileUploadTask<?>) task;
            return new ProgressBar().setProgress(t.progress(), t.statusMessage());
        }).setFixedWidth(500);;
        _taskList.setMultiSelect(true);
        _taskList.setSelectionHandler(new SelectionHandler<FileUploadTask<?>>() {

            @Override
            public void selected(FileUploadTask<?> o) {
                updateButtons();
            }

            @Override
            public void deselected(FileUploadTask<?> o) {
                updateButtons();
            }
        });
    }

    @Override
    public void startMonitor(FileUploadTask<?> task) {
        addTask(task);
    }

    private void addTask(FileUploadTask<?> task) {
        if (task != null) {
            if (_tasks == null) {
                _tasks = new ArrayList<FileUploadTask<?>>();
            }
            task.addListener(this);
            _tasks.add(task);
            updateTaskList();
            updateButtons();
        }
    }

    private void updateTaskList() {
        List<ListGridEntry<FileUploadTask<?>>> entries = new ArrayList<ListGridEntry<FileUploadTask<?>>>();
        if (_tasks != null) {
            for (FileUploadTask<?> task : _tasks) {
                ListGridEntry<FileUploadTask<?>> entry = new ListGridEntry<FileUploadTask<?>>(task);
                entry.set("id", task.id());
                entry.set("name", task.name());
                entry.set("progress", task.progress());
                entries.add(entry);
            }
        }
        _taskList.setData(entries);
    }

    private void updateButtons() {
        _bbSP.clear();
        ButtonBar bb = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.RIGHT);
        List<FileUploadTask<?>> selectedTasks = _taskList.selections();
        if (selectedTasks != null && !selectedTasks.isEmpty()) {
            List<FileUploadTask<?>> failed = new ArrayList<FileUploadTask<?>>();
            List<FileUploadTask<?>> aborted = new ArrayList<FileUploadTask<?>>();
            List<FileUploadTask<?>> succeeded = new ArrayList<FileUploadTask<?>>();
            List<FileUploadTask<?>> uploading = new ArrayList<FileUploadTask<?>>();
            List<FileUploadTask<?>> consuming = new ArrayList<FileUploadTask<?>>();
            List<FileUploadTask<?>> initial = new ArrayList<FileUploadTask<?>>();
            for (FileUploadTask<?> task : selectedTasks) {
                switch (task.state()) {
                case INITIAL:
                    initial.add(task);
                    break;
                case UPLOADING:
                    uploading.add(task);
                    break;
                case CONSUMING:
                    consuming.add(task);
                    break;
                case ABORTED:
                    aborted.add(task);
                    break;
                case FAILED:
                    failed.add(task);
                    break;
                case SUCCEEDED:
                    succeeded.add(task);
                    break;
                default:
                    break;
                }
                if (initial.size() > 0 || uploading.size() > 0 || consuming.size() > 0) {
                    bb.addButton("Abort").addClickHandler(e -> {
                        for (FileUploadTask<?> t : initial) {
                            t.abort();
                        }
                        for (FileUploadTask<?> t : uploading) {
                            t.abort();
                        }
                        for (FileUploadTask<?> t : initial) {
                            t.abort();
                        }
                    });
                }
                if (aborted.size() > 0 || failed.size() > 0 || succeeded.size() > 0) {
                    bb.addButton("Remove").addClickHandler(e -> {
                        _tasks.removeAll(aborted);
                        _tasks.removeAll(failed);
                        _tasks.removeAll(succeeded);
                        updateTaskList();
                    });
                }
            }
        }
        bb.addButton("Dismiss").addClickHandler(e -> {
            hide();
        });
        _bbSP.setContent(bb);
    }

    public void show(arc.gui.window.Window owner) {
        if (_win != null && _win.isShowing()) {
            return;
        }
        WindowProperties wp = new WindowProperties();
        wp.setOwnerWindow(owner);
        wp.setCanBeClosed(false);
        wp.setCanBeMaximised(false);
        wp.setCanBeMoved(true);
        wp.setCanBeResized(true);
        wp.setCenterInPage(true);
        wp.setModal(false);
        wp.setShowHeader(true);
        wp.setSize(0.5, 0.5);
        wp.setTitle("Upload Tasks");
        _win = Window.create(wp);
        _win.setContent(_vp);
        _win.centerInPage();
        _win.show();
    }

    public void hide() {
        if (_win != null) {
            if (_win.isShowing()) {
                _win.hide();
            }
            _win = null;
        }
    }

    private static FileUploadTaskManager _instance;

    public static FileUploadTaskManager get() {
        if (_instance == null) {
            _instance = new FileUploadTaskManager();
        }
        return _instance;
    }

    @Override
    public void updated(FileUploadTask<?> task) {
        Row row = _taskList.rowFor(task);
        if (row != null) {
            ProgressBar pb = (ProgressBar) row.cell(2).widget();
            pb.setProgress(task.progress(), task.statusMessage());
        }
    }

}

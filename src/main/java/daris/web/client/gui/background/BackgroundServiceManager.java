package daris.web.client.gui.background;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;

import arc.gui.dialog.DialogProperties;
import arc.gui.dialog.DialogProperties.Type;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.input.TextArea;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.window.Window;
import arc.gui.gwt.widget.window.WindowCloseListener;
import arc.gui.window.WindowProperties;
import arc.mf.client.Output;
import arc.mf.client.task.Task.State;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.model.service.BackgroundService;
import arc.mf.model.service.BackgroundServiceSet;
import arc.mf.object.CollectionResolveHandler;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.util.DateTimeUtil;

public class BackgroundServiceManager {

    private BackgroundServiceSet _services;
    private Timer _timer;

    private VerticalPanel _vp;
    private ListGrid<BackgroundService> _list;
    private SimplePanel _bbSP;

    private arc.gui.gwt.widget.window.Window _win;

    private BackgroundService _selected;

    public BackgroundServiceManager() {
        _services = new BackgroundServiceSet();

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<BackgroundService>(ScrollPolicy.AUTO) {
            @Override
            protected void preLoad() {
                _selected = (_list.selections() != null && !_list.selections().isEmpty()) ? _list.selections().get(0)
                        : null;
            }

            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<BackgroundService>> entries) {
                if (entries != null && !entries.isEmpty()) {
                    if (_selected == null) {
                        select(entries.size() - 1);
                    } else {
                        for (int i = 0; i < entries.size(); i++) {
                            if (entries.get(i).data().id() == _selected.id()) {
                                select(i);
                                break;
                            }
                        }
                    }
                } else {
                    updateButtonBar(null);
                }
            }
        };
        _list.fitToParent();
        _list.setCursorSize(Integer.MAX_VALUE);
        _list.setDataSource(new DataSource<ListGridEntry<BackgroundService>>() {

            @Override
            public boolean isRemote() {
                return true;
            }

            @Override
            public boolean supportCursor() {
                return false;
            }

            @Override
            public void load(Filter f, final long start, final long end,
                    final DataLoadHandler<ListGridEntry<BackgroundService>> lh) {
                _services.reset();
                _services.resolve(start, end, new CollectionResolveHandler<BackgroundService>() {

                    @Override
                    public void resolved(List<BackgroundService> bss) throws Throwable {
                        if (bss != null && !bss.isEmpty()) {
                            List<ListGridEntry<BackgroundService>> es = new ArrayList<ListGridEntry<BackgroundService>>();
                            for (BackgroundService bs : bss) {
                                ListGridEntry<BackgroundService> e = new ListGridEntry<BackgroundService>(bs);
                                e.set("id", bs.id());
                                e.set("name", bs.name());
                                e.set("description", bs.description());
                                e.set("state", bs.state());
                                e.set("startTime", bs.startTime());
                                e.set("endTime", bs.endTime());
                                es.add(e);
                            }
                            lh.loaded(start, end, es.size(), es, DataLoadAction.REPLACE);
                            return;
                        }
                        lh.loaded(0, 0, 0, null, null);
                    }
                });
            }
        });
        _list.setEmptyMessage("No background tasks.");
        _list.addColumnDefn("id", "Task id").setWidth(60);
        _list.addColumnDefn("name", "Name").setWidth(120);
        _list.addColumnDefn("description", "Description").setWidth(180);
        _list.addColumnDefn("state", "State", null, new WidgetFormatter<BackgroundService, BackgroundService.State>() {

            @Override
            public BaseWidget format(BackgroundService bs, State state) {
                return stateGuiFor(state);
            }
        }).setWidth(110);
        // @formatter:off
//        _list.addColumnDefn("state", "Progress", null,
//                new WidgetFormatter<BackgroundService, State>() {
//
//                    @Override
//                    public BaseWidget format(BackgroundService bs,
//                            State state) {
//                        return progressGuiFor(bs);
//                    }
//                }).setWidth(300);
        // @formatter:on
        _list.addColumnDefn("state", "Execution Time", null,
                new WidgetFormatter<BackgroundService, BackgroundService.State>() {

                    @Override
                    public BaseWidget format(BackgroundService bso, State state) {
                        if (bso.executionTime() > 0) {
                            return new HTML(DateTimeUtil.convertSecondsToHumanReadableTime(bso.executionTime()));
                        }
                        return null;
                    }
                }).setWidth(120);
        _list.addColumnDefn("state", "Message", null, new WidgetFormatter<BackgroundService, State>() {

            @Override
            public BaseWidget format(BackgroundService bs, State state) {
                return messageGuiFor(_win, bs);
            }
        }).setWidth(350);
        _list.addColumnDefn("startTime", "Start time").setWidth(150);
        _list.addColumnDefn("endTime", "End time").setWidth(150);
        _list.setMultiSelect(false);
        _list.setSelectionHandler(new SelectionHandler<BackgroundService>() {

            @Override
            public void selected(BackgroundService o) {
                _selected = o;
                updateButtonBar(o);
            }

            @Override
            public void deselected(BackgroundService o) {
                _selected = null;
                updateButtonBar(null);
            }
        });
        _vp.add(_list);

        _bbSP = new SimplePanel();
        _bbSP.setWidth100();
        _bbSP.setHeight(30);

        _vp.add(_bbSP);

        _timer = new Timer() {

            @Override
            public void run() {
                _list.refresh();
            }
        };
        _timer.scheduleRepeating(2000);
    }

    public void show(arc.gui.window.Window owner) {
        WindowProperties wp = new WindowProperties();
        wp.setTitle("Background tasks");
        wp.setSize(0.6, 0.6);
        wp.setCanBeResized(true);
        wp.setCanBeMoved(true);
        wp.setCanBeClosed(true);
        wp.setCenterInPage(true);
        _win = Window.create(wp);
        _win.setContent(_vp);
        _win.addCloseListener(new WindowCloseListener() {

            @Override
            public void closed(Window w) {
                _timer.cancel();
            }
        });
        _win.centerInPage();
        _win.show();
    }

    private void updateButtonBar(final BackgroundService bs) {
        ButtonBar bb = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.CENTER);
        if (bs != null) {
            if (!bs.finished() && bs.canAbort()) {
                bb.addButton("Abort").addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        bs.abort();
                    }
                });
            }
            if (bs.canSuspend()) {
                bb.addButton("Suspend").addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        bs.suspend();
                    }
                });
            }
            if (bs.finished()) {
                bb.addButton("Delete").addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
                        bs.destroy();
                    }
                });
            }

        }
        if (_services.totalNumberOfMembers() > 0) {
            bb.addButton("Clear").addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    XmlStringWriter w = new XmlStringWriter();
                    w.add("include", "completed");
                    w.add("include", "aborted");
                    w.add("include", "failed");
                    Session.execute("service.background.destroy", w.document(), new ServiceResponseHandler() {

                        @Override
                        public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                            // DO NOTHING..
                        }
                    });
                }
            });
        }
        bb.addButton("Dismiss").addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (_win != null) {
                    _win.close();
                }
            }
        });
        _bbSP.setContent(bb);
    }

    private static void displayError(arc.gui.window.Window owner, BackgroundService bs) {
        TextArea ta = new TextArea();
        ta.fitToParent();
        ta.setReadOnly(true);
        ta.setValue(bs.error());
        StringBuilder title = new StringBuilder("Background service error");
        title.append("[");
        if (bs.name() != null) {
            title.append("name: ").append(bs.name()).append("; ");
        }
        title.append("task_id: ").append(bs.id()).append("]");
        DialogProperties dp = new DialogProperties(Type.ERROR, title.toString(), ta);
        dp.setCancelLabel(null); // no cancel button;
        dp.setButtonLabel("Dismiss");
        dp.setSize(800, 500);
        Dialog.postDialog(dp, null).show();
    }

    static BaseWidget stateGuiFor(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("<img style=\"vertical-align:middle\" src=\"")
                .append(BackgroundServiceMonitor.iconForState(state).path()).append("\">").append(state);
        HTML html = new HTML(sb.toString());
        html.setFontSize(11);
        return html;
    }

    // @formatter:off
//    static BaseWidget progressGuiFor(BackgroundService bso) {
//
//        ProgressBar pb = new ProgressBar();
//        pb.setWidth(280);
//        if (bso.state() == State.EXECUTING) {
//            String execTime = "[" + DateTimeUtil.convertSecondsToHumanReadableTime(bso.executionTime()) + "]";
//            if (bso.numberSubOperationsCompleted() > 0 && bso.totalOperations() > 0) {
//                double progress = (double) bso.subOperationsCompleted() / (double) bso.totalOperations();
//                int percent = (int) (progress * 100);
//                pb.setProgress(bso.numberSubOperationsCompleted(), bso.totalOperations(),
//                        percent + "%" + " " + execTime);
//            } else {
//                pb.setMessage(execTime);
//            }
//        } else {
//            if (bso.state() == State.COMPLETED) {
//                pb.setProgress(100, 100, bso.state().name().toLowerCase());
//            } else {
//                pb.setMessage(bso.state().name().toLowerCase());
//            }
//        }
//        return pb;
//    }
    // @formatter:on

    static BaseWidget messageGuiFor(final arc.gui.window.Window owner, final BackgroundService bs) {

        if (bs.error() != null) {
            Button errorButton = new Button("Error");
            errorButton.setColour(RGB.RED);
            errorButton.setFontSize(11);
            errorButton.setToolTip("Click to see error details.");
            errorButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    displayError(owner, bs);
                }
            });
            return errorButton;
        } else if (bs.currentActivity() != null) {
            HTML activityMsg = new HTML(bs.currentActivity());
            activityMsg.setFontSize(11);
            return activityMsg;
        }
        return null;
    }

}

package daris.web.client.gui.background;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.button.ButtonBar.Alignment;
import arc.gui.gwt.widget.button.ButtonBar.Position;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.window.Window;
import arc.gui.gwt.widget.window.WindowCloseListener;
import arc.gui.window.WindowProperties;
import arc.mf.client.task.Task.State;
import arc.mf.client.util.DateTime;
import arc.mf.model.service.BackgroundService;
import arc.mf.model.service.messages.DestroyRequestResponse;
import daris.web.client.gui.Resource;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.util.DateTimeUtil;

public class BackgroundServiceMonitor implements arc.mf.model.service.BackgroundServiceMonitorHandler {

    public static final arc.gui.image.Image ICON_PENDING = new arc.gui.image.Image(
            Resource.INSTANCE.waiting16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_EXECUTING = new arc.gui.image.Image(
            Resource.INSTANCE.loading16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_SUSPENDED = new arc.gui.image.Image(
            Resource.INSTANCE.suspended16().getSafeUri().asString(), 16, 16);
    private static final arc.gui.image.Image ICON_ABORT_PENDING = new arc.gui.image.Image(
            Resource.INSTANCE.alert16().getSafeUri().asString(), 16, 16);
    private static final arc.gui.image.Image ICON_ABORTED = new arc.gui.image.Image(
            Resource.INSTANCE.alert16().getSafeUri().asString(), 16, 16);
    private static final arc.gui.image.Image ICON_COMPLETED = new arc.gui.image.Image(
            Resource.INSTANCE.check16().getSafeUri().asString(), 16, 16);
    private static final arc.gui.image.Image ICON_STATE_FAILED_WILL_RETRY = new arc.gui.image.Image(
            Resource.INSTANCE.alert16().getSafeUri().asString(), 16, 16);
    private static final arc.gui.image.Image ICON_FAILED = new arc.gui.image.Image(
            Resource.INSTANCE.error16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image LOADING_BAR = new arc.gui.image.Image(
            Resource.INSTANCE.loadingBar().getSafeUri().asString(), 128, 15);
    public static final arc.gui.image.Image LOADING_BAR_PENDING = new arc.gui.image.Image(
            Resource.INSTANCE.loadingBarPending().getSafeUri().asString(), 128, 15);
    public static final arc.gui.image.Image LOADING_BAR_ABORTED = new arc.gui.image.Image(
            Resource.INSTANCE.loadingBarAborted().getSafeUri().asString(), 128, 15);
    public static final arc.gui.image.Image LOADING_BAR_COMPLETED = new arc.gui.image.Image(
            Resource.INSTANCE.loadingBarCompleted().getSafeUri().asString(), 128, 15);

    private long _id;
    private arc.mf.model.service.BackgroundServiceMonitor _m;

    private SimplePanel _sp;
    private Window _win;

    public BackgroundServiceMonitor(long id) {
        _id = id;

        _sp = new SimplePanel();
        _sp.fitToParent();

        _m = new arc.mf.model.service.BackgroundServiceMonitor(id, this);
        _m.execute(1000);
    }

    public void show(arc.gui.window.Window owner) {
        if (_win != null) {
            if (!_win.isShowing()) {
                _win.show();
                _win.centerInPage();
            }
            return;
        }
        WindowProperties wp = new WindowProperties();
        wp.setTitle("Background task " + _id);
        wp.setCanBeMaximised(false);
        wp.setCanBeClosed(true);
        wp.setCanBeResized(true);
        wp.setCanBeMoved(true);
        wp.setOwnerWindow(owner);
        wp.setCenterInPage(true);
        wp.setSize(480, 320);

        _win = Window.create(wp);
        _win.addCloseListener(new WindowCloseListener() {

            @Override
            public void closed(Window w) {
                _m.cancel();
            }
        });
        _win.setContent(_sp);
        _win.show();
        _win.centerInPage();
    }

    @Override
    public void checked(final BackgroundService bso) {

        if (bso == null) {
            _sp.clear();
            return;
        }

        if (_win != null) {
            if (bso.name() != null) {
                _win.setTitle("Background task " + bso.id() + " - " + bso.name());
            } else if (bso.key() != null) {
                _win.setTitle("Background task " + bso.id() + " - " + bso.key());
            } else {
                _win.setTitle("Background task " + bso.id());
            }
        }

        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();

        HTML detail = new HTML(detailHtmlFor(bso));
        detail.fitToParent();
        detail.setPaddingTop(5);
        detail.setMargin(20);
        detail.setFontSize(11);
        detail.setBorder(1, RGB.GREY_EEE);
        vp.add(detail);

        // @formatter:off
//        if (bso.numberSubOperationsCompleted() > 0 && bso.totalOperations() > 0) {
//            ProgressBar pb = new ProgressBar();
//            pb.setWidth100();
//            pb.setHeight(18);
//            vp.add(pb);
//            pb.setMarginLeft(20);
//            pb.setMarginRight(20);
//            pb.setMarginBottom(20);
//            long completed = bso.numberSubOperationsCompleted();
//            long total = bso.totalOperations();
//            if (completed > total) {
//                completed = total;
//            }
//            pb.setProgress(completed, total, "" + completed + " / " + total);
//        }
        // @formatter:on
        AbsolutePanel progressAP = new AbsolutePanel();
        progressAP.setHeight(40);
        progressAP.setWidth100();
        vp.add(progressAP);
        progressAP.add(progressImageFor(bso));

        ButtonBar bb = buttonBarFor(bso);
        vp.add(bb);

        _sp.setContent(vp);

        if (bso.finished()) {
            _m.cancel();
        }
    }

    private static String detailHtmlFor(BackgroundService bs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style=\"width:100%;\">");
        sb.append(
                "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Task id:</td><td style=\"text-align:left;\">")
                .append(bs.id()).append("</td></tr>");
        sb.append(
                "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Name:</td><td style=\"text-align:left;\">")
                .append(bs.name()).append("</td></tr>");
        if (bs.description() != null) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Description:</td><td style=\"text-align:left;\">")
                    .append(bs.description()).append("</td></tr>");
        }
        sb.append(
                "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">State:</td><td style=\"text-align:left;\">")
                .append("<img style=\"vertical-align:middle\" src=\"").append(iconForState(bs.state()).path())
                .append("\">").append(bs.state()).append("</td></tr>");
        if (bs.startTime() != null) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Start time:</td><td style=\"text-align:left;\">")
                    .append(DateTime.dateTimeAsClientString(bs.startTime())).append("</td></tr>");
        }
        if (bs.state() == State.EXECUTING && bs.executionTime() >= 0) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Duration:</td><td style=\"text-align:left;\">")
                    .append(DateTimeUtil.convertSecondsToHumanReadableTime(bs.executionTime())).append("</td></tr>");

        }
        if (bs.endTime() != null) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">End time:</td><td style=\"text-align:left;\">")
                    .append(DateTime.dateTimeAsClientString(bs.endTime())).append("</td></tr>");
        }
        if (bs.state() == State.EXECUTING && bs.currentActivity() != null) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Current activity:</td><td style=\"text-align:left;\">")
                    .append(bs.currentActivity()).append("</td></tr>");
        }
        // @formatter:off
//        if (bs.numberSubOperationsCompleted() > 0 && bs.totalOperations() > 0) {
//            long completed = bs.numberSubOperationsCompleted();
//            long total = bs.totalOperations();
//            if (completed > total) {
//                completed = total;
//            }
//            sb.append(
//                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Completed:</td><td style=\"text-align:left;\">")
//                    .append(completed).append("/").append(total).append("</td></tr>");
//        }
        // @formatter:on
        if (bs.error() != null) {
            sb.append(
                    "<tr><td style=\"width:25%;text-align:right;font-weight:bold;\">Error:</td><td style=\"text-align:left;\">")
                    .append("<textarea readonly style=\"width:100%;box-sizing:border-box;\">");
            sb.append(bs.error());
            if (bs.failureStackTraces() != null && !bs.failureStackTraces().isEmpty()) {
                sb.append("\nStack Trace:");
                for (String st : bs.failureStackTraces()) {
                    sb.append(st).append("\n");
                }
            }
            sb.append("</textarea></td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static arc.gui.gwt.widget.image.Image progressImageFor(BackgroundService bs) {
        arc.gui.image.Image i = null;
        switch (bs.state()) {
        case PENDING:
            i = LOADING_BAR_PENDING;
            break;
        case EXECUTING:
            i = LOADING_BAR;
            break;
        case COMPLETED:
            i = LOADING_BAR_COMPLETED;
            break;
        default:
            i = LOADING_BAR_ABORTED;
            break;
        }
        arc.gui.gwt.widget.image.Image img = new arc.gui.gwt.widget.image.Image(i, 256, 15);
        img.setPosition(Style.Position.ABSOLUTE);
        img.setTop(0);
        img.setBottom(0);
        img.setRight(0);
        img.setLeft(0);
        img.element().getStyle().setProperty("margin", "auto");
        return img;
    }

    private ButtonBar buttonBarFor(final BackgroundService bs) {
        ButtonBar bb = new ButtonBar(Position.BOTTOM, Alignment.CENTER);
        if (!bs.finished() && bs.canAbort()) {
            bb.addButton("Abort").addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    bs.abort();
                }
            });
        }
        if (!bs.finished() && bs.canSuspend()) {
            bb.addButton("Suspend").addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    bs.suspend();
                }
            });
        }
        if (bs.state() == State.SUSPENDED) {
            bb.addButton("Resume").addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    bs.resume();
                }
            });
        }
        if (bs.finished()) {
            bb.addButton("Delete").addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    bs.destroy(new DestroyRequestResponse() {

                        @Override
                        public void destroyRequested() {
                            if (_win != null) {
                                _win.close();
                            }
                            MessageBox.show(250, 22, _sp, MessageBox.Position.CENTER,
                                    "Background service " + _id + " has been deleted.", 3000);
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
        return bb;
    }

    static arc.gui.image.Image iconForState(State state) {
        switch (state) {
        case PENDING:
            return ICON_PENDING;
        case EXECUTING:
            return ICON_EXECUTING;
        case SUSPENDED:
            return ICON_SUSPENDED;
        case ABORT_PENDING:
            return ICON_ABORT_PENDING;
        case ABORTED:
            return ICON_ABORTED;
        case COMPLETED:
            return ICON_COMPLETED;
        case STATE_FAILED_WILL_RETRY:
            return ICON_STATE_FAILED_WILL_RETRY;
        case FAILED:
            return ICON_FAILED;
        default:
            return null;
        }
    }

}

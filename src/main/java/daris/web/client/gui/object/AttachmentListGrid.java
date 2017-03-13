package daris.web.client.gui.object;

import java.util.List;
import java.util.Vector;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.button.ButtonBar.Alignment;
import arc.gui.gwt.widget.button.ButtonBar.Position;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.tip.ToolTip;
import arc.gui.gwt.widget.tip.ToolTipHandler;
import arc.mf.client.file.LocalFile;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.gui.Resource;
import daris.web.client.gui.util.ButtonUtil;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.model.object.Attachment;
import daris.web.client.model.object.AttachmentRef;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.messages.ObjectAttach;
import daris.web.client.model.object.messages.ObjectAttachmentGet;
import daris.web.client.model.object.messages.ObjectAttachmentList;
import daris.web.client.model.object.messages.ObjectDetach;

public class AttachmentListGrid extends ContainerWidget {

    public static final arc.gui.image.Image ICON_ADD = new arc.gui.image.Image(
            Resource.INSTANCE.add16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_DOWNLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.download16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_REMOVE = new arc.gui.image.Image(
            Resource.INSTANCE.sub16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_CLEAR = new arc.gui.image.Image(
            Resource.INSTANCE.cross16().getSafeUri().asString(), 12, 12);

    public static final int FONT_SIZE = 11;

    public static final int MIN_ROW_HEIGHT = 22;

    private DObject _o;
    private List<Attachment> _data;

    private VerticalPanel _vp;
    private ListGrid<Attachment> _list;
    private int _nbe = 0;
    private SimplePanel _bbSP;

    public AttachmentListGrid(DObject o) {
        _o = o;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<Attachment>(ScrollPolicy.AUTO) {
            protected void postLoad(long start, long end, long total, List<ListGridEntry<Attachment>> entries) {
                if (entries == null) {
                    _nbe = 0;
                } else {
                    _nbe = entries.size();
                }
                if (_o.editable()) {
                    updateButtons();
                }
            }
        };
        _list.fitToParent();
        _vp.add(_list);

        initListGrid();

        if (_o.editable()) {
            HTML message = new HTML("To add attachments, drag local files and drop into above area...");
            message.setFontFamily(DStyles.FONT_FAMILY);
            message.setFontSize(10);
            message.setTextAlign(TextAlign.CENTER);
            message.setVerticalAlign(VerticalAlign.MIDDLE);
            message.setWidth100();
            message.setHeight(20);
            message.element().getStyle().setLineHeight(20, Unit.PX);
            message.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_EEE);
            message.setTextShadow(1, 1, 0, RGB.GREY_EEE);
            _vp.add(message);

            _bbSP = new SimplePanel();
            _bbSP.setHeight(32);
            _bbSP.setWidth100();
            _vp.add(_bbSP);
        }

        initWidget(_vp);

    }

    private void initListGrid() {
        _list.setDataSource(new DataSource<ListGridEntry<Attachment>>() {

            @Override
            public boolean isRemote() {
                return true;
            }

            @Override
            public boolean supportCursor() {
                return false;
            }

            @Override
            public void load(Filter f, long start, long end, DataLoadHandler<ListGridEntry<Attachment>> lh) {
                new ObjectAttachmentList(_o.citeableId()).send(new ObjectMessageResponse<List<Attachment>>() {

                    @Override
                    public void responded(List<Attachment> as) {
                        _data = as;
                        if (as != null && !as.isEmpty()) {
                            List<ListGridEntry<Attachment>> es = new Vector<ListGridEntry<Attachment>>(as.size());
                            for (Attachment a : as) {
                                if (f == null || f.matches(a)) {
                                    ListGridEntry<Attachment> e = new ListGridEntry<Attachment>(a);
                                    e.set("assetId", a.assetId());
                                    e.set("name", a.name());
                                    e.set("extension", a.extension());
                                    e.set("mimeType", a.mimeType());
                                    e.set("size", a.humanReadableSize());
                                    es.add(e);
                                }
                            }
                            int total = es.size();
                            int start1 = start < 0 ? 0 : (start > total ? total : (int) start);
                            int end1 = end > total ? total : (int) end;
                            if (start1 < 0 || end1 > total || start1 > end) {
                                lh.loaded(start, end, total, null, null);
                            } else {
                                es = es.subList(start1, end1);
                                lh.loaded(start1, end1, total, es, DataLoadAction.REPLACE);
                            }
                            return;
                        }
                        lh.loaded(0, 0, 0, null, null);
                    }
                });
            }
        });

        WidgetFormatter<Attachment, String> formatter = (attachment, str) -> {
            HTML html = new HTML(str);
            html.setFontFamily(DStyles.FONT_FAMILY);
            html.setFontSize(FONT_SIZE);
            html.setVerticalAlign(VerticalAlign.MIDDLE);
            return html;
        };

        _list.addColumnDefn("name", "Name", "Attachment file name.", formatter).setWidth(200);
        _list.addColumnDefn("size", "Size", "Attachment file size.", formatter).setWidth(200);
        _list.addColumnDefn("mimeType", "MIME Type", "MIME type", formatter).setWidth(200);
        _list.setMultiSelect(true);
        _list.setMinRowHeight(MIN_ROW_HEIGHT);
        _list.setEmptyMessage("");
        _list.setLoadingMessage("Loading attachments ...");
        _list.setCursorSize(Integer.MAX_VALUE);
        _list.setRowToolTip(new ToolTip<Attachment>() {

            @Override
            public void generate(Attachment a, ToolTipHandler th) {
                th.setTip(new HTML(a.toHTML()));
            }
        });

        _list.setSelectionHandler(new SelectionHandler<Attachment>() {

            @Override
            public void selected(Attachment a) {
                updateButtons();
            }

            @Override
            public void deselected(Attachment o) {
                updateButtons();
            }
        });
        _list.enableDropTarget(false);

        if (_o.editable()) {
            _list.setDropHandler(new DropHandler() {

                @Override
                public DropCheck checkCanDrop(Object object) {
                    if (object != null && object instanceof LocalFile) {
                        if (((LocalFile) object).isFile()) {
                            return DropCheck.CAN;
                        }
                    }
                    return DropCheck.CANNOT;
                }

                @Override
                public void drop(BaseWidget target, List<Object> objects, DropListener dl) {

                    if (objects == null || objects.isEmpty()) {
                        dl.dropped(DropCheck.CANNOT);
                        return;
                    }
                    dl.dropped(DropCheck.CAN);
                    for (Object object : objects) {
                        new ObjectAttach(_o.citeableId(), (LocalFile) object)
                                .send(new ObjectMessageResponse<AttachmentRef>() {
                                    @Override
                                    public void responded(AttachmentRef attachment) {
                                        _list.refresh();
                                    }
                                });
                    }
                }
            });
        }

    }

    private void updateButtons() {
        _bbSP.clear();
        if (_nbe <= 0) {
            return;
        }

        ButtonBar bb = new ButtonBar(Position.BOTTOM, Alignment.CENTER);
        bb.setHeight(32);
        _bbSP.setContent(bb);
        if (_data != null && !_data.isEmpty()) {
            Button downloadButton = ButtonUtil.createButton(ICON_DOWNLOAD, "Download",
                    "Download " + ((_list.selections() != null && !_list.selections().isEmpty()) ? "selected" : "all")
                            + " attachments.",
                    true);
            downloadButton.setWidth(100);
            downloadButton.addClickHandler(e -> {
                new ObjectAttachmentGet(_o.citeableId(), _list.selections()).send();
            });
            bb.add(downloadButton);
        }
        if (_list.haveSelections()) {
            Button removeButton = ButtonUtil.createButton(ICON_REMOVE, "Remove", "Remove selected attachments", true);
            removeButton.setWidth(100);
            removeButton.addClickHandler(e -> {
                removeSelectedAttachments();
            });
            bb.add(removeButton);
        }
        Button clearButton = ButtonUtil.createButton(ICON_CLEAR, "Remove all", "Remove attachments...", true);
        clearButton.setWidth(100);
        clearButton.addClickHandler(e -> {
            removeAllAttachments();
        });
        bb.add(clearButton);
    }

    private void removeSelectedAttachments() {

        if (_list.haveSelections()) {
            Dialog.confirm(_vp.window(), "Remove attachment",
                    "Are you sure you want to remove the selected attachments?", executed -> {
                        if (executed) {
                            new ObjectDetach(_o.citeableId(), _list.selections(), false).send(r -> {
                                _list.refresh();
                            });
                        }
                    });
        }
    }

    private void removeAllAttachments() {
        Dialog.confirm(_vp.window(), "Remove all attachments", "Are you sure you want to remove all attachments?",
                executed -> {
                    if (executed) {
                        new ObjectDetach(_o.citeableId()).send(r -> {
                            _list.refresh();
                        });
                    }
                });
    }

}

package daris.web.client.gui.archive;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.button.ButtonBar.Alignment;
import arc.gui.gwt.widget.button.ButtonBar.Position;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.object.CollectionResolveHandler;
import daris.web.client.gui.Resource;
import daris.web.client.gui.util.ButtonUtil;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.model.archive.ArchiveEntry;
import daris.web.client.model.archive.ArchiveEntryCollectionRef;
import daris.web.client.model.archive.messages.ArchiveContentGet;
import daris.web.client.util.SizeUtil;

public class ArchiveViewerGUI extends ContainerWidget implements PagingListener {

    public static final arc.gui.image.Image ICON_DOWNLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.download16().getSafeUri().asString(), 16, 16);

    private ArchiveEntryCollectionRef _arc;

    private ArchiveEntry _selected;

    private SimplePanel _sp;

    private ListGrid<ArchiveEntry> _list;

    private PagingControl _pc;

    private SimplePanel _detailSP;

    public ArchiveViewerGUI(ArchiveEntryCollectionRef arc) {

        _arc = arc;

        VerticalPanel listVP = new VerticalPanel();
        listVP.setPreferredWidth(0.4);

        /*
         * List grid
         */
        _list = new ListGrid<ArchiveEntry>(ScrollPolicy.AUTO) {

            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<ArchiveEntry>> entries) {
                if (entries != null && !entries.isEmpty()) {
                    if (_selected == null) {
                        select(0);
                    } else {
                        select(_selected);
                    }
                }
            }
        };
        _list.setMinRowHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _list.setClearSelectionOnRefresh(false);
        _list.setMultiSelect(false);
        _list.setSelectionHandler(new SelectionHandler<ArchiveEntry>() {

            @Override
            public void selected(ArchiveEntry ae) {
                _selected = ae;
                updateDetailView();
            }

            @Override
            public void deselected(ArchiveEntry ae) {
                _selected = null;
                updateDetailView();
            }
        });
        _list.setEmptyMessage("");
        _list.setLoadingMessage("");
        _list.setLoadingMessage("");
        _list.setCursorSize(_arc.defaultPagingSize());
        _list.fitToParent();
        _list.addColumnDefn("idx", "Index", "Ordinal index", new WidgetFormatter<ArchiveEntry, Integer>() {

            @Override
            public BaseWidget format(ArchiveEntry ae, Integer idx) {
                HTML html = new HTML(String.valueOf(idx));
                html.setFontFamily(DStyles.FONT_FAMILY);
                html.setFontSize(DStyles.LIST_GRID_CELL_FONT_SIZE);
                html.element().getStyle().setLineHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT, Unit.PX);
                return html;
            }
        }).setWidth(50);
        _list.addColumnDefn("name", "Name", "File name/path.", new WidgetFormatter<ArchiveEntry, String>() {

            @Override
            public BaseWidget format(ArchiveEntry ae, String name) {
                HTML html = new HTML(name);
                html.setFontFamily(DStyles.FONT_FAMILY);
                html.setFontSize(DStyles.LIST_GRID_CELL_FONT_SIZE);
                return html;
            }
        }).setWidth(350);
        _list.addColumnDefn("size", "Size", "File size", new WidgetFormatter<ArchiveEntry, Long>() {

            @Override
            public BaseWidget format(ArchiveEntry ae, Long size) {
                HTML html = new HTML(size >= 0 ? SizeUtil.getHumanReadableSize(size, true) : "");
                html.setFontFamily(DStyles.FONT_FAMILY);
                html.setFontSize(DStyles.LIST_GRID_CELL_FONT_SIZE);
                html.element().getStyle().setLineHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT, Unit.PX);
                if (size != null && size >= 0) {
                    html.setToolTip(Long.toString(size) + " bytes");
                }
                return html;
            }
        }).setWidth(150);
        // _list.addColumnDefn("idx", "download", "Download", new
        // WidgetFormatter<ArchiveEntry, Integer>() {
        //
        // @Override
        // public BaseWidget format(final ArchiveEntry ae, Integer idx) {
        // Button button = new Button("Download");
        // button.addClickHandler(new ClickHandler() {
        // @Override
        // public void onClick(ClickEvent event) {
        // new ArchiveContentGet(_arc, ae).send();
        // }
        // });
        // return button;
        // }
        // }).setWidth(80);
        listVP.add(_list);

        _pc = new PagingControl(_arc.defaultPagingSize());
        _pc.setWidth100();
        _pc.setHeight(22);
        _pc.addPagingListener(this);
        listVP.add(_pc);

        _detailSP = new SimplePanel();
        _detailSP.fitToParent();

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(3);
        hsp.fitToParent();

        hsp.add(listVP);
        hsp.add(_detailSP);

        _sp = new SimplePanel();
        _sp.fitToParent();
        _sp.setContent(hsp);

        initWidget(_sp);
        gotoOffset(0);
    }

    @Override
    public void gotoOffset(long offset) {
        _arc.resolve(offset, offset + _arc.pagingSize(), new CollectionResolveHandler<ArchiveEntry>() {
            @Override
            public void resolved(List<ArchiveEntry> data) throws Throwable {
                long total = _arc.totalNumberOfMembers();
                _pc.setOffset(offset, total, true);
                List<ListGridEntry<ArchiveEntry>> lges = null;
                if (data != null && !data.isEmpty()) {
                    lges = new ArrayList<ListGridEntry<ArchiveEntry>>();
                    for (ArchiveEntry ae : data) {
                        ListGridEntry<ArchiveEntry> lge = new ListGridEntry<ArchiveEntry>(ae);
                        lge.set("idx", ae.ordinal());
                        lge.set("name", ae.name());
                        lge.set("size", ae.size());
                        lges.add(lge);
                    }
                }
                _list.setData(lges);
            }
        });
    }

    private void updateDetailView() {
        if (_selected == null) {
            _detailSP.clear();
            return;
        }
        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();

        if (_selected.isViewableImage()) {
            ArchiveEntryImagePanel imagePanel = new ArchiveEntryImagePanel(_arc, _selected);
            imagePanel.fitToParent();
            vp.add(imagePanel);
        }

        AbsolutePanel infoPanel = createInfoPanel(_selected);
        vp.add(infoPanel);

        ButtonBar bb = new ButtonBar(Position.BOTTOM, Alignment.CENTER);
        Button button = ButtonUtil.createButton(ICON_DOWNLOAD, "Download", "Download " + _selected.fileName(), true);
        button.setWidth(100);
        button.addClickHandler(e -> {
            new ArchiveContentGet(_arc, _selected).send();
        });
        bb.add(button);
        bb.setHeight(32);
        vp.add(bb);

        _detailSP.setContent(vp);
    }

    private static AbsolutePanel createInfoPanel(ArchiveEntry ae) {
        String tdStyle = "font-family:" + DStyles.FONT_FAMILY + "; font-size:" + DStyles.LIST_GRID_CELL_FONT_SIZE
                + "px; border: 1px inset #ddd;";
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"position:absolute; top:50%; left:50%; transform:translateX(-50%) translateY(-50%);\">");
        sb.append(
                "<table cellspacing=\"0\" cellpadding=\"5\" border=\"1\" style=\"border-collapse: collapse; border: 1px inset #ddd\">");
        sb.append("<tr><th align=\"right\" style=\"" + tdStyle + "\">Index:</th><td style=\"" + tdStyle + "\">"
                + ae.ordinal() + "</td></tr>");
        sb.append("<tr><th align=\"right\" style=\"" + tdStyle + "\">Name:</th><td style=\"" + tdStyle + "\">"
                + ae.name() + "</td></tr>");
        sb.append("<tr><th align=\"right\" style=\"" + tdStyle + "\">Size:</th><td style=\"" + tdStyle + "\">"
                + ae.size() + " bytes"
                + (ae.size() > 1000 ? (" (" + SizeUtil.getHumanReadableSize(ae.size(), true) + ")") : "")
                + "</td></tr>");
        sb.append("</table>");
        sb.append("<div>");
        HTML html = new HTML(sb.toString());
        AbsolutePanel ap = new AbsolutePanel();
        if (ae.isViewableImage()) {
            ap.setHeight(100);
            ap.setWidth100();
        } else {
            ap.fitToParent();
        }
        ap.add(html);

        return ap;
    }

}

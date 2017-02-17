package daris.web.client.gui.archive;

import java.util.ArrayList;
import java.util.List;

import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.object.CollectionResolveHandler;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.model.archive.ArchiveEntry;
import daris.web.client.model.archive.ArchiveEntryCollectionRef;

public class ArchiveViewerGUI extends ContainerWidget implements PagingListener {

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
        _list.addColumnDefn("idx", "idx", "Ordinal index").setWidth(50);
        _list.addColumnDefn("name", "name", "File name/path.").setMinWidth(250);
        _list.addColumnDefn("size", "size (bytes)", "File size", new WidgetFormatter<ArchiveEntry, Long>() {

            @Override
            public BaseWidget format(ArchiveEntry ae, final Long size) {
                HTML html = new HTML(size >= 0 ? Long.toString(size) : "");
                return html;
            }
        }).setWidth(100);
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

        HorizontalSplitPanel hsp = new HorizontalSplitPanel();
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
        // TODO:
    }

}

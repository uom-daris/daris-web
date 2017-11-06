package daris.web.client.gui.sink;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.sink.SinkSetRef;

public class SinkSelectForm extends ValidatedInterfaceComponent {

    private VerticalPanel _vp;
    private ListGrid<Sink> _list;
    private HTML _status;

    private Sink _selected;

    public SinkSelectForm() {
        _selected = null;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<Sink>(ScrollPolicy.AUTO);
        _list.fitToParent();
        _list.setDataSource(new DataSource<ListGridEntry<Sink>>() {

            @Override
            public boolean isRemote() {
                return true;
            }

            @Override
            public boolean supportCursor() {
                return false;
            }

            @Override
            public void load(Filter f, long start, long end, DataLoadHandler<ListGridEntry<Sink>> lh) {
                SinkSetRef.DARIS_SINKS.reset();
                SinkSetRef.DARIS_SINKS.resolve(sinks -> {
                    if (sinks != null && !sinks.isEmpty()) {
                        List<ListGridEntry<Sink>> entries = new ArrayList<ListGridEntry<Sink>>();
                        for (Sink sink : sinks) {
                            ListGridEntry<Sink> entry = new ListGridEntry<Sink>(sink);
                            entry.set("name", sink.name());
                            entry.set("type", sink.type().name());
                            entry.set("description", sink.description());
                            entries.add(entry);
                        }
                        lh.loaded(0, entries.size(), entries.size(), entries, DataLoadAction.REPLACE);
                    } else {
                        lh.loaded(0, 0, 0, null, null);
                    }
                });

            }
        });
        _list.addColumnDefn("name", "Sink Name", "Name of the sink.").setWidth(200);
        _list.addColumnDefn("type", "Sink Type", "Type of the sink.").setWidth(200);
        _list.addColumnDefn("description", "Sink Description", "Description about the sink.").setWidth(500);
        _list.setLoadingMessage("retrieving sinks...");
        _list.setEmptyMessage("No sinks found!");
        _list.setAutoColumnWidths(true);
        _list.setMultiSelect(false);
        _list.setSelectionHandler(new SelectionHandler<Sink>() {

            @Override
            public void selected(Sink sink) {
                _selected = sink;
                SinkSelectForm.this.notifyOfChangeInState();
            }

            @Override
            public void deselected(Sink o) {
                _selected = null;
                SinkSelectForm.this.notifyOfChangeInState();
            }
        });
        _list.setMarginLeft(20);
        _list.setMarginRight(20);
        _vp.add(_list);

        _status = new HTML();
        _status.setHeight(20);
        _status.setWidth100();
        _status.setTextAlign(TextAlign.CENTER);
        _status.element().getStyle().setLineHeight(20, Unit.PX);
        _status.setFontSize(10);
        _status.setColour(RGB.RED);
        _vp.add(_status);
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_selected == null) {
                v = new IsNotValid("No sink is selected.");
            }
        }
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public Sink selectedSink() {
        return _selected;
    }

}

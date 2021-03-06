package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.gui.form.FormUtil;
import daris.web.client.model.object.filter.SimpleObjectFilter;
import daris.web.client.model.object.filter.SimpleObjectFilter.Operator;
import daris.web.client.model.query.sort.SortKey;
import daris.web.client.util.MapUtil;

public class ContextViewOptionsForm extends ValidatedInterfaceComponent {

    public static interface Listener {
        void updated(SimpleObjectFilter filter, SortKey sortKey, int pageSize);
    }

    public static interface CloseHandler {
        void closed();
    }

    private VerticalPanel _vp;
    private TabPanel _tp;

    int _filterTabId;
    private SimplePanel _filterSP;
    private Form _filterForm;

    int _sortingTabId;
    private SimplePanel _sortingSP;
    private Form _sortingForm;
    private Field<String> _sortKeyNameField;
    private Field<SortKey.Order> _sortKeyOrderField;

    int _pagingTabId;
    private SimplePanel _pagingSP;
    private Form _pagingForm;

    private List<Listener> _listeners;
    private CloseHandler _ch;

    private SimpleObjectFilter _filter;
    private String _sortKeyName;
    private SortKey _sortKey;
    private int _pageSize;

    private Map<String, SortKey> _availableSortKeys = MapUtil.map(
            new String[] { "object id", "object name", "modification time", "mime type", "content type" },
            new SortKey[] { SortKey.citeableId(), SortKey.objectName(), SortKey.modificationTime(), SortKey.mimeType(),
                    SortKey.contentType() });

    public ContextViewOptionsForm(SimpleObjectFilter filter, SortKey sortKey, int pageSize) {

        _filter = filter == null
                ? new SimpleObjectFilter(SimpleObjectFilter.Type.name, SimpleObjectFilter.Operator.CONTAINS, null)
                : filter.duplicate();

        if (sortKey != null) {
            _sortKey = sortKey.copy();
            Set<String> sortKeyNames = _availableSortKeys.keySet();

            for (String sortKeyName : sortKeyNames) {
                SortKey sk = _availableSortKeys.get(sortKeyName);
                if (sk.keyEquals(sortKey)) {
                    _sortKeyName = sortKeyName;
                    break;
                }
            }
            if (_sortKeyName == null) {
                _sortKeyName = sortKey.key();
                _availableSortKeys.put(sortKey.key(), _sortKey);
            }
        } else {
            _sortKey = SortKey.citeableId();
            _sortKeyName = "object id";
        }

        _pageSize = pageSize;

        _vp = new VerticalPanel();
        _vp.setHeight(150);
        _vp.setWidth100();
        addTitleBar(_vp);

        _tp = new TabPanel();
        _tp.fitToParent();
        _vp.add(_tp);

        _filterSP = new SimplePanel();
        _filterSP.fitToParent();
        _filterSP.setPaddingLeft(20);
        _filterTabId = _tp.addTab("Filter", "Filter", _filterSP);

        _sortingSP = new SimplePanel();
        _sortingSP.fitToParent();
        _sortingSP.setPaddingLeft(20);
        _sortingTabId = _tp.addTab("Sorting", "Sorting Options", _sortingSP);

        _pagingSP = new SimplePanel();
        _pagingSP.fitToParent();
        _pagingSP.setPaddingLeft(20);
        _pagingTabId = _tp.addTab("Paging", "Number of object per page", _pagingSP);

        _tp.setActiveTabById(_filterTabId);

        updateTabs();

        ButtonBar bb = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.RIGHT);
        Button resetButton = bb.addButton("Reset");
        resetButton.addClickHandler(e -> {
            _filter.setValue(null);
            _sortKey = ContextView.DEFAULT_SORT_KEY;
            _pageSize = ContextView.DEFAULT_PAGE_SIZE;
            notifyOfUpdate();
        });
        Button applyButton = bb.addButton("Apply");
        applyButton.addClickHandler(e -> {
            notifyOfUpdate();
        });
        applyButton.setEnabled(valid().valid());
        addChangeListener(() -> {
            applyButton.setEnabled(valid().valid());
        });
        _vp.add(bb);

        _listeners = new ArrayList<Listener>();

    }

    private void updateTabs() {
        updateFilterForm();
        updateSortingForm();
        updatePagingForm();
    }

    private void updatePagingForm() {
        if (_pagingForm != null) {
            removeMustBeValid(_pagingForm);
        }
        _pagingSP.clear();

        _pagingForm = FormUtil.createForm();
        _pagingForm.setShowDescriptions(false);
        _pagingForm.setShowHelp(false);
        _pagingForm.setShowLabels(true);
        _pagingForm.setWidth100();

        Field<Integer> pageSizeField = new Field<Integer>(new FieldDefinition("Page size", "size",
                new EnumerationType<Integer>(new Integer[] { 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000 }),
                null, null, 0, 1));
        pageSizeField.setInitialValue(_pageSize, false);
        pageSizeField.setRenderOptions(new FieldRenderOptions().setWidth(160));
        pageSizeField.addListener(new FormItemListener<Integer>() {

            @Override
            public void itemValueChanged(FormItem<Integer> f) {
                Integer v = f.value();
                if (v == null || v <= 0) {
                    v = ListView.DEFAULT_PAGE_SIZE;
                }
                _pageSize = v;
            }

            @Override
            public void itemPropertyChanged(FormItem<Integer> f, Property property) {

            }
        });
        _pagingForm.add(pageSizeField);

        _pagingForm.render();
        _pagingSP.setContent(_pagingForm);
        addMustBeValid(_pagingForm);
    }

    private void updateSortingForm() {
        if (_sortingForm != null) {
            removeMustBeValid(_sortingForm);
        }
        _sortingSP.clear();

        _sortingForm = FormUtil.createForm();
        _sortingForm.setShowDescriptions(false);
        _sortingForm.setShowHelp(false);
        _sortingForm.setShowLabels(true);
        _sortingForm.setWidth100();

        _sortKeyNameField = new Field<String>(new FieldDefinition("Sort by",
                new EnumerationType<String>(_availableSortKeys.keySet()), "Sort key", null, 0, 1));
        _sortKeyNameField.setInitialValue(_sortKeyName, false);
        _sortKeyNameField.setRenderOptions(new FieldRenderOptions().setWidth(160));
        _sortKeyNameField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                _sortKeyName = f.value();
                _sortKey = new SortKey(_availableSortKeys.get(_sortKeyName).key(), _sortKeyOrderField.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _sortingForm.add(_sortKeyNameField);

        _sortKeyOrderField = new Field<SortKey.Order>(new FieldDefinition("Order",
                new EnumerationType<SortKey.Order>(SortKey.Order.values()), null, null, 0, 1));
        _sortKeyOrderField.setInitialValue(_sortKey.order(), false);
        _sortKeyOrderField.setRenderOptions(new FieldRenderOptions().setWidth(160));
        _sortKeyOrderField.addListener(new FormItemListener<SortKey.Order>() {

            @Override
            public void itemValueChanged(FormItem<SortKey.Order> f) {
                _sortKey = new SortKey(_sortKey.key(), f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<SortKey.Order> f, Property property) {

            }
        });
        _sortingForm.add(_sortKeyOrderField);

        _sortingForm.render();
        _sortingSP.setContent(_sortingForm);
        addMustBeValid(_sortingForm);
    }

    private void updateFilterForm() {

        if (_filterForm != null) {
            removeMustBeValid(_filterForm);
        }
        _filterSP.clear();

        _filterForm = FormUtil.createForm();
        _filterForm.setShowDescriptions(false);
        _filterForm.setShowHelp(false);
        _filterForm.setShowLabels(false);
        _filterForm.setWidth100();

        Field<SimpleObjectFilter.Type> typeField = new Field<SimpleObjectFilter.Type>(new FieldDefinition("type",
                new EnumerationType<SimpleObjectFilter.Type>(SimpleObjectFilter.Type.values()), null, null, 1, 1));
        typeField.setRenderOptions(new FieldRenderOptions().setWidth(100));
        typeField.setInitialValue(_filter.type(), false);
        typeField.addListener(new FormItemListener<SimpleObjectFilter.Type>() {

            @Override
            public void itemValueChanged(FormItem<SimpleObjectFilter.Type> f) {
                if (_filter.type() != f.value()) {
                    _filter.setType(f.value());
                    updateFilterForm();
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<SimpleObjectFilter.Type> f, Property property) {

            }
        });
        _filterForm.add(typeField);

        Field<SimpleObjectFilter.Operator> opField = new Field<SimpleObjectFilter.Operator>(
                new FieldDefinition("operator", new EnumerationType<SimpleObjectFilter.Operator>(
                        SimpleObjectFilter.Operator.operatorsFor(_filter.type())), null, null, 1, 1));
        opField.setRenderOptions(new FieldRenderOptions().setWidth(100));
        opField.setInitialValue(_filter.operator(), false);
        opField.addListener(new FormItemListener<SimpleObjectFilter.Operator>() {

            @Override
            public void itemValueChanged(FormItem<Operator> f) {
                _filter.setOperator(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Operator> f, Property property) {

            }
        });
        _filterForm.add(opField);

        Field<String> valueField = new Field<String>(
                new FieldDefinition("value", StringType.DEFAULT, null, null, 0, 1));
        valueField.setRenderOptions(new FieldRenderOptions().setWidth100());
        valueField.setInitialValue(_filter.value(), false);
        valueField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                String v = f.value();
                if (v != null && v.isEmpty()) {
                    v = null;
                }
                _filter.setValue(v);
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _filterForm.add(valueField);

        _filterForm.render();
        _filterSP.setContent(_filterForm);
        addMustBeValid(_filterForm);

    }

    private void addTitleBar(VerticalPanel container) {

        AbsolutePanel ap = new AbsolutePanel();
        ap.setHeight(20);
        ap.setWidth100();
        container.add(ap);

        HTML title = new HTML("List View Options");
        title.setTextAlign(TextAlign.CENTER);
        title.setFontWeight(FontWeight.BOLD);
        title.setFontSize(11);
        title.setHeight(20);
        title.setWidth100();
        title.setTextShadow(1, 0, 1, RGB.WHITESMOKE);
        title.setBackgroundImage(new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
                ListGridHeader.HEADER_COLOUR_LIGHT, ListGridHeader.HEADER_COLOUR_DARK));
        title.setPosition(Position.ABSOLUTE);
        title.setLeft(0);
        ap.add(title);

        Image closeImage = arc.gui.gwt.widget.window.Window.CLOSE_IMAGE.copyOf();
        closeImage.setPosition(Position.ABSOLUTE);
        closeImage.setTop(1);
        closeImage.setRight(1);
        closeImage.setPadding(2);
        closeImage.setBorder(1, RGBA.TRANSPARENT);
        closeImage.setBorder(1, RGB.GREY_888);
        closeImage.setBorderRadius(3);
        closeImage.setOpacity(0.7);

        closeImage.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                closeImage.setOpacity(1.0);
            }

        });

        closeImage.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                closeImage.setBorder(1, RGB.GREY_444);
                closeImage.setBackgroundColour(RGB.GREY_888);
            }
        });

        closeImage.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                closeImage.setBorder(1, RGB.GREY_888);
                closeImage.setBackgroundColour(RGBA.TRANSPARENT);
                closeImage.setOpacity(0.7);
            }

        });

        closeImage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        ap.add(closeImage);
    }

    private void notifyOfUpdate() {
        SimpleObjectFilter f = _filter.valid().valid() ? _filter : null;
        for (Listener l : _listeners) {
            l.updated(f, _sortKey, _pageSize);
        }
    }

    public void hide() {
        if (_ch != null) {
            _ch.closed();
        }
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public void addListener(Listener ul) {
        _listeners.add(ul);
    }

    public void removeListener(Listener ul) {
        _listeners.remove(ul);
    }

    public void setCloseHandler(CloseHandler ch) {
        _ch = ch;
    }
}

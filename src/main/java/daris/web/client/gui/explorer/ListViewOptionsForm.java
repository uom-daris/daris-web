package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.List;

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
import daris.web.client.model.object.DObjectChildrenRef.SortKey;
import daris.web.client.model.object.SortOrder;
import daris.web.client.model.object.filter.SimpleObjectFilter;
import daris.web.client.model.object.filter.SimpleObjectFilter.Operator;

public class ListViewOptionsForm extends ValidatedInterfaceComponent {

    public static interface UpdateListener {

        void updated(SimpleObjectFilter filter, SortKey sortKey, SortOrder sortOrder, int pageSize);
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

    int _pagingTabId;
    private SimplePanel _pagingSP;
    private Form _pagingForm;

    private List<UpdateListener> _uls;
    private CloseHandler _ch;

    private SimpleObjectFilter _filter;
    private SortKey _sortKey;
    private SortOrder _sortOrder;
    private int _pageSize;

    public ListViewOptionsForm(SimpleObjectFilter filter, SortKey sortKey, SortOrder sortOrder,
            int pageSize) {
        _filter = filter == null
                ? new SimpleObjectFilter(SimpleObjectFilter.Type.name, SimpleObjectFilter.Operator.CONTAINS, null)
                : filter.duplicate();
        _sortKey = sortKey;
        _sortOrder = sortOrder;
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
            _sortKey = ListView.DEFAULT_SORT_KEY;
            _sortOrder = ListView.DEFAULT_SORT_ORDER;
            _pageSize = ListView.DEFAULT_PAGE_SIZE;
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

        _uls = new ArrayList<UpdateListener>();

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

        _pagingForm = new Form();
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

        _sortingForm = new Form();
        _sortingForm.setShowDescriptions(false);
        _sortingForm.setShowHelp(false);
        _sortingForm.setShowLabels(true);
        _sortingForm.setWidth100();

        Field<SortKey> sortKeyField = new Field<SortKey>(
                new FieldDefinition("Sort by", "key", SortKey.toEnumerationType(), null, null, 0, 1));
        sortKeyField.setInitialValue(_sortKey, false);
        sortKeyField.setRenderOptions(new FieldRenderOptions().setWidth(160));
        sortKeyField.addListener(new FormItemListener<SortKey>() {

            @Override
            public void itemValueChanged(FormItem<SortKey> f) {
                _sortKey = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<SortKey> f, Property property) {

            }
        });
        _sortingForm.add(sortKeyField);

        Field<SortOrder> sortOrderField = new Field<SortOrder>(
                new FieldDefinition("Order", "order", SortOrder.toEnumerationType(), null, null, 0, 1));
        sortOrderField.setInitialValue(_sortOrder, false);
        sortOrderField.setRenderOptions(new FieldRenderOptions().setWidth(160));
        sortOrderField.addListener(new FormItemListener<SortOrder>() {

            @Override
            public void itemValueChanged(FormItem<SortOrder> f) {
                _sortOrder = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<SortOrder> f, Property property) {

            }
        });
        _sortingForm.add(sortOrderField);

        _sortingForm.render();
        _sortingSP.setContent(_sortingForm);
        addMustBeValid(_sortingForm);
    }

    private void updateFilterForm() {

        if (_filterForm != null) {
            removeMustBeValid(_filterForm);
        }
        _filterSP.clear();

        _filterForm = new Form();
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
        for (UpdateListener ul : _uls) {
            ul.updated(f, _sortKey, _sortOrder, _pageSize);
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

    public void addUpdateListener(UpdateListener ul) {
        _uls.add(ul);
    }

    public void removeUpdateListener(UpdateListener ul) {
        _uls.remove(ul);
    }

    public void setCloseHandler(CloseHandler ch) {
        _ch = ch;
    }
}

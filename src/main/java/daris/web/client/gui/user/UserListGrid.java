package daris.web.client.gui.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.EnumerationType.Value;
import arc.mf.dtype.StringType;
import arc.mf.model.authentication.DomainRef;
import arc.mf.model.authentication.UserRef;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.Session;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.ListGridStyles;
import daris.web.client.model.user.messages.UserDescribe;

public class UserListGrid extends ContainerWidget {

    private VerticalPanel _vp;
    private ListGrid<UserRef> _list;
    private SimplePanel _filterSP;

    private DomainRef _domain;

    public UserListGrid() {

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<UserRef>();
        _list.setDataSource(new DataSource<ListGridEntry<UserRef>>() {

            @Override
            public boolean isRemote() {
                return true;
            }

            @Override
            public boolean supportCursor() {
                return false;
            }

            @Override
            public void load(final Filter filter, final long start, final long end,
                    final DataLoadHandler<ListGridEntry<UserRef>> lh) {
                new UserDescribe(_domain).send(new ObjectMessageResponse<List<UserRef>>() {

                    @Override
                    public void responded(List<UserRef> users) {
                        if (users != null && !users.isEmpty()) {
                            List<ListGridEntry<UserRef>> entries = new ArrayList<ListGridEntry<UserRef>>();
                            for (UserRef user : users) {
                                if (filter == null || filter.matches(user)) {
                                    ListGridEntry<UserRef> entry = new ListGridEntry<UserRef>(user);
                                    entry.set("domain", user.domain());
                                    entry.set("user", user.name());
                                    entry.set("name", user.personName());
                                    entry.set("email", user.email());
                                    entries.add(entry);
                                }
                            }
                            lh.loaded(start, end, entries.size(), entries, DataLoadAction.REPLACE);
                            return;
                        }
                        lh.loaded(0, 0, 0, null, null);
                    }
                });
            }
        }, false);
        _list.setClearSelectionOnRefresh(false);
        _list.setMultiSelect(false);
        _list.setEmptyMessage("");
        _list.setLoadingMessage("");
        _list.setCursorSize(Integer.MAX_VALUE);
        _list.setMinRowHeight(ListGridStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _list.fitToParent();
        _list.addColumnDefn("domain", "Domain", null, ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(200);
        _list.addColumnDefn("user", "User", null, ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(150);
        _list.addColumnDefn("name", "Name", null, ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(150);
        _list.addColumnDefn("email", "Email", null, ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(200);
        _list.setObjectRegistry(DObjectGUIRegistry.get());
        _list.enableRowDrag();
        _vp.add(_list);

        _filterSP = new SimplePanel();
        _filterSP.setHeight(50);
        _filterSP.setWidth100();
        _filterSP.setPaddingLeft(10);
        _filterSP.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_BBB);
        _vp.add(_filterSP);

        initFilters();

        initWidget(_vp);

    }

    private void initFilters() {

        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();

        HTML title = new HTML("Filters");
        title.setFontFamily(DefaultStyles.FONT_FAMILY);
        title.setFontSize(DefaultStyles.FONT_SIZE);
        title.setFontWeight(FontWeight.BOLD);
        title.setTextAlign(TextAlign.CENTER);
        title.setHeight(22);
        title.element().getStyle().setLineHeight(22, Unit.PX);
        vp.add(title);

        Form form = new Form();
        form.setMargin(0);
        form.setPadding(0);
        form.setNumberOfColumns(2);
        form.setShowDescriptions(false);
        form.setShowHelp(false);
        form.setShowLabels(true);
        Field<String> domain = new Field<String>(new FieldDefinition("Domain", "domain",
                new EnumerationType<String>(new DynamicEnumerationDataSource<String>() {

                    @Override
                    public boolean supportPrefix() {
                        return false;
                    }

                    @Override
                    public void exists(String value, DynamicEnumerationExistsHandler handler) {
                        if (value == null) {
                            handler.exists(value, false);
                            return;
                        }
                        if (value.equals("all")) {
                            handler.exists(value, true);
                            return;
                        }
                        Session.execute("authentication.domain.exists", "<domain>" + value + "</domain>",
                                (xe, outputs) -> {
                                    handler.exists(value, xe.booleanValue("exists", false));
                                });
                    }

                    @Override
                    public void retrieve(String prefix, long start, long end,
                            DynamicEnumerationDataHandler<String> handler) {
                        Session.execute("authentication.domain.list", (xe, outputs) -> {
                            Collection<String> domains = xe.values("domain");
                            List<Value<String>> values = new ArrayList<Value<String>>();
                            values.add(new Value<String>("all"));
                            if (domains != null && !domains.isEmpty()) {
                                for (String domain : domains) {
                                    Value<String> entry = new Value<String>(domain);
                                    values.add(entry);
                                }
                            }
                            handler.process(start, end, values.size(), values);
                        });

                    }
                }), null, null, 0, 1));
        domain.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                String value = f.value();
                if (value == null || value.equals("all")) {
                    _domain = null;
                } else {
                    _domain = new DomainRef(value);
                }
                _list.refresh();
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        if (_domain != null) {
            domain.setValue(_domain.name());
        } else {
            domain.setValue("all");
        }
        form.add(domain);

        Field<String> name = new Field<String>(
                new FieldDefinition("Name", "name", StringType.DEFAULT, null, null, 0, 1));
        name.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                String value = f.value();
                Filter filter = createFilterFor(value);
                _list.refresh(filter, true);
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        form.add(name);

        form.render();

        vp.add(form);

        _filterSP.setContent(vp);

    }

    private static Filter createFilterFor(String value) {
        return new Filter() {
            @Override
            public boolean matches(Object o) {
                if (value == null || value.trim().isEmpty()) {
                    return true;
                }
                String str = value.trim().toLowerCase();
                UserRef u = (UserRef) o;
                String name = u.name().toLowerCase();
                if (name.indexOf(str) != -1) {
                    return true;
                }
                String email = u.email() == null ? null : u.email().toLowerCase();
                if (email != null && email.indexOf(str) != -1) {
                    return true;
                }
                String fullName = u.personName() == null ? null : u.personName().toLowerCase();
                if (fullName != null && fullName.indexOf(str) != -1) {
                    return true;
                }
                return false;
            }
        };
    }

}

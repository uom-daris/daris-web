package daris.web.client.gui.project;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.button.ButtonBar.Alignment;
import arc.gui.gwt.widget.button.ButtonBar.Position;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.TabPanel.TabPosition;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.Resource;
import daris.web.client.gui.object.DObjectViewForm;
import daris.web.client.gui.project.user.ProjectUserDialog;
import daris.web.client.gui.util.ButtonUtil;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.model.method.MethodRef;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.Project;
import daris.web.client.model.project.ProjectRoleUser;
import daris.web.client.model.project.ProjectUser;

public class ProjectViewForm extends DObjectViewForm<Project> {

    public static final String USER_TAB_NAME = "Users";


    public static final arc.gui.image.Image ICON_USER = new arc.gui.image.Image(
            Resource.INSTANCE.group20().getSafeUri().asString(), 16, 16);

    public ProjectViewForm(Project o) {
        super(o);
    }

    @Override
    protected void updateOtherTabs() {
        updateUserTab();
    }

    @Override
    protected void appendToInterfaceForm(Form form) {
        Project project = object();
        List<MethodRef> methods = project.methods();
        if (methods != null) {
            for (MethodRef m : methods) {
                Field<MethodRef> methodField = new Field<MethodRef>(new FieldDefinition("Method", "method",
                        ConstantType.DEFAULT, null, null, 0, Integer.MAX_VALUE));
                methodField.setValue(m, false);
                form.add(methodField);
            }
        }

        DataUse dataUse = project.dataUse();
        if (dataUse != null) {
            addDataUseField(dataUse, form);
        }
    }

    public static void addDataUseField(DataUse dataUse, Form form) {
        Field<DataUse> dataUseField = new Field<DataUse>(
                new FieldDefinition("Data Use", "data-use", new EnumerationType<DataUse>(DataUse.values()),
                        "Specifies the type of consent for the use of data for this project: "
                                + "<br> 1) 'specific' means use the data only for the original specific intent, "
                                + "<br> 2) 'extended' means use the data for related projects and "
                                + "<br> 3) 'unspecified' means use the data for any research",
                        null, 1, 1));
        dataUseField.setValue(dataUse);
        form.add(dataUseField);
    }

    private void updateUserTab() {
        Project project = object();

        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();

        ListGrid<ProjectUser> userList = createUserListGrid(project.users());
        userList.fitToParent();

        if (project.roleUsers() != null) {
            TabPanel tp = new TabPanel(TabPosition.BOTTOM);
            tp.fitToParent();
            tp.addTab("Users", null, userList);

            ListGrid<ProjectRoleUser> roleUserList = createRoleUserListGrid(project.roleUsers());
            roleUserList.fitToParent();
            tp.addTab("Role users", null, roleUserList);
            tp.setActiveTab(0);
            vp.add(tp);
        } else {
            vp.add(userList);
        }

        if (project.editable()) {
            ButtonBar bb = new ButtonBar(Position.BOTTOM, Alignment.CENTER);
            bb.setHeight(32);
            Button button = ButtonUtil.createButton(ICON_USER, "Manage users", "Add or remove project users.", true);
            button.setWidth(120);
            button.addClickHandler(e -> {
                new ProjectUserDialog(project).show(window(), executed -> {
                    if (executed) {
                        MessageBox.show(320, 40, widget(), MessageBox.Position.CENTER, "Updating project users...", 5);
                    }
                });
            });
            bb.add(button);
            vp.add(bb);
        }
        putTab(USER_TAB_NAME, "Project users", vp);
    }

    private static ListGrid<ProjectUser> createUserListGrid(List<ProjectUser> users) {
        ListGrid<ProjectUser> list = new ListGrid<ProjectUser>(ScrollPolicy.AUTO);
        list.setMultiSelect(false);
        list.setClearSelectionOnRefresh(true);
        list.setMinRowHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT);
        WidgetFormatter<ProjectUser, String> formatter = new WidgetFormatter<ProjectUser, String>() {

            @Override
            public BaseWidget format(ProjectUser user, String value) {
                return createListGridCellHtml(value);
            }
        };
        list.addColumnDefn("domain", "Domain", null, formatter).setWidth(120);
        list.addColumnDefn("user", "User", null, formatter).setWidth(120);
        list.addColumnDefn("role", "Role", null, formatter).setWidth(200);
        list.addColumnDefn("data-use", "Data Use", null, formatter).setWidth(180);
        if (users != null && !users.isEmpty()) {
            List<ListGridEntry<ProjectUser>> entries = new ArrayList<ListGridEntry<ProjectUser>>(users.size());
            for (ProjectUser pu : users) {
                ListGridEntry<ProjectUser> e = new ListGridEntry<ProjectUser>(pu);
                e.set("domain", pu.user().domain().name());
                e.set("user", pu.user().name());
                e.set("role", pu.role().toString());
                e.set("data-use", pu.dataUse() == null ? null : pu.dataUse().toString());
                entries.add(e);
            }
            list.setData(entries);
        } else {
            list.setData(null);
        }
        list.setAutoColumnWidths(true);
        list.setEmptyMessage("");
        return list;
    }

    private static ListGrid<ProjectRoleUser> createRoleUserListGrid(List<ProjectRoleUser> roleUsers) {

        ListGrid<ProjectRoleUser> list = new ListGrid<ProjectRoleUser>(ScrollPolicy.AUTO);
        list.setMultiSelect(false);
        list.setClearSelectionOnRefresh(true);
        list.setMinRowHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT);
        WidgetFormatter<ProjectRoleUser, String> formatter = new WidgetFormatter<ProjectRoleUser, String>() {

            @Override
            public BaseWidget format(ProjectRoleUser user, String value) {
                return createListGridCellHtml(value);
            }
        };
        list.addColumnDefn("name", "Name", null, formatter).setWidth(200);
        list.addColumnDefn("role", "Role", null, formatter).setWidth(200);
        list.addColumnDefn("data-use", "Data Use", null, formatter).setWidth(180);
        if (roleUsers != null && !roleUsers.isEmpty()) {
            List<ListGridEntry<ProjectRoleUser>> entries = new ArrayList<ListGridEntry<ProjectRoleUser>>(
                    roleUsers.size());
            for (ProjectRoleUser ru : roleUsers) {
                ListGridEntry<ProjectRoleUser> e = new ListGridEntry<ProjectRoleUser>(ru);
                e.set("name", ru.name());
                e.set("role", ru.role().toString());
                e.set("data-use", ru.dataUse() == null ? null : ru.dataUse().toString());
                entries.add(e);
            }
            list.setData(entries);
        } else {
            list.setData(null);
        }
        list.setAutoColumnWidths(true);
        list.setEmptyMessage("");
        return list;
    }

    private static HTML createListGridCellHtml(String value) {
        if (value != null) {
            HTML html = new HTML(value);
            html.setFontSize(11);
            html.setFontFamily(DStyles.FONT_FAMILY);
            html.setHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT);
            html.element().getStyle().setLineHeight(DStyles.LIST_GRID_MIN_ROW_HEIGHT, Unit.PX);
            return html;
        }
        return null;
    }

}

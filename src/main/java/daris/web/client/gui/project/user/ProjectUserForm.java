package daris.web.client.gui.project.user;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.combo.ComboBox;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.tip.ToolTip;
import arc.gui.gwt.widget.tip.ToolTipHandler;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.model.authentication.DomainRef;
import arc.mf.model.authentication.User;
import arc.mf.model.authentication.UserRef;
import daris.web.client.gui.Resource;
import daris.web.client.gui.user.RoleUserListGrid;
import daris.web.client.gui.user.UserSelect;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.model.project.DataUse;
import daris.web.client.model.project.ProjectRoleType;
import daris.web.client.model.project.ProjectRoleUser;
import daris.web.client.model.project.ProjectUser;
import daris.web.client.model.user.RoleUser;

public class ProjectUserForm extends ValidatedInterfaceComponent {

    public static interface ProjectUserChangeHandler {
        void changed(List<ProjectUser> user, List<ProjectRoleUser> roleUsers);
    }

    public static final int LIST_GRID_CELL_FONT_SIZE = 11;

    public static final int LIST_GRID_MIN_ROW_HEIGHT = 28;

    public static final arc.gui.image.Image ICON_DELETE = new arc.gui.image.Image(
            Resource.INSTANCE.delete_12x16().getSafeUri().asString(), 12, 16);

    private static HTML createCellHtml(String value) {
        HTML html = value == null ? new HTML() : new HTML(value);
        html.setHeight(LIST_GRID_MIN_ROW_HEIGHT);
        html.setFontFamily(DStyles.FONT_FAMILY);
        html.setFontSize(LIST_GRID_CELL_FONT_SIZE);
        html.element().getStyle().setLineHeight(LIST_GRID_MIN_ROW_HEIGHT, Unit.PX);
        return html;
    }

    private static ComboBox<DataUse> createDataUseComboBox() {
        ComboBox<DataUse> combo = new ComboBox<DataUse>(DataUse.comboBoxEntries());
        combo.setShowMultiSelect(false);
        return combo;
    }

    private static ComboBox<ProjectRoleType> createRoleComboBox() {
        ComboBox<ProjectRoleType> combo = new ComboBox<ProjectRoleType>(ProjectRoleType.comboBoxEntries());
        combo.setShowMultiSelect(false);
        return combo;
    }

    private static boolean dropRoleUser(List<ProjectRoleUser> prus, RoleUser ru) {
        boolean exists = false;
        for (ProjectRoleUser pru : prus) {
            if (pru.name().equals(ru.name())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            prus.add(new ProjectRoleUser(ru));
            return true;
        } else {
            return false;
        }
    }

    private static boolean dropUser(List<ProjectUser> pus, UserRef u) {
        boolean exists = false;
        for (ProjectUser pu : pus) {
            if (pu.user().equals(u)) {
                exists = true;
            }
        }
        if (!exists) {
            pus.add(new ProjectUser(u));
            return true;
        } else {
            return false;
        }
    }

    private static String toolTipFor(DomainRef domain) {
        StringBuilder sb = new StringBuilder("<div><ul>");
        sb.append("<li><b>Domain: </b>").append(domain.name()).append("</li>");
        if (domain.authority() != null) {
            sb.append("<li><b>Authority: </b>").append(domain.authority().name()).append("</li>");
            if (domain.authority().protocol() != null) {
                sb.append("<li><b>Protocol: </b>").append(domain.authority().protocol()).append("</li>");
            }
        }
        sb.append("</ul></div>");
        return sb.toString();
    }

    private static String toolTipFor(User user) {
        StringBuilder sb = new StringBuilder("<div><ul>");
        sb.append("<li><b>Domain: </b>").append(user.domain()).append("</li>");
        sb.append("<li><b>User: </b>").append(user.name()).append("</li>");
        if (user.firstName() != null || user.lastName() != null) {
            sb.append("<li><b>Name: </b>").append(user.firstName()).append(" ").append(user.lastName()).append("</li>");
        }
        if (user.email() != null) {
            sb.append("<li><b>Name: </b>").append(user.email()).append("</li>");
        }
        sb.append("</ul></div>");
        return sb.toString();
    }

    private static arc.gui.gwt.widget.image.Image createDeleteIcon() {
        arc.gui.gwt.widget.image.Image deleteIcon = new arc.gui.gwt.widget.image.Image(ICON_DELETE);
        deleteIcon.addMouseOverHandler(e -> {
            deleteIcon.setBackgroundColour(RGB.GREY_EEE);
        });
        deleteIcon.addMouseOutHandler(e -> {
            deleteIcon.element().getStyle().clearBackgroundColor();
        });
        return deleteIcon;
    }

    private List<ProjectUser> _users;
    private List<ProjectRoleUser> _roleUsers;

    private VerticalPanel _vp;

    private TabPanel _tpLeft;
    private int _userTabId;
    private int _roleUserTabId;
    private ListGrid<ProjectUser> _userList;
    private ListGrid<ProjectRoleUser> _roleUserList;

    private TabPanel _tpRight;
    private UserSelect _availableUserList;
    private RoleUserListGrid _availableRoleUserList;

    private HTML _hint;
    private HTML _status;

    public ProjectUserForm() {
        this(null, null);
    }

    public ProjectUserForm(List<ProjectUser> users, List<ProjectRoleUser> roleUsers) {

        _users = new ArrayList<ProjectUser>();
        if (users != null) {
            for (ProjectUser user : users) {
                _users.add(user.copy());
            }
        }

        _roleUsers = new ArrayList<ProjectRoleUser>();
        if (roleUsers != null) {
            for (ProjectRoleUser roleUser : roleUsers) {
                _roleUsers.add(roleUser.copy());
            }
        }

        _vp = new VerticalPanel();
        _vp.fitToParent();

        HorizontalSplitPanel hsp = new HorizontalSplitPanel(5);
        hsp.fitToParent();
        _vp.add(hsp);

        _tpLeft = new TabPanel() {
            protected void activated(int id) {
                if (id == _userTabId) {
                    showAvailableUsers();
                    _hint.setHTML(
                            "To add project user, drag a user from 'Available DaRIS Users' and drop into the 'Project Users' list.");
                } else if (id == _roleUserTabId) {
                    showAvailableRoleUsers();
                    _hint.setHTML(
                            "To add project role user, drag a user from 'Available DaRIS Role Users' and drop into the 'Project Role Users' list.");

                }
            }
        };
        _tpLeft.fitToParent();
        hsp.add(_tpLeft);

        initUserList();
        updateUserList();
        _userTabId = _tpLeft.addTab("Project Users", null, _userList);

        initRoleUserList();
        updateRoleUserList();
        _roleUserTabId = _tpLeft.addTab("Project Role Users", null, _roleUserList);

        _tpRight = new TabPanel();
        _tpRight.fitToParent();

        hsp.add(_tpRight);

        _hint = new HTML();
        _hint.setFontFamily(DStyles.FONT_FAMILY);
        _hint.setFontSize(10);
        _hint.setTextAlign(TextAlign.CENTER);
        _hint.setVerticalAlign(VerticalAlign.MIDDLE);
        _hint.setWidth100();
        _hint.setHeight(20);
        _hint.element().getStyle().setLineHeight(20, Unit.PX);
        _hint.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_EEE);
        _hint.setTextShadow(1, 1, 0, RGB.GREY_EEE);
        _vp.add(_hint);

        _status = new HTML();
        _status.setFontFamily(DStyles.FONT_FAMILY);
        _status.setColour(RGB.RED);
        _status.setFontSize(11);
        _status.setHeight(28);
        _status.setWidth100();
        _status.setPaddingLeft(10);
        _status.element().getStyle().setLineHeight(28, Unit.PX);
        _status.setBorder(1, RGB.GREY_EEE);
        _vp.add(_status);

        _tpLeft.setActiveTab(0);

    }

    @Override
    public Widget gui() {
        return _vp;
    }

    private void initRoleUserList() {

        _roleUserList = new ListGrid<ProjectRoleUser>(ScrollPolicy.AUTO);
        _roleUserList.setPreferredWidth(0.4);
        _roleUserList.setHeight100();
        _roleUserList.setMultiSelect(false);
        _roleUserList.setEmptyMessage("");
        _roleUserList.setMinRowHeight(LIST_GRID_MIN_ROW_HEIGHT);
        _roleUserList.addColumnDefn("object", "", null, new WidgetFormatter<ProjectRoleUser, ProjectRoleUser>() {

            @Override
            public BaseWidget format(ProjectRoleUser context, ProjectRoleUser ru) {
                arc.gui.gwt.widget.image.Image deleteIcon = createDeleteIcon();
                deleteIcon.addClickHandler(e -> {
                    _roleUsers.remove(ru);
                    updateRoleUserList();
                    notifyOfChangeInState();
                });
                deleteIcon.setToolTip("delete role user: " + ru.name());
                return deleteIcon;
            }
        }).setWidth(24);
        _roleUserList.addColumnDefn("name", "Name", "Role name", new WidgetFormatter<ProjectRoleUser, String>() {

            @Override
            public BaseWidget format(ProjectRoleUser pru, String name) {
                HTML html = createCellHtml(name);
                return html;
            }
        }).setWidth(100);
        _roleUserList
                .addColumnDefn("role", "Role", "Role type", new WidgetFormatter<ProjectRoleUser, ProjectRoleType>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public BaseWidget format(ProjectRoleUser pru, ProjectRoleType role) {
                        ComboBox<ProjectRoleType> combo = createRoleComboBox();
                        combo.setInitialValue(role.toString(), role);
                        combo.addChangeListener(cb -> {
                            pru.setRole(cb.value());
                            ComboBox<DataUse> dataUseCombo = (ComboBox<DataUse>) _roleUserList.rowFor(pru).cell(3).widget();
                            if (pru.role() == ProjectRoleType.GUEST || pru.role() == ProjectRoleType.MEMBER) {
                                dataUseCombo.setValue(DataUse.SPECIFIC.toString(), DataUse.SPECIFIC);
                                dataUseCombo.enable();
                            } else {
                                dataUseCombo.setValue(null, null);
                                dataUseCombo.disable();
                            }
                            notifyOfChangeInState();
                        });
                        combo.setWidth(130);
                        return combo;
                    }
                }).setWidth(140);

        _roleUserList
                .addColumnDefn("data-use", "Data Use", "Data use", new WidgetFormatter<ProjectRoleUser, DataUse>() {

                    @Override
                    public BaseWidget format(ProjectRoleUser pru, DataUse dataUse) {
                        ComboBox<DataUse> combo = createDataUseComboBox();
                        if (dataUse != null) {
                            combo.setInitialValue(dataUse.toString(), dataUse);
                        }
                        if (pru.role() == ProjectRoleType.PROJECT_ADMINISTRATOR
                                || pru.role() == ProjectRoleType.SUBJECT_ADMINISTRATOR) {
                            combo.setInitialValue(null, null);
                            combo.disable();
                        }
                        combo.addChangeListener(cb -> {
                            pru.setDataUse(cb.value());
                            notifyOfChangeInState();
                        });
                        combo.setWidth(110);
                        return combo;
                    }
                }).setWidth(120);

        _roleUserList.enableDropTarget(false);
        _roleUserList.setDropHandler(new DropHandler() {

            @Override
            public DropCheck checkCanDrop(Object object) {
                if (object != null && object instanceof RoleUser) {
                    return DropCheck.CAN;
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
                boolean changed = false;
                for (Object object : objects) {
                    if (dropRoleUser(_roleUsers, (RoleUser) object)) {
                        changed = true;
                    }
                }
                if (changed) {
                    updateRoleUserList();
                    notifyOfChangeInState();
                }
            }
        });

    }

    private void updateRoleUserList() {
        List<ListGridEntry<ProjectRoleUser>> entries = new ArrayList<ListGridEntry<ProjectRoleUser>>(_roleUsers.size());
        for (ProjectRoleUser roleUser : _roleUsers) {
            ListGridEntry<ProjectRoleUser> entry = new ListGridEntry<ProjectRoleUser>(roleUser);
            entry.set("name", roleUser.name());
            entry.set("role", roleUser.role());
            entry.set("data-use", roleUser.dataUse());
            entry.set("object", roleUser);
            entries.add(entry);
        }
        _roleUserList.setData(entries, false);
    }

    private void initUserList() {

        _userList = new ListGrid<ProjectUser>(ScrollPolicy.AUTO);
        _userList.setPreferredWidth(0.4);
        _userList.setHeight100();
        _userList.fitToParent();
        _userList.setMultiSelect(false);
        _userList.setEmptyMessage("");
        _userList.setMinRowHeight(LIST_GRID_MIN_ROW_HEIGHT);
        _userList.addColumnDefn("object", "", null, new WidgetFormatter<ProjectUser, ProjectUser>() {

            @Override
            public BaseWidget format(ProjectUser context, ProjectUser u) {
                arc.gui.gwt.widget.image.Image deleteIcon = createDeleteIcon();
                deleteIcon.addClickHandler(e -> {
                    _users.remove(u);
                    updateUserList();
                    notifyOfChangeInState();
                });
                deleteIcon.setToolTip("remove user: " + u.user().actorName());
                return deleteIcon;
            }
        }).setWidth(22);
        _userList.addColumnDefn("domain", "Domain", "Authentication domain",
                new WidgetFormatter<ProjectUser, DomainRef>() {

                    @Override
                    public BaseWidget format(ProjectUser pu, DomainRef domain) {
                        HTML html = createCellHtml(domain.name());
                        html.setToolTip(toolTipFor(domain));
                        return html;
                    }
                }).setWidth(80);
        _userList.addColumnDefn("user", "User", "User login", new WidgetFormatter<ProjectUser, UserRef>() {

            @Override
            public BaseWidget format(ProjectUser pu, final UserRef user) {
                HTML html = createCellHtml(user.name());
                html.setToolTip(new ToolTip<UserRef>() {

                    @Override
                    public void generate(UserRef ctx, ToolTipHandler th) {
                        user.resolve(o -> {
                            th.setTip(new HTML(toolTipFor(o)));
                        });
                    }
                });
                return html;
            }
        }).setWidth(80);
        _userList.addColumnDefn("role", "Role", "Role type", new WidgetFormatter<ProjectUser, ProjectRoleType>() {

            @SuppressWarnings("unchecked")
            @Override
            public BaseWidget format(ProjectUser pu, ProjectRoleType role) {
                ComboBox<ProjectRoleType> combo = createRoleComboBox();
                combo.setInitialValue(role.toString(), role);
                combo.addChangeListener(cb -> {
                    pu.setRole(cb.value());
                    ComboBox<DataUse> dataUseCombo = (ComboBox<DataUse>) _userList.rowFor(pu).cell(4).widget();
                    if (pu.role() == ProjectRoleType.GUEST || pu.role() == ProjectRoleType.MEMBER) {
                        dataUseCombo.setValue(DataUse.SPECIFIC.toString(), DataUse.SPECIFIC);
                        dataUseCombo.enable();
                    } else {
                        dataUseCombo.setValue(null, null);
                        dataUseCombo.disable();
                    }
                    notifyOfChangeInState();
                });
                combo.setWidth(130);
                return combo;
            }
        }).setWidth(140);

        _userList.addColumnDefn("data-use", "Data Use", "Data use", new WidgetFormatter<ProjectUser, DataUse>() {

            @Override
            public BaseWidget format(ProjectUser pu, DataUse dataUse) {
                ComboBox<DataUse> combo = createDataUseComboBox();
                if (dataUse != null) {
                    combo.setInitialValue(dataUse.toString(), dataUse);
                }
                if (pu.role() == ProjectRoleType.PROJECT_ADMINISTRATOR
                        || pu.role() == ProjectRoleType.SUBJECT_ADMINISTRATOR) {
                    combo.setInitialValue(null, null);
                    combo.disable();
                }
                combo.addChangeListener(cb -> {
                    pu.setDataUse(cb.value());
                    notifyOfChangeInState();
                });
                combo.setWidth(110);
                return combo;
            }
        }).setWidth(120);

        _userList.enableDropTarget(false);
        _userList.setDropHandler(new DropHandler() {

            @Override
            public DropCheck checkCanDrop(Object object) {
                if (object != null && object instanceof UserRef) {
                    return DropCheck.CAN;
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
                boolean changed = false;
                for (Object object : objects) {
                    if (dropUser(_users, (UserRef) object)) {
                        changed = true;
                    }
                }
                if (changed) {
                    updateUserList();
                    notifyOfChangeInState();
                }
            }
        });

    }

    private void updateUserList() {
        List<ListGridEntry<ProjectUser>> entries = new ArrayList<ListGridEntry<ProjectUser>>(_users.size());
        for (ProjectUser user : _users) {
            ListGridEntry<ProjectUser> entry = new ListGridEntry<ProjectUser>(user);
            entry.set("domain", user.user().domain());
            entry.set("user", user.user());
            entry.set("role", user.role());
            entry.set("data-use", user.dataUse());
            entry.set("object", user);
            entries.add(entry);
        }
        _userList.setData(entries, false);
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_users.isEmpty()) {
                v = new IsNotValid("No project user is set. At least one project user is required.");
            } else {
                boolean hasAdmin = false;
                for (ProjectUser user : _users) {
                    if (user.role() == ProjectRoleType.PROJECT_ADMINISTRATOR) {
                        hasAdmin = true;
                        break;
                    }
                }
                if (!hasAdmin) {
                    v = new IsNotValid(
                            "No project administrator is set. At least one project administrator is required.");
                }
            }
        }
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    private void showAvailableUsers() {

        if (_availableUserList == null) {
            _availableUserList = new UserSelect();
        }
        _tpRight.removeAll();
        _tpRight.addTab("Avaiable DaRIS Users", null, _availableUserList);
        _tpRight.setActiveTab(0);
    }

    private void showAvailableRoleUsers() {
        if (_availableRoleUserList == null) {
            _availableRoleUserList = new RoleUserListGrid();
        }
        _tpRight.removeAll();
        _tpRight.addTab("Avaiable DaRIS Role Users", null, _availableRoleUserList);
        _tpRight.setActiveTab(0);
    }

    public List<ProjectRoleUser> roleUsers() {
        return _roleUsers;
    }

    public List<ProjectUser> users() {
        return _users;
    }

}

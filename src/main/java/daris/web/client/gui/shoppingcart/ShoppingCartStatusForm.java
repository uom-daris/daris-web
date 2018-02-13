package daris.web.client.gui.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.InterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.input.TextArea;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.Menu;
import arc.mf.client.util.Action;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.FormUtil;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.ListGridCellWidget;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.Log;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;

public class ShoppingCartStatusForm implements InterfaceComponent {

    private ShoppingCart _cart;
    private SimplePanel _sp;
    private Field<Status> _statusField;
    private ListGrid<ShoppingCart.Log> _logList;

    public ShoppingCartStatusForm(ShoppingCart cart) {
        _sp = new SimplePanel();
        _sp.fitToParent();
        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();
        _sp.setContent(vp);

        /*
         * status
         */
        Form statusForm = FormUtil.createForm(FormEditMode.READ_ONLY);
        statusForm.setHeight(30);
        statusForm.setPadding(20);
        _statusField = new Field<Status>(new FieldDefinition("status", new EnumerationType<Status>(Status.values()),
                "The current status of the shopping cart.", null, 0, 1));
        statusForm.add(_statusField);
        statusForm.render();
        vp.add(statusForm);

        /*
         * logs
         */
        _logList = new ListGrid<Log>(ScrollPolicy.AUTO);
        _logList.setEmptyMessage("");
        _logList.fitToParent();
        _logList.setMinRowHeight(DefaultStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _logList.addColumnDefn("changed", "Modification Time", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER)
                .setWidth(120);
        _logList.addColumnDefn("status", "Status", null, ListGridCellWidget.DEFAULT_TEXT_FORMATTER).setWidth(120);
        _logList.addColumnDefn("message", "Message", null, new WidgetFormatter<Log, String>() {

            @Override
            public BaseWidget format(ShoppingCart.Log log, String message) {
                if (message == null || message.isEmpty()) {
                    return null;
                }
                if (message.indexOf('\n') == -1) {
                    return ListGridCellWidget.createTextWidget(message);
                } else {
                    TextArea ta = new TextArea();
                    ta.setWidth100();
                    ta.setHeight(100);
                    ta.setFontFamily(DefaultStyles.FONT_FAMILY);
                    ta.setFontSize(DefaultStyles.LIST_GRID_CELL_FONT_SIZE);
                    ta.setReadOnly(true);
                    ta.setValue(message);
                    ta.selectAll();
                    Menu menu = new Menu();
                    menu.add(new ActionEntry("Copy message to clipboard", new Action() {

                        @Override
                        public void execute() {
                            arc.gui.util.ClipboardUtil.copyToClipboard(message);
                        }
                    }));
                    ta.setContextMenu(menu);
                    return ta;
                }
            }
        }).setWidth(550);
        vp.add(_logList);

        setCart(cart);
    }

    void setCart(ShoppingCart cart) {
        _cart = cart;
        _statusField.setValue(_cart == null ? null : _cart.status());
        _logList.clear();
        if (_cart != null) {
            List<Log> logs = _cart.logs();
            if (logs != null && !logs.isEmpty()) {
                List<ListGridEntry<ShoppingCart.Log>> entries = new ArrayList<ListGridEntry<ShoppingCart.Log>>();
                for (Log log : logs) {
                    ListGridEntry<Log> entry = new ListGridEntry<Log>(log);
                    entry.set("status", log.status);
                    entry.set("changed", log.changed);
                    entry.set("message", log.message);
                    entries.add(entry);
                }
                _logList.setData(entries);
            }
        }
    }

    @Override
    public Widget gui() {
        return _sp;
    }

}

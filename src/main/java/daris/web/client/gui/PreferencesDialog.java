package daris.web.client.gui;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.client.xml.XmlElement;
import arc.mf.dtype.EnumerationType;
import arc.mf.session.Session;
import daris.web.client.gui.explorer.ListView;
import daris.web.client.gui.widget.LoadingMessage;

public class PreferencesDialog extends ValidatedInterfaceComponent {

    public static final String APP = "daris-web-explorer";

    private SimplePanel _sp;

    private XmlElement _se;

    private int _pageSizeCommitted;

    private int _pageSizeUncommitted;

    public PreferencesDialog() {

        _pageSizeCommitted = ListView.DEFAULT_PAGE_SIZE;
        _pageSizeUncommitted = ListView.DEFAULT_PAGE_SIZE;
        _sp = new SimplePanel();
        _sp.fitToParent();

        update();
    }

    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_pageSizeCommitted == _pageSizeUncommitted) {
                return IsNotValid.INSTANCE; // no change
            }
        }
        return v;
    }

    private void update() {
        _sp.setContent(new LoadingMessage("loading..."));
        Session.execute("user.self.settings.get", "<app>" + APP + "</app>", (re, outputs) -> {
            _se = re.element("settings[@app='" + APP + "']");
            update(_se);
        });
    }

    private void update(XmlElement se) throws Throwable {
        _pageSizeCommitted = se.intValue("page.size", ListView.DEFAULT_PAGE_SIZE);
        TabPanel tp = new TabPanel();
        tp.fitToParent();
        Form explorerPreferencesForm = new Form(FormEditMode.UPDATE);
        Field<Integer> pageSizeField = new Field<Integer>(new FieldDefinition("Objects per page", "pageSize",
                new EnumerationType<Integer>(), "Number of object per page.", null, 0, 1));
        pageSizeField.addListener(new FormItemListener<Integer>() {

            @Override
            public void itemValueChanged(FormItem<Integer> f) {
                _pageSizeUncommitted = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<Integer> f, Property property) {

            }
        });
        explorerPreferencesForm.add(pageSizeField);
        explorerPreferencesForm.render();
        tp.addTab("Explorer", "Explorer preferences", explorerPreferencesForm);
        _sp.setContent(tp);
    }

    public void show(arc.gui.window.Window owner) {
        // TODO
    }

    @Override
    public Widget gui() {
        return _sp;
    }

}

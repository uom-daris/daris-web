package daris.web.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.dialog.DialogProperties;
import arc.gui.dialog.DialogProperties.Type;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.widget.LoadingMessage;

public class AboutDialog {

    private SimplePanel _sp;

    public AboutDialog() {

        _sp = new SimplePanel();
        _sp.fitToParent();
        update();
    }

    private void update() {
        _sp.setContent(new LoadingMessage("loading..."));
        XmlStringWriter w = new XmlStringWriter();
        w.add("service", new String[] { "name", "server.identity" });
        w.add("service", new String[] { "name", "package.list" });
        Session.execute("service.execute", w.document(), new ServiceResponseHandler() {
            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                XmlElement se = xe.element("reply[@service='server.identity']/response/server");
                List<XmlElement> pes = xe.elements("reply[@service='package.list']/response/package");
                List<XmlElement> dpes = null;
                if (pes != null) {
                    dpes = new ArrayList<XmlElement>();
                    for (XmlElement pe : pes) {
                        if (pe.value().startsWith("daris-")) {
                            dpes.add(pe);
                        }
                    }
                }
                update(se, dpes);
            }
        });
    }

    private void update(XmlElement se, List<XmlElement> pes) {
        TabPanel tp = new TabPanel();
        tp.fitToParent();

        /*
         * server
         */
        Form serverIdentityForm = new Form(FormEditMode.READ_ONLY);
        serverIdentityForm.setShowHelp(false);
        serverIdentityForm.setShowDescriptions(false);
        serverIdentityForm.setMarginTop(10);
        serverIdentityForm.setMarginLeft(30);
        XmlMetaForm.addDocsForView(serverIdentityForm, se.elements());
        serverIdentityForm.render();

        tp.addTab("Server", "Mediaflux server identity", new ScrollPanel(serverIdentityForm, ScrollPolicy.AUTO));

        /*
         * packages
         */
        if (pes != null && !pes.isEmpty()) {
            ListGrid<XmlElement> packageListGrid = createPackageListGrid(pes);
            tp.addTab("Packages", "DaRIS plugin packages", packageListGrid);
        }

        tp.setActiveTab(0);
        _sp.setContent(tp);
    }

    private static ListGrid<XmlElement> createPackageListGrid(List<XmlElement> pes) {
        ListGrid<XmlElement> list = new ListGrid<XmlElement>(ScrollPolicy.AUTO);
        list.addColumnDefn("package", "Package").setWidth(350);
        list.addColumnDefn("version", "Version");

        List<ListGridEntry<XmlElement>> entries = new ArrayList<ListGridEntry<XmlElement>>(pes.size());
        for (XmlElement pe : pes) {
            ListGridEntry<XmlElement> entry = new ListGridEntry<XmlElement>(pe);
            entry.set("package", pe.value());
            entry.set("version", pe.value("@version"));
            entries.add(entry);
        }
        list.setData(entries, false);
        return list;
    }

    public void show(arc.gui.window.Window owner) {
        DialogProperties dp = new DialogProperties(Type.NONE, "About DaRIS", new ValidatedInterfaceComponent() {
            @Override
            public Widget gui() {
                return _sp;
            }
        });
        dp.setActionEnabled(true);
        dp.setModal(false);
        dp.setOwner(owner);
        dp.setSize(480, 320);
        dp.setButtonLabel("OK");
        Dialog dlg = Dialog.postDialog(dp);
        dlg.show();
    }

}

package daris.web.client.gui.object;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.TextFieldRenderOptions;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.ListOfType;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import daris.web.client.gui.dataset.DatasetViewer;
import daris.web.client.gui.exmethod.ExMethodViewer;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.project.ProjectViewerGUI;
import daris.web.client.gui.study.StudyViewer;
import daris.web.client.gui.subject.SubjectViewer;
import daris.web.client.gui.widget.DStyles;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.object.DObject;
import daris.web.client.model.project.Project;
import daris.web.client.model.study.Study;
import daris.web.client.model.subject.Subject;
import daris.web.client.util.StringUtils;

public class DObjectViewerGUI<T extends DObject> extends ValidatedInterfaceComponent {

    public static final int HEADER_HEIGHT = ListGridHeader.HEIGHT;
    public static final Image HEADER_BACKGROUND_IMAGE = new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
            ListGridHeader.HEADER_COLOUR_LIGHT, ListGridHeader.HEADER_COLOUR_DARK);

    public static final String INTERFACE_TAB_NAME = "Interface";
    public static final String METADATA_TAB_NAME = "Metadata";
    public static final String ATTACHMENT_TAB_NAME = "Attachment";

    private T _o;
    private VerticalPanel _vp;
    private TabPanel _tp;

    private Map<String, Integer> _tabIds;
    private Map<Integer, String> _tabNames;

    private VerticalPanel _interfaceVP;

    protected DObjectViewerGUI(T o) {
        _o = o;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        HTML title = new HTML(titleFor(o));
        title.setFontFamily(DStyles.FONT_FAMILY);
        title.setFontSize(11);
        title.setFontWeight(FontWeight.BOLD);
        title.setWidth100();
        title.setHeight(HEADER_HEIGHT);
        title.element().getStyle().setLineHeight(HEADER_HEIGHT, Unit.PX);
        title.setBackgroundImage(HEADER_BACKGROUND_IMAGE);
        title.setTextAlign(TextAlign.CENTER);
        title.setTextShadow(0, 1, 1, RGB.WHITE);
        _vp.add(title);

        _tp = new TabPanel();
        _tp.fitToParent();

        _vp.add(_tp);

        _tabIds = new LinkedHashMap<String, Integer>();
        _tabNames = new LinkedHashMap<Integer, String>();

        updateInterfaceTab();

        updateMetadataTab();

        updateAttachmentTab();

        activateTab(INTERFACE_TAB_NAME);

    }

    private void updateAttachmentTab() {
        AttachmentListGrid attachmentList = new AttachmentListGrid(_o);
        putTab(ATTACHMENT_TAB_NAME, "Attachments", attachmentList);
    }

    private void updateMetadataTab() {

        if (_o.metadata() == null) {
            removeTab(METADATA_TAB_NAME);
            return;
        }
        Form metadataForm = XmlMetaForm.formFor(_o.metadata(), FormEditMode.READ_ONLY);
        metadataForm.setMarginTop(10);
        metadataForm.setMarginLeft(10);
        metadataForm.setWidth100();
        metadataForm.render();
        putTab(METADATA_TAB_NAME, "Metadata", new ScrollPanel(metadataForm, ScrollPolicy.AUTO));
    }

    private void updateInterfaceTab() {
        if (_interfaceVP == null) {
            _interfaceVP = new VerticalPanel();
            _interfaceVP.setPaddingTop(10);
            _interfaceVP.setPaddingLeft(10);
            _interfaceVP.fitToParent();
        } else {
            _interfaceVP.removeAll();
        }

        prependToInterfaceVP(_interfaceVP);

        Form interfaceForm = new Form(FormEditMode.READ_ONLY);
        interfaceForm.setWidth100();

        prependToInterfaceForm(interfaceForm);

        addToInterfaceForm(interfaceForm);

        appendToInterfaceForm(interfaceForm);

        interfaceForm.render();

        _interfaceVP.add(interfaceForm);

        appendToInterfaceVP(_interfaceVP);

        putTab(INTERFACE_TAB_NAME, "Interface", new ScrollPanel(_interfaceVP, ScrollPolicy.AUTO));

    }

    protected void prependToInterfaceVP(VerticalPanel interfaceVP) {

    }

    protected void appendToInterfaceVP(VerticalPanel interfaceVP) {

    }

    protected void prependToInterfaceForm(Form interfaceForm) {

    }

    private void addToInterfaceForm(Form interfaceForm) {

        Field<String> cid = new Field<String>(new FieldDefinition("ID", "cid", StringType.DEFAULT,
                "Citeable id of the " + _o.type().toString() + ". (asset_id=" + _o.assetId() + ")", null, 1, 1));
        cid.setValue(_o.citeableId());
        interfaceForm.add(cid);

        if (_o.name() != null) {
            Field<String> name = new Field<String>(new FieldDefinition("Name", "name", StringType.DEFAULT,
                    "Name of the " + _o.type().toString() + ".", null, 0, 1));
            name.setValue(_o.name());
            interfaceForm.add(name);
        }

        Field<String> namespace = new Field<String>(new FieldDefinition("Namespace", "namespace", StringType.DEFAULT,
                "Asset namespace where the " + _o.type().toString() + " is located.", null, 1, 1));
        namespace.setValue(_o.namespace());
        interfaceForm.add(namespace);

        if (_o.hasTags()) {
            List<String> tags = new ArrayList<String>(_o.tags().size());
            Field<List<String>> tagsField = new Field<List<String>>(
                    new FieldDefinition("Tags", "tags", new ListOfType(ConstantType.DEFAULT), "Tags", null, 0, 1));
            tagsField.setValue(tags);
            interfaceForm.add(tagsField);
        }

        if (_o.description() != null) {
            Field<String> description = new Field<String>(new FieldDefinition("Description", "description",
                    TextType.DEFAULT, "Description about the " + _o.type().toString() + ".", null, 0, 1));
            FieldRenderOptions fro = new FieldRenderOptions();
            fro.addOption(TextFieldRenderOptions.AUTO_RESIZE, true);
            fro.setWidth100();
            description.setRenderOptions(fro);
            description.setValue(_o.description());

            interfaceForm.add(description);
        }

        if (_o.filename() != null) {
            Field<String> filename = new Field<String>(
                    new FieldDefinition("Original File Name", "filename", StringType.DEFAULT,
                            "Original file name of the " + _o.type().toString() + "'s content.", null, 0, 1));
            filename.setValue(_o.filename());
            interfaceForm.add(filename);
        }

    }

    protected void appendToInterfaceForm(Form interfaceForm) {

    }

    public T object() {
        return _o;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    protected void activateTab(String name) {
        Integer tabId = _tabIds.get(name);
        if (tabId != null) {
            _tp.setActiveTabById(tabId);
        }
    }

    protected void putTab(String name, String description, BaseWidget w) {
        if (_tabIds.containsKey(name)) {
            _tp.setTabContent(_tabIds.get(name), w);
        } else {
            int tabId = _tp.addTab(name, description, w);
            _tabIds.put(name, tabId);
            _tabNames.put(tabId, name);
        }
    }

    protected void removeTab(String name) {
        if (_tabIds.containsKey(name)) {
            int tabId = _tabIds.get(name);
            _tp.removeTabById(_tabIds.get(name));
            _tabIds.remove(name);
            _tabNames.remove(tabId);
        }
    }

    private static String titleFor(DObject o) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.upperCaseFirst(o.type().toString()));
        sb.append(" ").append(o.citeableId());
        if (o.name() != null) {
            sb.append(": ").append(o.name());
        }
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    public static DObjectViewerGUI create(DObject object) {
        switch (object.type()) {
        case PROJECT:
            return new ProjectViewerGUI((Project) object);
        case SUBJECT:
            return new SubjectViewer((Subject) object);
        case EX_METHOD:
            return new ExMethodViewer((ExMethod) object);
        case STUDY:
            return new StudyViewer((Study) object);
        case DATASET:
            return DatasetViewer.create((Dataset) object);
        default:
            throw new AssertionError("Unknown object type: " + object.type());
        }
    }

}

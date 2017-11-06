package daris.web.client.gui.object;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.Validity;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import daris.web.client.gui.dataset.DatasetUpdateForm;
import daris.web.client.gui.exmethod.ExMethodUpdateForm;
import daris.web.client.gui.project.ProjectUpdateForm;
import daris.web.client.gui.study.StudyUpdateForm;
import daris.web.client.gui.subject.SubjectUpdateForm;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.exmethod.ExMethod;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectUpdater;
import daris.web.client.model.project.Project;
import daris.web.client.model.study.Study;
import daris.web.client.model.subject.Subject;

public abstract class DObjectUpdateForm<T extends DObject> extends ValidatedInterfaceComponent
        implements AsynchronousAction {

    protected T object;
    protected DObjectUpdater<T> updater;

    private VerticalPanel _container;
    protected TabPanel tabs;
    private HTML _status;

    protected DObjectUpdateForm(T o) {
        this.object = o;
        this.updater = DObjectUpdater.create(o);

        _container = new VerticalPanel();
        _container.fitToParent();

        this.tabs = new TabPanel();
        this.tabs.fitToParent();
        _container.add(this.tabs);

        addToContainer(_container);

        _status = new HTML();
        _status.setFontSize(10);
        _status.setColour(RGB.RED);
        _status.setTextAlign(TextAlign.CENTER);
        _status.setHeight(22);
        _status.setWidth100();
        _status.setBorder(1, ListGridHeader.HEADER_COLOUR_LIGHT);
        _container.add(_status);
        addChangeListener(() -> {
            Validity v = valid();
            if (v.valid()) {
                _status.clear();
            } else {
                _status.setHTML(v.reasonForIssue());
            }
        });

        Form interfaceForm = new Form(FormEditMode.UPDATE);
        interfaceForm.fitToParent();
        addToInterfaceForm(interfaceForm);
        interfaceForm.render();
        this.tabs.addTab("Interface", null, new ScrollPanel(interfaceForm, ScrollPolicy.AUTO));
        this.tabs.setActiveTab(0);

        notifyOfChangeInState();
    }

    protected void addToContainer(VerticalPanel container) {

    }

    protected void addToInterfaceForm(Form interfaceForm) {

        Field<String> name = new Field<String>(
                new FieldDefinition("Name", "name", StringType.DEFAULT, null, null, 1, 1));
        name.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                updater.setName(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        name.setInitialValue(this.updater.name(), false);
        interfaceForm.add(name);

        Field<String> description = new Field<String>(
                new FieldDefinition("Description", "description", TextType.DEFAULT, null, null, 0, 1));
        description.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                updater.setDescription(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        description.setInitialValue(this.updater.description(), false);
        interfaceForm.add(description);
    }

    @Override
    public Widget gui() {
        return _container;
    }

    public arc.gui.gwt.widget.window.Window window() {
        return _container.window();
    }

    @SuppressWarnings("rawtypes")
    public static DObjectUpdateForm create(DObject object) {
        switch (object.objectType()) {
        case PROJECT:
            return new ProjectUpdateForm((Project) object);
        case SUBJECT:
            return new SubjectUpdateForm((Subject) object);
        case EX_METHOD:
            return new ExMethodUpdateForm((ExMethod) object);
        case STUDY:
            return new StudyUpdateForm((Study) object);
        case DATASET:
            return DatasetUpdateForm.create((Dataset) object);
        default:
            throw new AssertionError("Unknown object type: " + object.objectType());
        }
    }

}
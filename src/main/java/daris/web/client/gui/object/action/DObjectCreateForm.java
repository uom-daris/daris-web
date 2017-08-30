package daris.web.client.gui.object.action;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
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
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import daris.web.client.model.object.DObjectCreator;

public abstract class DObjectCreateForm<T extends DObjectCreator> extends ValidatedInterfaceComponent
        implements AsynchronousAction {

    private VerticalPanel _container;
    protected TabPanel tabs;

    private HTML _status;

    protected T creator;

    protected DObjectCreateForm(T creator) {
        this.creator = creator;

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

        Form interfaceForm = new Form();
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
                new FieldDefinition("Name", "name", StringType.DEFAULT, null, null, 0, 1));
        name.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setName(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        name.setInitialValue(this.creator.name(), false);
        interfaceForm.add(name);

        Field<String> description = new Field<String>(
                new FieldDefinition("Description", "description", TextType.DEFAULT, null, null, 0, 1));
        description.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setDescription(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        description.setInitialValue(this.creator.description(), false);
        interfaceForm.add(description);

        Field<String> type = new Field<String>(
                new FieldDefinition("Type", "type", new EnumerationType<String>(), null, null, 0, 1));
        type.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                creator.setType(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        type.setInitialValue(this.creator.type(), false);
        interfaceForm.add(type);

    }

    @Override
    public Widget gui() {
        return _container;
    }

}

package daris.web.client.gui.object.action;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import daris.web.client.model.object.DObjectCreator;

public abstract class DObjectCreateForm<T extends DObjectCreator> extends ValidatedInterfaceComponent
        implements AsynchronousAction {

    protected VerticalPanel container;
    protected TabPanel tabs;

    protected T creator;

    protected DObjectCreateForm(T creator) {
        this.creator = creator;

        this.container = new VerticalPanel();
        this.container.fitToParent();

        this.tabs = new TabPanel();
        this.tabs.fitToParent();
        this.container.add(this.tabs);

        Form interfaceForm = new Form();
        interfaceForm.fitToParent();
        addToInterfaceForm(interfaceForm);
        interfaceForm.render();
        this.tabs.addTab("Interface", null, new ScrollPanel(interfaceForm, ScrollPolicy.AUTO));
        this.tabs.setActiveTab(0);

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
        return this.container;
    }

}

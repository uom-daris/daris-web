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
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import daris.web.client.gui.form.FormUtil;
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

        Form interfaceForm = FormUtil.createForm(FormEditMode.CREATE);
        interfaceForm.setPaddingTop(15);
        interfaceForm.setPaddingLeft(20);
        interfaceForm.setPaddingRight(20);
        interfaceForm.fitToParent();
        /*
         * called by sub classes
         */
        addToInterfaceForm(interfaceForm);
        /*
         * fill in
         */
        Field<Boolean> fillInField = new Field<Boolean>(
                new FieldDefinition("Fill in ID Number", "Fill_in_ID_Number", BooleanType.DEFAULT_TRUE_FALSE,
                        "Fill in/Reuse deleted citeable identifiers. Not recommended.", null, 0, 1));
        fillInField.setInitialValue(creator.fillInIdNumber(), false);
        fillInField.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                creator.setFillInIdNumber(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        interfaceForm.add(fillInField);

        /*
         * allow incomplete metadata
         */
        Field<Boolean> allowIncompleteMetadataField = new Field<Boolean>(
                new FieldDefinition("Allow Incomplete Metadata", "Allow_Incomplete_Meatdata",
                        BooleanType.DEFAULT_TRUE_FALSE, "Allow incomplete metadata. Not recommended.", null, 0, 1));
        allowIncompleteMetadataField.setInitialValue(creator.allowIncompleteMeta(), false);
        allowIncompleteMetadataField.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                creator.setAllowIncompleteMeta(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        interfaceForm.add(allowIncompleteMetadataField);
        interfaceForm.render();
        this.tabs.addTab("Interface", null, new ScrollPanel(interfaceForm, ScrollPolicy.AUTO));
        this.tabs.setActiveTab(0);
        addMustBeValid(interfaceForm);

        notifyOfChangeInState();
    }

    protected void addToContainer(VerticalPanel container) {

    }

    protected void addToInterfaceForm(Form interfaceForm) {

        Field<String> name = new Field<String>(
                new FieldDefinition("Name", "Name", StringType.DEFAULT, "Name", null, 1, 1));
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
                new FieldDefinition("Description", "Description", TextType.DEFAULT, "Description", null, 0, 1));
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

    }

    @Override
    public Widget gui() {
        return _container;
    }

}

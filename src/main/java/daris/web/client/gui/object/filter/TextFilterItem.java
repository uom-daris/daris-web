package daris.web.client.gui.object.filter;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.model.object.filter.TextFilter;

public class TextFilterItem extends FilterItem<TextFilter> {

    private TextFilter _f;

    private HorizontalPanel _hp;

    private Form _opForm;
    private Form _valueForm;

    public TextFilterItem(TextFilter.Operator op, String value) {
        _f = new TextFilter(op, value);

        _hp = new HorizontalPanel();
        _hp.setHeight(22);

        updateGUI();
    }

    private void updateGUI() {
        removeAllMustBeValid();
        _hp.removeAll();

        HTML label = new HTML("Text");
        label.setFontSize(10);
        label.setFontWeight(FontWeight.BOLD);
        _hp.add(label);

        /*
         * operator
         */
        TextFilter.Operator op = _f.operator();
        _opForm = new Form(FormEditMode.UPDATE);
        _opForm.setShowLabels(false);
        _opForm.setShowDescriptions(false);
        _opForm.setShowHelp(false);
        Field<TextFilter.Operator> opField = new Field<TextFilter.Operator>(new FieldDefinition("Operator", "operator",
                new EnumerationType<TextFilter.Operator>(TextFilter.Operator.values()), null, null, 1, 1));
        opField.setInitialValue(op, false);
        opField.addListener(new FormItemListener<TextFilter.Operator>() {

            @Override
            public void itemValueChanged(FormItem<TextFilter.Operator> f) {
                _f.setOperator(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<TextFilter.Operator> f, Property property) {

            }
        });
        _opForm.add(opField);
        _opForm.render();
        _hp.add(_opForm);
        addMustBeValid(_opForm);

        /*
         * value
         */
        _valueForm = new Form(FormEditMode.UPDATE);
        _valueForm.setShowLabels(false);
        _valueForm.setShowDescriptions(false);
        _valueForm.setShowHelp(false);
        Field<String> valueField = new Field<String>(
                new FieldDefinition("Value", "value", StringType.DEFAULT, null, null, 1, 1));
        valueField.setInitialValue(_f.value(), false);
        valueField.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                _f.setValue(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        _valueForm.add(valueField);
        _valueForm.render();
        _hp.add(_valueForm);
        addMustBeValid(_valueForm);

    }

    @Override
    public Widget gui() {
        return _hp;
    }

    @Override
    public TextFilter filter() {
        return _f;
    }

}

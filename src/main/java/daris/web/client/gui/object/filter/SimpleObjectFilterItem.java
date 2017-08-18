package daris.web.client.gui.object.filter;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.util.Validity;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.model.object.filter.SimpleObjectFilter;
import daris.web.client.model.object.filter.SimpleObjectFilter.Operator;

public class SimpleObjectFilterItem extends FilterItem<SimpleObjectFilter> {

    private SimpleObjectFilter _f;

    private VerticalPanel _vp;
    private SimplePanel _formSP;
    private Form _form;
    private HTML _status;

    public SimpleObjectFilterItem(SimpleObjectFilter f) {

        _f = f == null
                ? new SimpleObjectFilter(SimpleObjectFilter.Type.name, SimpleObjectFilter.Operator.CONTAINS, null)
                : f.duplicate();

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _formSP = new SimplePanel();
        _formSP.setPaddingLeft(20);
        _formSP.setPaddingRight(20);
        _formSP.fitToParent();
        _vp.add(_formSP);

        _status = new HTML();
        _status.setHeight(20);
        _status.setWidth100();
        _status.setPaddingLeft(25);
        _status.setFontSize(10);
        _status.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _status.setBorderBottom(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _status.setFontWeight(FontWeight.BOLD);
        _status.setColour(RGB.RED);
        _vp.add(_status);

        addChangeListener(() -> {
            Validity v = valid();
            if (v.valid()) {
                _status.clear();
            } else {
                _status.setHTML(v.reasonForIssue());
            }
        });

        updateGUI();
    }

    private void updateGUI() {
        removeAllMustBeValid();
        _formSP.clear();

        _form = new Form();
        _form.setShowDescriptions(false);
        _form.setShowHelp(false);
        _form.setShowLabels(false);
        _form.setWidth100();

        Field<SimpleObjectFilter.Type> typeField = new Field<SimpleObjectFilter.Type>(new FieldDefinition("type",
                new EnumerationType<SimpleObjectFilter.Type>(SimpleObjectFilter.Type.values()), null, null, 1, 1));
        typeField.setRenderOptions(new FieldRenderOptions().setWidth(100));
        typeField.setInitialValue(_f.type(), false);
        typeField.addListener(new FormItemListener<SimpleObjectFilter.Type>() {

            @Override
            public void itemValueChanged(FormItem<SimpleObjectFilter.Type> f) {
                if (_f.type() != f.value()) {
                    _f.setType(f.value());
                    updateGUI();
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<SimpleObjectFilter.Type> f, Property property) {

            }
        });
        _form.add(typeField);

        Field<SimpleObjectFilter.Operator> opField = new Field<SimpleObjectFilter.Operator>(new FieldDefinition(
                "operator",
                new EnumerationType<SimpleObjectFilter.Operator>(SimpleObjectFilter.Operator.operatorsFor(_f.type())),
                null, null, 1, 1));
        opField.setRenderOptions(new FieldRenderOptions().setWidth(100));
        opField.setInitialValue(_f.operator(), false);
        opField.addListener(new FormItemListener<SimpleObjectFilter.Operator>() {

            @Override
            public void itemValueChanged(FormItem<Operator> f) {
                _f.setOperator(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Operator> f, Property property) {

            }
        });
        _form.add(opField);

        Field<String> valueField = new Field<String>(
                new FieldDefinition("value", StringType.DEFAULT, null, null, 1, 1));
        valueField.setRenderOptions(new FieldRenderOptions().setWidth100());
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
        _form.add(valueField);
        _form.render();
        _formSP.setContent(_form);
        addMustBeValid(_form);
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public SimpleObjectFilter filter() {
        return _f;
    }

}

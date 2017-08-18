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
import arc.mf.client.util.ObjectUtil;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.model.object.filter.MetadataFilter;
import daris.web.client.model.object.filter.MetadataFilter.Operator;

public class MetadataFilterItem extends FilterItem<MetadataFilter> {

    private MetadataFilter _f;
    private boolean _immutablePath;

    private HorizontalPanel _hp;

    private Form _pathForm;
    private Form _opForm;
    private Form _valueForm;

    public MetadataFilterItem(String xpath, String displayName, MetadataFilter.Operator op, String value,
            boolean ignoreCase, boolean immutablePath) {
        _f = new MetadataFilter(xpath, displayName, op, value, ignoreCase);
        _immutablePath = xpath != null && immutablePath;

        _hp = new HorizontalPanel();
        _hp.setHeight(22);

        updateGUI();
    }

    private void updateGUI() {
        removeAllMustBeValid();
        _hp.removeAll();

        /*
         * xpath
         */
        if (!_immutablePath) {
            HTML label = new HTML(_f.name() != null ? _f.name() : _f.path());
            label.setFontSize(10);
            label.setFontWeight(FontWeight.BOLD);
            _hp.add(label);
        } else {
            _pathForm = new Form(FormEditMode.UPDATE);
            _pathForm.setShowLabels(false);
            _pathForm.setShowDescriptions(false);
            _pathForm.setShowHelp(false);
            Field<String> pathField = new Field<String>(
                    new FieldDefinition("Metadata Path", "xpath", StringType.DEFAULT, null, null, 1, 1));
            pathField.setInitialValue(_f.path(), false);
            pathField.addListener(new FormItemListener<String>() {

                @Override
                public void itemValueChanged(FormItem<String> f) {
                    _f.setPath(f.value());
                }

                @Override
                public void itemPropertyChanged(FormItem<String> f, Property property) {

                }
            });
            _pathForm.add(pathField);
            _pathForm.render();
            _hp.add(_pathForm);
            addMustBeValid(_pathForm);
        }

        /*
         * operator
         */
        MetadataFilter.Operator op = _f.operator();
        _opForm = new Form(FormEditMode.UPDATE);
        _opForm.setShowLabels(false);
        _opForm.setShowDescriptions(false);
        _opForm.setShowHelp(false);
        Field<MetadataFilter.Operator> opField = new Field<MetadataFilter.Operator>(new FieldDefinition("Operator",
                "operator", new EnumerationType<MetadataFilter.Operator>(MetadataFilter.Operator.values()), null, null,
                1, 1));
        opField.setInitialValue(op, false);
        opField.addListener(new FormItemListener<MetadataFilter.Operator>() {

            @Override
            public void itemValueChanged(FormItem<Operator> f) {
                if (!ObjectUtil.equals(f.value(), _f.value())) {
                    _f.setOperator(f.value());
                    updateGUI();
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<Operator> f, Property property) {

            }
        });
        _opForm.add(opField);
        _opForm.render();
        _hp.add(_opForm);
        addMustBeValid(_opForm);

        if (op != null && op.numberOfValues() > 0) {
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

            /*
             * ignore-case
             */
            // TODO
        }
    }

    @Override
    public Widget gui() {
        return _hp;
    }

    @Override
    public MetadataFilter filter() {
        return _f;
    }

}

package daris.web.client.gui.query.item;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.IntegerType;
import arc.mf.expr.Operator;
import arc.mf.expr.RangeOperator;
import arc.mf.expr.StandardOperators;

public class IntegerFilterFieldGroup extends FilterFieldGroup {

    private Operator _op;
    private IntegerType _type;
    private Integer _value;
    private Integer _from;
    private Integer _to;

    private Field<Operator> _opField;
    private Field<Integer> _valueField;
    private Field<Integer> _fromField;
    private Field<Integer> _toField;

    public IntegerFilterFieldGroup(String xpath, String name, String description, Operator op, IntegerType type,
            Integer value, Integer from, Integer to) {
        super(xpath, name, name, description, null, 0, 1);
        _op = op;
        _type = type;
        _value = value;
        _from = from;
        _to = to;
        updateGUI();
    }

    private void updateGUI() {
        FieldGroupUtil.removeAllFields(this);
        if (_opField == null) {
            _opField = new Field<Operator>(new FieldDefinition(null, null,
                    new EnumerationType<Operator>(StandardOperators.NUMERIC_OPERATORS), null, null, 1, 1));
            _opField.setRenderOptions(new FieldRenderOptions().setWidth(135));
            _opField.setInitialValue(_op);
            _opField.addListener(new FormItemListener<Operator>() {

                @Override
                public void itemValueChanged(FormItem<Operator> f) {
                    _op = f.value();
                    updateGUI();
                }

                @Override
                public void itemPropertyChanged(FormItem<Operator> f, Property property) {

                }
            });
        }
        add(_opField);
        if (_op != null) {
            if (_op.numberOfValues() == 1) {
                if (_valueField == null) {
                    _valueField = new Field<Integer>(new FieldDefinition(null, null, _type, null, null, 1, 1));
                    _valueField.setInitialValue(_value);
                    _valueField.addListener(new FormItemListener<Integer>() {

                        @Override
                        public void itemValueChanged(FormItem<Integer> f) {
                            _value = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Integer> f, Property property) {

                        }
                    });
                }
                add(_valueField);
            } else if (_op.numberOfValues() > 1) {
                if (_fromField == null) {
                    _fromField = new Field<Integer>(
                            new FieldDefinition("from", "from", _type, "From(inclusive)", null, 1, 1));
                    _fromField.setInitialValue(_from);
                    _fromField.addListener(new FormItemListener<Integer>() {

                        @Override
                        public void itemValueChanged(FormItem<Integer> f) {
                            _from = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Integer> f, Property property) {
                        }
                    });
                }
                add(_fromField);
                if (_toField == null) {
                    _toField = new Field<Integer>(new FieldDefinition("to", "to", _type, "To(inclusive)", null, 1, 1));
                    _toField.setInitialValue(_to);
                    _toField.addListener(new FormItemListener<Integer>() {

                        @Override
                        public void itemValueChanged(FormItem<Integer> f) {
                            _to = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Integer> f, Property property) {
                        }
                    });
                }
                add(_toField);
            }
        }
    }

    @Override
    public void save(StringBuilder sb) {
        if (_op != null) {
            if (xpath().indexOf('/') != -1) {
                sb.append("xpath(");
            }
            sb.append(xpath());
            if (xpath().indexOf('/') != -1) {
                sb.append(")");
            }
            if (RangeOperator.DEFAULT.equals(_op)) {
                sb.append(" in range [");
                sb.append(_from).append(",");
                sb.append(_to);
                sb.append("]");
            } else {
                sb.append(_op.value());
                sb.append(_value);
            }
        }
    }

    @Override
    public void reset() {
        if (_fromField != null) {
            _fromField.reset();
        }
        if (_toField != null) {
            _toField.reset();
        }
        if (_valueField != null) {
            _valueField.reset();
        }
    }
}

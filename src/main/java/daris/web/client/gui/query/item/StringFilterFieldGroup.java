package daris.web.client.gui.query.item;

import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.client.util.ListUtil;
import arc.mf.client.util.ObjectUtil;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import arc.mf.expr.Operator;
import arc.mf.expr.StandardOperators;

public class StringFilterFieldGroup extends FilterFieldGroup {

    public static final Operator ANY = new Operator("any", "", -1);

    static List<Operator> STRING_VALUE_OPERATORS = ListUtil.list(StandardOperators.EQUALS, StandardOperators.CONTAINS,
            StandardOperators.STARTS_WITH, new Operator("ends with", "Value ends with the given characters"), ANY);

    private Operator _op;
    private String _value;
    private boolean _ignoreCase;

    private Field<Operator> _opField;
    private Field<String> _valueField;
    private Field<Boolean> _ignoreCaseField;

    public StringFilterFieldGroup(String xpath, String name, String description) {
        this(xpath, name, name, description, null, 0, 1, ANY, null);
    }

    public StringFilterFieldGroup(String xpath, String title, String name, String description, String helpText,
            int minOccurs, int maxOccurs, Operator op, String value) {
        super(xpath, title, name, description, helpText, minOccurs, maxOccurs);
        _op = op;
        _value = value;
        _ignoreCase = false;

        updateGUI();
    }

    private void updateGUI() {
        FieldGroupUtil.removeAllFields(this);
        if (_opField == null) {
            _opField = new Field<Operator>(new FieldDefinition(null, null,
                    new EnumerationType<Operator>(STRING_VALUE_OPERATORS), null, null, 1, 1));
            _opField.setInitialValue(_op);
            _opField.setRenderOptions(new FieldRenderOptions().setWidth(135));
            _opField.addListener(new FormItemListener<Operator>() {

                @Override
                public void itemValueChanged(FormItem<Operator> f) {
                    if (!ObjectUtil.equals(_op, f.value())) {
                        _op = f.value();
                        if (_op.numberOfValues() <= 0) {
                            _value = null;
                        }
                        updateGUI();
                    }
                }

                @Override
                public void itemPropertyChanged(FormItem<Operator> f, Property property) {
                }
            });
        }
        add(_opField);
        if (_op.numberOfValues() > 0) {
            if (_valueField == null) {
                _valueField = new Field<String>(new FieldDefinition(null, null, StringType.DEFAULT, null, null, 1, 1));
                _valueField.setRenderOptions(new FieldRenderOptions().setWidth(250));
                _valueField.setInitialValue(_value);
                _valueField.addListener(new FormItemListener<String>() {

                    @Override
                    public void itemValueChanged(FormItem<String> f) {
                        if (!ObjectUtil.equals(_value, f.value())) {
                            _value = f.value();
                        }
                    }

                    @Override
                    public void itemPropertyChanged(FormItem<String> f, Property property) {
                    }
                });
            }
            add(_valueField);
            if (_op.equals(StandardOperators.EQUALS)) {
                if (_ignoreCaseField == null) {
                    _ignoreCaseField = new Field<Boolean>(
                            new FieldDefinition("ignore case", BooleanType.DEFAULT_TRUE_FALSE, null, null, 0, 1));
                    _ignoreCaseField.setInitialValue(_ignoreCase);
                    _ignoreCaseField.addListener(new FormItemListener<Boolean>() {

                        @Override
                        public void itemValueChanged(FormItem<Boolean> f) {
                            _ignoreCase = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

                        }
                    });
                }
                add(_ignoreCaseField);
            }
        }
    }

    @Override
    public void save(StringBuilder sb) {

        if (_op != null && _op.numberOfValues() >= 0) {
            sb.append("xpath(").append(xpath()).append(")");
            sb.append(" ");
            sb.append(_op.value());
            if (_op.numberOfValues() > 0) {
                sb.append(" ");
                if (_op.equals(StandardOperators.EQUALS) && _ignoreCase) {
                    sb.append("ignore-case(");
                }
                if (_op.equals(StandardOperators.CONTAINS)) {
                    sb.append("literal(");
                }

                sb.append("'").append(_value).append("'");

                if (_op.equals(StandardOperators.EQUALS) && _ignoreCase) {
                    sb.append(")");
                }
                if (_op.equals(StandardOperators.CONTAINS)) {
                    sb.append(")");
                }
            }
        }
    }

    public void reset() {
        if (_opField != null) {
            _opField.reset();
        }
        if (_valueField != null) {
            _valueField.reset();
        }
        if (_ignoreCaseField != null) {
            _ignoreCaseField.reset();
        }
    }

    public String stringValue() {
        return _value;
    }

    public Operator operator() {
        return _op;
    }

    public boolean ignoreCase() {
        return _ignoreCase;
    }

}

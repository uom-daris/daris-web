package daris.web.client.gui.query.item;

import java.util.Date;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.client.util.DateTime;
import arc.mf.dtype.DateType;
import arc.mf.dtype.EnumerationType;
import arc.mf.expr.Operator;
import arc.mf.expr.RangeOperator;
import arc.mf.expr.StandardOperators;

public class DateFilterFieldGroup extends FilterFieldGroup {

    private Operator _op;
    private Date _value;
    private Date _from;
    private Date _to;

    private Field<Operator> _opField;
    private Field<Date> _valueField;
    private Field<Date> _fromField;
    private Field<Date> _toField;
    private boolean _includeTime;

    public DateFilterFieldGroup(String xpath, String name, String description, boolean includeTime) {
        this(xpath, name, name, description, null, 0, 1, StandardOperators.EQUALS, new Date(0), new Date(0), new Date(),
                includeTime);
    }

    public DateFilterFieldGroup(String xpath, String name, String description, Operator op, Date value, Date from,
            Date to, boolean includeTime) {
        this(xpath, name, name, description, null, 0, 1, op, value, from, to, includeTime);
    }

    protected DateFilterFieldGroup(String xpath, String title, String name, String description, String helpText,
            int minOccurs, int maxOccurs, Operator op, Date value, Date from, Date to, boolean includeTime) {
        super(xpath, title, name, description, helpText, minOccurs, maxOccurs);
        _op = op;
        _value = value;
        _from = from;
        _to = to;
        _includeTime = includeTime;
        updateGUI();
    }

    private void updateGUI() {
        FieldGroupUtil.removeAllFields(this);
        if (_opField == null) {
            _opField = new Field<Operator>(new FieldDefinition(null, null,
                    new EnumerationType<Operator>(StandardOperators.NUMERIC_OPERATORS), null, null, 1, 1));
            _opField.setRenderOptions(new FieldRenderOptions().setWidth(_includeTime ? 135 : 200));
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
            if (RangeOperator.DEFAULT.equals(_op)) {

                if (_fromField == null) {
                    _fromField = new Field<Date>(new FieldDefinition("from", "from",
                            _includeTime ? DateType.DATE_AND_TIME : DateType.DATE_ONLY, "From(inclusive)", null, 1, 1));
                    _fromField.setInitialValue(_from);
                    _fromField.addListener(new FormItemListener<Date>() {

                        @Override
                        public void itemValueChanged(FormItem<Date> f) {
                            _from = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Date> f, Property property) {
                        }
                    });
                }
                add(_fromField);
                if (_toField == null) {
                    _toField = new Field<Date>(new FieldDefinition("to", "to",
                            _includeTime ? DateType.DATE_AND_TIME : DateType.DATE_ONLY, "To(inclusive)", null, 1, 1));
                    _toField.setInitialValue(_to);
                    _toField.addListener(new FormItemListener<Date>() {

                        @Override
                        public void itemValueChanged(FormItem<Date> f) {
                            _to = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Date> f, Property property) {
                        }
                    });
                }
                add(_toField);
            } else {
                if (_valueField == null) {
                    _valueField = new Field<Date>(new FieldDefinition(null, null,
                            _includeTime ? DateType.DATE_AND_TIME : DateType.DATE_ONLY, null, null, 1, 1));
                    _valueField.setValue(_value);
                    _valueField.addListener(new FormItemListener<Date>() {

                        @Override
                        public void itemValueChanged(FormItem<Date> f) {
                            _value = f.value();
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<Date> f, Property property) {

                        }
                    });
                }
                add(_valueField);
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
                Date fromDate = _from.compareTo(_to) <= 0 ? _from : _to;
                Date toDate = _from.compareTo(_to) <= 0 ? _to : _from;
                String fromStr = _includeTime ? DateTime.dateAsServerString(fromDate)
                        : DateTime.dateTimeAsServerString(fromDate);
                String toStr = _includeTime ? DateTime.dateAsServerString(toDate)
                        : DateTime.dateTimeAsServerString(toDate);
                sb.append("'").append(fromStr).append("',");
                sb.append("'").append(toStr).append("'");
                sb.append("]");
            } else {
                sb.append(_op.value());
                sb.append("'").append(
                        _includeTime ? DateTime.dateAsServerString(_value) : DateTime.dateTimeAsServerString(_value))
                        .append("'");
            }
        }
    }

    public Date fromDate() {
        return _from;
    }

    public Date toDate() {
        return _to;
    }

    public Date date() {
        return _value;
    }

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

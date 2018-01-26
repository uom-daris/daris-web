package daris.web.client.gui.query.item;

import java.util.Date;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.client.util.DateTime;
import arc.mf.client.util.ObjectUtil;
import arc.mf.dtype.DateType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.model.query.DateAction;
import daris.web.client.model.query.DatePeriod;

public class TimePeriodFilterFieldGroup extends FilterFieldGroup {

    private DateAction _action;
    private Date _from;
    private Date _to;
    private DatePeriod _period;

    private Field<DateAction> _actionField;
    private Field<DatePeriod> _periodField;
    private Field<Date> _fromField;
    private Field<Date> _toField;
    private boolean _includeTime;

    public TimePeriodFilterFieldGroup(String xpath, String name, String description) {
        this(xpath, name, name, description, null, 0, 1, DateAction.TIME_PERIOD, new Date(), new Date(),
                DatePeriod.ALL_THE_TIME, true);
    }

    public TimePeriodFilterFieldGroup(String xpath, String name, String description, boolean includeTime) {
        this(xpath, name, name, description, null, 0, 1, DateAction.TIME_PERIOD, new Date(), new Date(),
                DatePeriod.ALL_THE_TIME, includeTime);
    }

    public TimePeriodFilterFieldGroup(String xpath, String title, String name, String description, String helpText,
            int minOccurs, int maxOccurs, DateAction action, Date from, Date to, DatePeriod period, boolean includeTime) {
        super(xpath, title, name, description, helpText, minOccurs, maxOccurs);
        _action = action;
        _from = from;
        _to = to;
        _period = period;
        _includeTime = includeTime;
        updateGUI();
    }

    private void updateGUI() {
        FieldGroupUtil.removeAllFields(this);
        if (_actionField == null) {
            _actionField = new Field<DateAction>(new FieldDefinition(null, null,
                    new EnumerationType<DateAction>(DateAction.values()), null, null, 1, 1));
            _actionField.setRenderOptions(new FieldRenderOptions().setWidth(_includeTime ? 135 : 200));
            _actionField.setInitialValue(_action);
            _actionField.addListener(new FormItemListener<DateAction>() {

                @Override
                public void itemValueChanged(FormItem<DateAction> f) {
                    if (!ObjectUtil.equals(_action, f.value())) {
                        _action = f.value();
                        updateGUI();
                    }
                }

                @Override
                public void itemPropertyChanged(FormItem<DateAction> f, Property property) {
                }
            });
        }
        add(_actionField);
        if (_action == DateAction.CHOOSE_DATES) {
            DateType dateType = new DateType(null, new Date());
            dateType.setIncludeTime(_includeTime);
            if (_fromField == null) {
                _fromField = new Field<Date>(
                        new FieldDefinition("from", "from", dateType, "From(inclusive)", null, 1, 1));
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
                _toField = new Field<Date>(new FieldDefinition("to", "to", dateType, "To(inclusive)", null, 1, 1));
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
            if (_periodField == null) {
                _periodField = new Field<DatePeriod>(new FieldDefinition(null, null,
                        new EnumerationType<DatePeriod>(DatePeriod.values()), "Time period", null, 1, 1));
                _periodField.setRenderOptions(new FieldRenderOptions().setWidth(_includeTime ? 135 : 200));
                _periodField.setInitialValue(_period);
                _periodField.addListener(new FormItemListener<DatePeriod>() {

                    @Override
                    public void itemValueChanged(FormItem<DatePeriod> f) {
                        _period = f.value();
                    }

                    @Override
                    public void itemPropertyChanged(FormItem<DatePeriod> f, Property property) {

                    }
                });
            }
            add(_periodField);
        }
    }

    @Override
    public void save(StringBuilder sb) {
        if (_action == DateAction.TIME_PERIOD) {
            if (_period.numberOfDays() >= 0) {
                int nbDays = _period.numberOfDays();
                sb.append("xpath(").append(xpath()).append(")");
                if (nbDays == 0) {
                    sb.append("='today'");
                } else {
                    sb.append("='today-" + nbDays + "day'");
                }
            }
        } else {
            Date fromDate = _from.compareTo(_to) <= 0 ? _from : _to;
            Date toDate = _from.compareTo(_to) <= 0 ? _to : _from;
            String fromStr = _includeTime ? DateTime.dateAsServerString(fromDate)
                    : DateTime.dateTimeAsServerString(fromDate);
            String toStr = _includeTime ? DateTime.dateAsServerString(toDate) : DateTime.dateTimeAsServerString(toDate);
            sb.append("xpath(").append(xpath()).append(")");
            if (ObjectUtil.equals(fromStr, toStr)) {
                sb.append("=");
                sb.append("'").append(toStr).append("'");
            } else {
                sb.append(" in range [");
                sb.append("'").append(fromStr).append("',");
                sb.append("'").append(toStr).append("'");
                sb.append("]");
            }
        }
    }

    public DatePeriod period() {
        if (_action == DateAction.TIME_PERIOD) {
            return _period;
        }
        return null;
    }

    public DateAction action() {
        return _action;
    }

    public Date fromDate() {
        if (_action == DateAction.CHOOSE_DATES) {
            return _from;
        }
        return null;
    }

    public Date toDate() {
        if (_action == DateAction.CHOOSE_DATES) {
            return _to;
        }
        return null;
    }

    public void reset() {
        _actionField.reset();
        _periodField.reset();
        if (_fromField != null) {
            _fromField.reset();
        }
        if (_toField != null) {
            _toField.reset();
        }
    }

}

package daris.web.client.model.query.filter;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.util.Validity;

public abstract class AttributeFilter<T> implements Filter {
    public static class Operator {

        private int _nbOperands;

        private String _value;

        private String _label;

        public String label() {
            return _label;
        }

        public String value() {
            return _value;
        }

        public int numberOfOperands() {
            return _nbOperands;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Operator) {
                Operator op = (Operator) o;
                if (op == o) {
                    return true;
                } else {
                    return ObjectUtil.equals(_value, op.value()) && ObjectUtil.equals(_label, op.label())
                            && _nbOperands == op.numberOfOperands();
                }
            }
            return false;
        }

    }

    private Operator _op;
    private List<T> _values;
    private Boolean _caseSensitive;

    protected AttributeFilter(Operator op, List<T> values) {
        _op = op;
        _values = new ArrayList<T>();
        if (values != null) {
            for (T value : values) {
                _values.add(value);
            }
        }
    }

    @SafeVarargs
    protected AttributeFilter(Operator op, T... values) {
        _op = op;
        _values = new ArrayList<T>();
        if (values != null) {
            for (T value : values) {
                _values.add(value);
            }
        }
    }

    protected AttributeFilter(Operator op) {
        this(op, (List<T>) null);
    }

    protected AttributeFilter() {
        this(null);
    }

    public abstract String attributeName();

    public Boolean caseSensitive() {
        return _caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

    public void setOperator(Operator op) {
        _op = op;
    }

    public Operator operator() {
        return _op;
    }

    @Override
    public String asQueryString() {
        StringBuilder sb = new StringBuilder();
        saveToQuery(sb);
        return sb.toString();
    }

    public Validity valid() {
        if (_op == null) {
            return new IsNotValid("Operator is not specified.");
        }
        if (_op.numberOfOperands() > 0) {
            if (_values.size() != _op.numberOfOperands()) {
                return new IsNotValid("Invalid number of values.");
            }
        }
        return IsValid.INSTANCE;
    }
}

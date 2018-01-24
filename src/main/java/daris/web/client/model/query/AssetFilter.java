package daris.web.client.model.query;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;
import arc.mf.expr.Operator;

public abstract class AssetFilter<T> extends AbstractFilter {

    private Operator _op;
    private List<T> _values;

    protected AssetFilter(Operator op, List<T> values) {
        _op = op;
        if (values != null && !values.isEmpty()) {
            _values = new ArrayList<T>();
            _values.addAll(values);
        }
    }

    public AssetFilter(Operator op) {
        this(op, null);
    }

    public AssetFilter() {
        this(null, null);
    }

    public Operator operator() {
        return _op;
    }

    public AssetFilter<T> setOperator(Operator op) {
        _op = op;
        return this;
    }

    public AssetFilter<T> setValues(List<T> values) {
        if (_values == null) {
            _values = new ArrayList<T>();
        } else {
            _values.clear();
        }
        if (values != null) {
            _values.addAll(values);
        }
        return this;
    }

    public AssetFilter<T> clearValues() {
        if (_values != null) {
            _values.clear();
        }
        return this;
    }

    public int numberOfValues() {
        if (_values == null) {
            return 0;
        }
        return _values.size();
    }

    public AssetFilter<T> addValue(T value) {
        if (_values == null) {
            _values = new ArrayList<T>();
        }
        _values.add(value);
        return this;
    }

    @Override
    public Validity valid() {
        if (_op == null) {
            return new IsNotValid("operator is not set.");
        }
        if (_op.numberOfValues() > numberOfValues()) {
            return new IsNotValid("value is not set.");
        }
        return IsValid.INSTANCE;
    }

}

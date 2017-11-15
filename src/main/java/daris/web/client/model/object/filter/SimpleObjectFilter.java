package daris.web.client.model.object.filter;

import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;

public class SimpleObjectFilter extends AbstractFilter {

    public static enum Type {
        name, metadata
    }

    public static enum Operator {
        EQ("=", true), CONTAINS("contains", false), STARTS_WITH("starts with", false), ENDS_WITH("ends with",
                false), CONTAINS_NO("contains-no", false), CONTAINS_ALL("contains-all", false), NE("!=", true);

        private String _value;
        private boolean _canIgnoreCase;

        Operator(String value, boolean canIgnoreCase) {
            _value = value;
            _canIgnoreCase = canIgnoreCase;
        }

        @Override
        public String toString() {
            return _value;
        }

        public boolean canIgnoreCase() {
            return _canIgnoreCase;
        }

        public static Operator[] operatorsFor(Type type) {
            if (type == Type.name) {
                return new Operator[] { EQ, CONTAINS, STARTS_WITH, ENDS_WITH, CONTAINS_NO, CONTAINS_ALL, NE };
            } else {
                return new Operator[] { CONTAINS, CONTAINS_ALL, CONTAINS_NO };
            }
        }
    }

    private Type _type;
    private Operator _op;
    private String _value;

    public SimpleObjectFilter(Type type, Operator op, String value) {
        _type = type;
        _op = op;
        _value = value;
    }

    public Type type() {
        return _type;
    }

    public void setType(Type type) {
        _type = type;
        if (_type == null) {
            _op = null;
        } else {
            Operator[] ops = Operator.operatorsFor(_type);
            if (_op == null) {
                _op = ops[0];
            } else {
                for (Operator op : ops) {
                    if (_op == op) {
                        return;
                    }
                }
                _op = ops[0];
            }
        }
    }

    public Operator operator() {
        return _op;
    }

    public void setOperator(Operator op) {
        _op = op;
    }

    public String value() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }

    @Override
    public Validity valid() {
        if (_type == null) {
            return new IsNotValid("Missing type.");
        }
        if (_op == null) {
            return new IsNotValid("Missing operator.");
        }
        if (_value == null) {
            return new IsNotValid("Missing value.");
        }
        return IsValid.INSTANCE;
    }

    @Override
    protected void saveAQL(StringBuilder sb) {
        if (_type == Type.name) {
            sb.append("xpath(daris:pssd-object/name) ").append(_op).append(" ");
            if (_op.canIgnoreCase()) {
                sb.append("ignore-case(");
            }
            sb.append("'").append(_value).append("'");
            if (_op.canIgnoreCase()) {
                sb.append(")");
            }
        } else {
            sb.append("text ").append(_op).append(" ");
            if (_op.canIgnoreCase()) {
                sb.append("ignore-case(");
            }
            sb.append("'").append(_value).append("'");
            if (_op.canIgnoreCase()) {
                sb.append(")");
            }
        }
    }

    @Override
    public SimpleObjectFilter duplicate() {
        return new SimpleObjectFilter(_type, _op, _value);
    }

}

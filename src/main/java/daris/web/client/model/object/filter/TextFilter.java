package daris.web.client.model.object.filter;

import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;

public class TextFilter extends AbstractFilter {

    public static enum Operator {
        CONTAINS("contains"), CONTAINS_NO("contains-no");
        private String _op;

        Operator(String op) {
            _op = op;
        }

        @Override
        public String toString() {
            return _op;
        }
    }

    private Operator _op;
    private String _value;

    public TextFilter(Operator op, String value) {
        _op = op;
        _value = value;
    }

    public void setOperator(Operator op) {
        _op = op;
    }

    public void setValue(String value) {
        _value = value;
    }

    @Override
    protected void saveAQL(StringBuilder sb) {
        sb.append("text ");
        sb.append(_op).append(" ");
        sb.append("'").append(_value).append("'");
    }

    @Override
    public Validity valid() {
        if (_op == null) {
            return new IsNotValid("Missing operator.");
        }
        if (_value == null) {
            return new IsNotValid("Missing value.");
        }
        return IsValid.INSTANCE;
    }

    public Operator operator() {
        return _op;
    }

    public String value() {
        return _value;
    }

    @Override
    public TextFilter duplicate() {
        return new TextFilter(_op, _value);
    }

}

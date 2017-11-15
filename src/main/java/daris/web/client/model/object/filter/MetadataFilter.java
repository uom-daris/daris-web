package daris.web.client.model.object.filter;

import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;

public class MetadataFilter extends AbstractFilter {

    public static enum Operator {
        EQ("=", 1), NE("!=", 1), STARTS_WITH("starts with", 1), ENDS_WITH("ends with", 1), CONTAINS("contains", 1);
        private String _op;
        private int _nbValues;

        Operator(String op, int nbValues) {
            _op = op;
            _nbValues = nbValues;
        }

        @Override
        public String toString() {
            return _op;
        }

        public int numberOfValues() {
            return _nbValues;
        }
    }

    private String _xpath;
    private String _name; // display name
    private Operator _op;
    private String _value;
    private boolean _ignoreCase = true;

    public MetadataFilter(String xpath, String name, Operator op, String value, boolean ignoreCase) {
        _xpath = xpath;
        _name = name;
        _op = op;
        _value = value;
        _ignoreCase = ignoreCase;
    }

    public String name() {
        return _name;
    }

    public void setPath(String xpath, String name) {
        _xpath = xpath;
        _name = name;
    }

    public void setPath(String xpath) {
        _xpath = xpath;
    }

    public String path() {
        return _xpath;
    }

    public String value() {
        return _value;
    }

    public Operator operator() {
        return _op;
    }

    public void setValue(String value) {
        _value = value;
    }

    public void setOperator(Operator op) {
        _op = op;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        _ignoreCase = ignoreCase;
    }

    @Override
    protected void saveAQL(StringBuilder sb) {
        sb.append("xpath(");
        sb.append(_xpath);
        sb.append(")");
        sb.append(" ").append(_op).append(" ");
        if (_op.numberOfValues() > 0) {
            if (_ignoreCase) {
                sb.append("ignore-case(");
            }
            sb.append("'").append(_value).append("'");
            if (_ignoreCase) {
                sb.append(")");
            }
        }
    }

    @Override
    public Validity valid() {
        if (_xpath == null) {
            return new IsNotValid("Missing xpath.");
        }
        if (_op == null) {
            return new IsNotValid("Missing operator.");
        }
        if (_op.numberOfValues() > 0 && _value == null) {
            return new IsNotValid("Missing value.");
        }
        return IsValid.INSTANCE;
    }

    @Override
    public MetadataFilter duplicate() {
        return new MetadataFilter(_xpath, _name, _op, _value, _ignoreCase);
    }

}

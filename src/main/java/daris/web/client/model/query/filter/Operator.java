package daris.web.client.model.query.filter;

import arc.mf.client.util.ObjectUtil;

public class Operator {

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

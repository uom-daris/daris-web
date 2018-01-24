package daris.web.client.model.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.util.ListUtil;

public class XValue {

    private String _xpath;
    private String _ename;
    private List<String> _values;

    public XValue(String xpath, String ename, Collection<String> values) {
        _xpath = xpath;
        _ename = ename;
        if (values != null && !values.isEmpty()) {
            _values = new ArrayList<String>();
            _values.addAll(values);
        }
    }

    public XValue(XPath xpath, Collection<String> values) {
        this(xpath.xpath(), xpath.ename(), values);
    }

    public XValue(String xpath, String ename, String... values) {
        _xpath = xpath;
        _ename = ename;
        if (values != null && values.length > 0) {
            _values = new ArrayList<String>();
            for (String value : values) {
                _values.add(value);
            }
        }
    }

    public XValue(XPath xpath, String... values) {
        this(xpath.xpath(), xpath.ename(), values);
    }

    public String xpath() {
        return _xpath;
    }

    public String ename() {
        return _ename;
    }

    public List<String> values() {
        return _values;
    }

    public String value() {
        if (_values == null || _values.isEmpty()) {
            return null;
        }
        return _values.get(0);
    }

    public void addValue(String value) {
        if (_values == null) {
            _values = new ArrayList<String>();
        }
        _values.add(value);
    }

    public void setValues(Collection<String> values) {
        if (values != null && !values.isEmpty()) {
            if (_values == null) {
                _values = new ArrayList<String>();
            } else {
                _values.clear();
            }
            _values.addAll(values);
        }
    }

    public void setValue(String value) {
        setValues(ListUtil.list(value));
    }

}

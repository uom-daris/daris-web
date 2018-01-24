package daris.web.client.model.query;

import arc.mf.client.util.ObjectUtil;

public class XPath {

    private String _xpath;
    private String _ename;

    public XPath(String xpath, String ename) {
        _xpath = xpath;
        _ename = ename;
    }

    public String xpath() {
        return _xpath;
    }

    public String ename() {
        return _ename;
    }

    public String toString() {
        return _xpath;
    }

    public int hashCode() {
        return _ename == null ? _xpath.hashCode() : ((_xpath + " " + _ename).hashCode());
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof XPath)) {
            XPath xo = (XPath) o;
            return ObjectUtil.equals(xo.xpath(), _xpath) && ObjectUtil.equals(xo.ename(), _ename);
        }
        return false;
    }
}

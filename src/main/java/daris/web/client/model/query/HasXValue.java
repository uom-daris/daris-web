package daris.web.client.model.query;

import java.util.Collection;

public interface HasXValue {

    Collection<XValue> xvalues();

    XValue xvalue(XPath xpath);

    void setXValue(XPath xpath, Collection<String> values);

    void setXValue(XPath xpath, String value);
    
    boolean hasXValues();

}

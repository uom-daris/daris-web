package daris.web.client.gui.query.item;

import arc.gui.form.FieldDefinition;
import arc.mf.dtype.EnumerationType;

public class EnumFilterField<T> extends FilterField<T> {

    private String _xpath;

    public EnumFilterField(String xpath, String name, String description, EnumerationType<T> type) {
        super(new FieldDefinition(name, type, description, null, 0, 1));
        _xpath = xpath;
    }

    public String xpath() {
        return _xpath;
    }

    @Override
    public void save(StringBuilder sb) {
        String value = valueAsString();
        if (value != null) {
            if (xpath().indexOf('/') != -1) {
                sb.append("xpath(");
            }
            sb.append(xpath());
            if (xpath().indexOf('/') != -1) {
                sb.append(")");
            }
            sb.append("=");
            sb.append("'").append(valueAsString()).append("'");
        }
    }

}

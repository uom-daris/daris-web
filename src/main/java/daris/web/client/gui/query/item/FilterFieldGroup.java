package daris.web.client.gui.query.item;

import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.mf.dtype.DocType;
import daris.web.client.model.query.Filter;

public abstract class FilterFieldGroup extends FieldGroup implements Filter {

    private String _xpath;

    public FilterFieldGroup(String xpath, String title, String name, String description, String helpText, int minOccurs,
            int maxOccurs) {
        super(new FieldDefinition(title, name, DocType.DEFAULT, description, helpText, minOccurs, maxOccurs));
        _xpath = xpath;
    }

    public String xpath() {
        return _xpath;
    }

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        save(sb);
        return sb.toString();
    }

}

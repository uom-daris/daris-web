package daris.web.client.gui.query.item;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import daris.web.client.model.query.Filter;

public abstract class FilterField<T> extends Field<T> implements Filter {

    public FilterField(FieldDefinition defn) {
        super(defn);
    }

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        save(sb);
        return sb.toString();
    }

}

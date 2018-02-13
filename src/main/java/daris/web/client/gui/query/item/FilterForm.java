package daris.web.client.gui.query.item;

import arc.gui.form.Field;
import arc.gui.form.FieldGroup;
import arc.gui.form.Form;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.model.query.ComplexFilter;
import daris.web.client.model.query.Filter;

public class FilterForm extends Form implements Filter {

    private ComplexFilter _cf;

    public FilterForm() {
        super();
        _cf = new ComplexFilter();
        setPadding(20);
        setShowDescriptions(false);
        setShowHelp(false);
        setFontSize(DefaultStyles.HTML_FONT_SIZE);
        setSpacing(DefaultStyles.FORM_SPACING);
    }

    public <T> Field<T> add(Field<T> field) {
        super.add(field);
        if (field instanceof FilterField) {
            _cf.addFilter((FilterField<?>) field);
        }
        return field;
    }

    public FieldGroup add(FieldGroup fieldGroup) {
        super.add(fieldGroup);
        if (fieldGroup instanceof FilterFieldGroup) {
            _cf.addFilter((FilterFieldGroup) fieldGroup);
        }
        return fieldGroup;
    }

    @Override
    public void save(StringBuilder sb) {
        _cf.save(sb);
    }

    @Override
    public String toQueryString() {
        return _cf.toQueryString();
    }

}

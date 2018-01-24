package daris.web.client.gui.query.item;

import java.util.ArrayList;
import java.util.List;

import arc.gui.form.FieldGroup;
import arc.gui.form.FormItem;

public class FieldGroupUtil {
    @SuppressWarnings("rawtypes")
    public static void removeAllFields(FieldGroup fg) {
        List<FormItem> fields = fg.fields();
        if (fields != null && !fields.isEmpty()) {
            List<FormItem> fs = new ArrayList<FormItem>(fields);
            for (FormItem field : fs) {
                fg.remove(field);
            }
        }
    }
}

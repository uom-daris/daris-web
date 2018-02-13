package daris.web.client.gui.form;

import com.google.gwt.dom.client.Style.FontWeight;

import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.gwt.form.FormLabel;
import arc.gui.gwt.style.StyleRegistry;
import daris.web.client.gui.widget.DefaultStyles;

public class FormUtil {

    private static boolean _initializedStyles = false;

    private static void initializeStyles() {
        if (!_initializedStyles) {
            Form.setFormFontSize(DefaultStyles.FORM_FONT_SIZE);
            // @formatter:off
            StyleRegistry.register(FormLabel.class)
            .setFontFamily(DefaultStyles.FONT_FAMILY)
            .setFontSize(Form.formFontSize())
            .setMarginRight(4)
            .setLineHeight(100)
            .setMarginTop(2)
            .setWordWrap(false)
            .defineVariant("mandatory")
                .setFontWeight(FontWeight.BOLD);
            // @formatter:on
        }
    }

    public static Form createForm(FormEditMode mode) {
        initializeStyles();
        Form form = new Form(mode);
        form.setSpacing(DefaultStyles.FORM_SPACING);
        form.setPadding(DefaultStyles.FORM_PADDING);
        return form;
    }

    public static Form createForm() {
        return createForm(FormEditMode.CREATE);
    }

}

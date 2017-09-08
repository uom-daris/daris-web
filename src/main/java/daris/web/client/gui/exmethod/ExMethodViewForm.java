package daris.web.client.gui.exmethod;

import arc.gui.form.Form;
import daris.web.client.gui.form.XmlMetaForm;
import daris.web.client.gui.object.DObjectViewForm;
import daris.web.client.model.exmethod.ExMethod;

public class ExMethodViewForm extends DObjectViewForm<ExMethod> {

    public static final String EXPERIMENTAL_WORKFLOW_TAB_NAME = "Experimental Workflow";

    public ExMethodViewForm(ExMethod o) {
        super(o);
    }

    @Override
    protected void appendToInterfaceForm(Form form) {
        ExMethod o = object();
        if (o.methodElement() != null) {
            XmlMetaForm.addDocForView(form, o.methodElement());
        }
    }

    @Override
    protected void updateOtherTabs() {
        // TODO: add experimental workflow (method graph)...
    }
}

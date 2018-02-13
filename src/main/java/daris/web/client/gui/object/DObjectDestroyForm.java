package daris.web.client.gui.object;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;
import arc.mf.dtype.CiteableIdType;
import daris.web.client.gui.widget.MessageBox;
import daris.web.client.gui.widget.SummaryTable;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.messages.DObjectDestroy;
import daris.web.client.util.SizeUtil;

public class DObjectDestroyForm extends ValidatedInterfaceComponent implements AsynchronousAction {

    private DObject _o;
    private CollectionSummary _summary;

    private SimplePanel _container;

    public DObjectDestroyForm(DObject o, CollectionSummary summary) {
        _o = o;
        _summary = summary;
        _container = new SimplePanel();
        _container.fitToParent();
        updateGUI(_container);
    }

    protected void updateGUI(SimplePanel container) {

        VerticalPanel vp = new VerticalPanel();
        vp.fitToParent();

        SummaryTable summaryTable = SummaryTable.create("Summary of " + _o.typeAndId(), "Name", _o.name(),
                "Description", _o.description(), "Number of datasets",
                _o.objectType() == DObject.Type.DATASET ? null : String.valueOf(_summary.numberOfDatasets()),
                "Total size of contents", SizeUtil.getHumanReadableSize(_summary.totalContentSize()));
        summaryTable.fitToParent();
        vp.add(summaryTable);

        StringBuilder sb = new StringBuilder();
        sb.append("Are you sure you want to destroy " + _o.typeAndId() + "?");
        if (_o.objectType() == DObject.Type.PROJECT) {
            sb.append("<br>Type " + _o.objectType() + " id <b><i>" + _o.citeableId() + "</i></b> below to confirm:");
        }
        HTML messageHtml = new HTML(sb.toString());
        messageHtml.setFontSize(14);
        messageHtml.setFontFamily("Helvetica, sans-serif");
        messageHtml.setHeight(50);
        messageHtml.setWidth100();
        messageHtml.setTextAlign(TextAlign.CENTER);
        messageHtml.setPaddingTop(5);
        messageHtml.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_CCC);
        messageHtml.element().getStyle().setLineHeight(20, Unit.PX);
        messageHtml.element().getStyle().setWhiteSpace(WhiteSpace.PRE_WRAP);
        vp.add(messageHtml);

        if (_o.objectType() == DObject.Type.PROJECT) {
            final Field<String> cidField = new Field<String>("cid", CiteableIdType.DEFAULT, null, 1, 1);
            cidField.setRenderOptions(new FieldRenderOptions().setWidth(280));
            final Form form = new Form() {
                public Validity valid() {
                    if (_o.citeableId().equals(cidField.valueAsString())) {
                        return IsValid.INSTANCE;
                    } else {
                        return new IsNotValid("Missing " + _o.objectType() + " id: " + _o.citeableId());
                    }
                }
            };
            form.setShowDescriptions(false);
            form.setShowHelp(false);
            form.setShowLabels(false);
            form.setHeight(40);
            form.setWidth(280);
            form.add(cidField);
            form.setPosition(Position.ABSOLUTE);
            form.render();

            AbsolutePanel formAP = new AbsolutePanel() {
                protected void doLayoutChildren() {
                    super.doLayoutChildren();
                    form.setLeft((width() - form.width()) / 2);
                }
            };
            formAP.setWidth100();
            formAP.setHeight(40);
            formAP.add(form);
            addMustBeValid(form);
            vp.add(formAP);
        }

        container.setContent(vp);
    }

    @Override
    public Widget gui() {
        return _container;
    }

    @Override
    public void execute(ActionListener l) {
        new DObjectDestroy(_o).send(r -> {
            MessageBox.show(200, 80, MessageBox.Position.CENTER, "Deleted " + _o.typeAndId(), 3);
        });
        l.executed(true);
    }

}

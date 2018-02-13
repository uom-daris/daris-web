package daris.web.client.gui.sink;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.colour.RGBA;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.FormUtil;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.exports.PathExpressionSetRef;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.sink.SinkEnum;
import daris.web.client.util.SizeUtil;
import daris.web.client.util.StringUtils;

public class SinkForm extends ValidatedInterfaceComponent {

    private DObject _o;
    private CollectionSummary _summary;
    private Sink _sink;

    private VerticalPanel _vp;
    private SimplePanel _sinkSelectFormSP;
    private Form _sinkSelectForm;
    private SimplePanel _sinkSettingsFormSP;
    private SinkSettingsForm _sinkSettingsForm;
    private HTML _status;

    public SinkForm(DObject o, CollectionSummary summary) {
        _o = o;
        _summary = summary;
        _sink = null;

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _sinkSelectFormSP = new SimplePanel();
        _sinkSelectFormSP.setHeight(160);
        _sinkSelectFormSP.setWidth100();
        _sinkSelectFormSP.setPaddingTop(20);
        _sinkSelectFormSP.setPaddingLeft(20);
        _sinkSelectFormSP.setPaddingRight(50);
        _sinkSelectFormSP.setPaddingBottom(10);
        _vp.add(_sinkSelectFormSP);

        initSinkSelectForm();

        _sinkSettingsFormSP = new SimplePanel();
        _sinkSettingsFormSP.setPaddingTop(10);
        _sinkSettingsFormSP.setPaddingLeft(20);
        _sinkSettingsFormSP.setPaddingRight(50);
        _sinkSettingsFormSP.setPaddingBottom(20);
        _sinkSettingsFormSP.fitToParent();
        _sinkSettingsFormSP.setBorder(1, RGB.GREY_DDD);
        _vp.add(_sinkSettingsFormSP);

        _status = new HTML();
        _status.setHeight(22);
        _status.setWidth100();
        _status.setTextAlign(TextAlign.CENTER);
        _status.setFontSize(11);
        _status.setFontWeight(FontWeight.BOLD);
        _status.setMarginLeft(20);
        _status.setColour(RGBA.RED);
        _status.setBorder(1, RGB.GREY_DDD);
        _vp.add(_status);

    }

    private void initSinkSelectForm() {
        _sinkSelectForm = FormUtil.createForm();
        _sinkSelectForm.setWidth100();
        _sinkSelectForm.setShowHelp(false);
        // _sinkSelectForm.setShowDescriptions(false);
        FieldGroup sourceCollection = new FieldGroup(
                new FieldDefinition("Source Collection", DocType.DEFAULT, "Source collection information", null, 1, 1));
        _sinkSelectForm.add(sourceCollection);
        Field<String> rootCID = new Field<String>(
                new FieldDefinition(StringUtils.upperCaseFirst(_o.objectType().name().toLowerCase()),
                        ConstantType.DEFAULT, null, null, 1, 1));
        rootCID.setInitialValue(_o.citeableId(), false);
        rootCID.setRenderOptions(new FieldRenderOptions().setWidth100());
        sourceCollection.add(rootCID);
        Field<Long> sourceDatasetCount = new Field<Long>(
                new FieldDefinition("Number of datasets", "nb-datasets", ConstantType.DEFAULT, null, null, 1, 1));
        sourceDatasetCount.setInitialValue(_summary.numberOfDatasets(), false);
        sourceDatasetCount.setRenderOptions(new FieldRenderOptions().setWidth100());
        sourceCollection.add(sourceDatasetCount);
        Field<String> sourceTotalContentSize = new Field<String>(
                new FieldDefinition("Total content size", "total-size", ConstantType.DEFAULT, null, null, 1, 1));
        sourceTotalContentSize.setInitialValue(SizeUtil.getHumanReadableSize(_summary.totalContentSize()), false);
        sourceTotalContentSize.setRenderOptions(new FieldRenderOptions().setWidth100());
        sourceCollection.add(sourceTotalContentSize);

        Field<Sink> sink = new Field<Sink>(new FieldDefinition("Destination Sink", "destination sink",
                new EnumerationType<Sink>(new SinkEnum()), "Select destination sink.", null, 1, 1));
        sink.addListener(new FormItemListener<Sink>() {

            @Override
            public void itemValueChanged(FormItem<Sink> f) {
                _sink = f.value() == null ? null : f.value().copy();
                _sink.loadFromUserSettings(r -> {
                    updateSinkSettingsForm();
                });
            }

            @Override
            public void itemPropertyChanged(FormItem<Sink> f, Property property) {

            }
        });
        sink.setRenderOptions(new FieldRenderOptions().setWidth100());
        _sinkSelectForm.add(sink);
        _sinkSelectForm.render();
        _sinkSelectFormSP.setContent(_sinkSelectForm);
        addMustBeValid(_sinkSelectForm);
    }

    private void updateSinkSettingsForm() {
        if (_sinkSettingsForm != null) {
            removeMustBeValid(_sinkSettingsForm);
            _sinkSettingsFormSP.clear();
            _sinkSettingsForm = null;
        }
        _sinkSettingsForm = new SinkSettingsForm(_sink, _summary.numberOfObjects() > 1,
                new PathExpressionSetRef(_o.projectCiteableId()));
        _sinkSettingsForm.render();
        _sinkSettingsFormSP.setContent(new ScrollPanel(_sinkSettingsForm, ScrollPolicy.AUTO));
        addMustBeValid(_sinkSettingsForm);
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid() && _sink == null) {
            v = new IsNotValid("No destination sink is selected.");
        }
        if (v.valid() && _sinkSettingsForm == null) {
            v = new IsNotValid("loading from previous user settings...");
        }
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public arc.gui.window.Window window() {
        return _vp.window();
    }

    public Sink sink() {
        return _sink;
    }

    public DObject root() {
        return _o;
    }

    public CollectionSummary summary() {
        return _summary;
    }

}

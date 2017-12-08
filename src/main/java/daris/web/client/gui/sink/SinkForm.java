package daris.web.client.gui.sink;

import java.util.Date;
import java.util.Set;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.Form.BooleanAs;
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
import arc.mf.dtype.DataType;
import arc.mf.dtype.DateType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.DoubleType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.FloatType;
import arc.mf.dtype.IntegerType;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.Session;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.exports.PathExpression;
import daris.web.client.model.object.exports.PathExpressionEnum;
import daris.web.client.model.object.exports.PathExpressionSetRef;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.sink.SinkConstants;
import daris.web.client.model.sink.SinkEnum;
import daris.web.client.model.sink.SinkType;
import daris.web.client.util.SizeUtil;
import daris.web.client.util.StringUtils;

public class SinkForm extends ValidatedInterfaceComponent {

    private DObject _o;
    private CollectionSummary _summary;
    private Sink _sink;
    private PathExpressionSetRef _pes;

    private VerticalPanel _vp;
    private SimplePanel _sinkSelectFormSP;
    private Form _sinkSelectForm;
    private SimplePanel _sinkSettingsFormSP;
    private Form _sinkSettingsForm;
    private HTML _status;

    public SinkForm(DObject o, CollectionSummary summary) {
        _o = o;
        _summary = summary;
        _sink = null;
        _pes = new PathExpressionSetRef(o.projectCiteableId());

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
        _sinkSettingsFormSP.setBorder(2, RGB.GREY_DDD);
        _vp.add(_sinkSettingsFormSP);

        _status = new HTML();
        _status.setHeight(22);
        _status.setWidth100();
        _status.setTextAlign(TextAlign.CENTER);
        _status.setFontSize(11);
        _status.setFontWeight(FontWeight.BOLD);
        _status.setMarginLeft(20);
        _status.setColour(RGBA.RED);
        _vp.add(_status);

    }

    private void initSinkSelectForm() {
        _sinkSelectForm = new Form();
        _sinkSelectForm.setSpacing(10);
        _sinkSelectForm.setWidth100();
        _sinkSelectForm.setShowHelp(false);
        _sinkSelectForm.setShowDescriptions(false);
        FieldGroup sourceCollection = new FieldGroup(new FieldDefinition("Source Collection", "source", DocType.DEFAULT,
                "Source collection information", null, 1, 1));
        _sinkSelectForm.add(sourceCollection);
        Field<String> sourceID = new Field<String>(
                new FieldDefinition(StringUtils.upperCaseFirst(_o.objectType().name().toLowerCase()), "cid",
                        ConstantType.DEFAULT, null, null, 1, 1));
        sourceID.setInitialValue(_o.citeableId(), false);
        sourceID.setRenderOptions(new FieldRenderOptions().setWidth100());
        sourceCollection.add(sourceID);
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
        _sinkSettingsForm = new Form();
        _sinkSettingsForm.setSpacing(10);
        _sinkSettingsForm.setBooleanAs(BooleanAs.CHECKBOX);
        _sinkSettingsForm.fitToParent();

        Set<String> argNames = _sink.type().argNames();
        if (argNames != null) {
            for (String argName : argNames) {
                if (_summary.numberOfObjects() > 1 && _sink.isAssetSpecific(argName)) {
                    // skip single consumption arg if there are multiple inputs.
                    continue;
                }
                if (_sink.isAdminArg(argName)) {
                    // skip admin only arguments.
                    continue;
                }
                _sinkSettingsForm.add(fieldFor(_sink, argName));
            }
        }
        _sinkSettingsForm.render();
        _sinkSettingsFormSP.setContent(new ScrollPanel(_sinkSettingsForm, ScrollPolicy.AUTO));
        addMustBeValid(_sinkSettingsForm);
    }

    @SuppressWarnings({ "rawtypes" })
    private Field<?> fieldFor(final Sink sink, final String argName) {
        final SinkType.ArgumentDefinition argDefn = sink.type().argDefn(argName);
        boolean mutable = sink.isMutableArg(argName);
        DataType type = argDefn.type();
        if (SinkType.isLayoutPatternArg(argName)) {
            type = new EnumerationType<PathExpression>(new PathExpressionEnum(_pes));
        }
        String argValue = sink.argValue(argName);

        Field<?> field = new Field(new FieldDefinition(argName, mutable ? type : ConstantType.DEFAULT,
                argDefn.description(), null, argDefn.optional() ? 0 : 1, 1)) {
            public Validity valid() {
                Validity v = super.valid();
                if (v.valid()) {
                    if (!hasSomeValue() && argDefn.xorArgs() != null) {
                        boolean anyHasValue = false;
                        String[] xorArgs = argDefn.xorArgs();
                        for (String xorArg : xorArgs) {
                            Field f = (Field) fieldSet().fieldByName(xorArg);
                            if (f.hasSomeValue()) {
                                anyHasValue = true;
                                break;
                            }
                        }
                        if (!anyHasValue) {
                            StringBuilder sb = new StringBuilder("One of the following fields: ");
                            sb.append(name());
                            for (String xorArg : xorArgs) {
                                sb.append(", ").append(xorArg);
                            }
                            sb.append(" must be specified.");
                            return new IsNotValid(sb.toString());
                        }
                    }
                }
                return v;
            }
        };
        if (argValue == null && argDefn.defaultValue() != null) {
            setInitialValue(field, type, argDefn.defaultValue(), false);
        } else {
            setInitialValue(field, type, argValue, false);
        }
        FormItemListener listener = new FormItemListener() {

            @Override
            public void itemValueChanged(FormItem f) {
                Object v = f.value();
                if (v != null && SinkType.isLayoutPatternArg(argName) && (v instanceof PathExpression)) {
                    sink.setArg(argName, ((PathExpression) v).expression);
                } else {
                    sink.setArg(argName, f.valueAsString());
                }
            }

            @Override
            public void itemPropertyChanged(FormItem f, Property property) {

            }
        };
        if (mutable) {
            field.addListener(listener);
        }
        field.setRenderOptions(new FieldRenderOptions().setWidth100());
        return field;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setInitialValue(Field f, DataType type, Object value, boolean fireEvents) {
        if (value == null) {
            f.setValue(null, fireEvents);
        } else {
            if ((type instanceof EnumerationType) && SinkConstants.SINK_ARG_LAYOUT_PATTERN.equals(f.name())) {
                if (value instanceof PathExpression) {
                    f.setInitialValue(value, fireEvents);
                } else {
                    resolvePathExpressionFromValue(String.valueOf(value), pe -> {
                        f.setInitialValue(pe, fireEvents);
                    });
                }
            } else if (type instanceof IntegerType) {
                if (value instanceof Integer) {
                    f.setInitialValue((Integer) value, fireEvents);
                } else {
                    f.setInitialValue(Integer.parseInt(String.valueOf(value)), fireEvents);
                }
            } else if (type instanceof FloatType) {
                if (value instanceof Float) {
                    f.setInitialValue((Float) value, fireEvents);
                } else {
                    f.setInitialValue(Float.parseFloat(String.valueOf(value)), fireEvents);
                }
            } else if (type instanceof DoubleType) {
                if (value instanceof DoubleType) {
                    f.setInitialValue((Double) value, fireEvents);
                } else {
                    f.setInitialValue(Double.parseDouble(String.valueOf(value)), fireEvents);
                }
            } else if (type instanceof DateType) {
                if (value instanceof Date) {
                    f.setInitialValue((Date) value, fireEvents);
                } else {
                    try {
                        f.setInitialValue(DateType.parseDate(String.valueOf(value)), fireEvents);
                    } catch (Throwable e) {
                        Session.displayError("Failed to parse date value: '" + value + "' Error: " + e.getMessage(), e);
                    }
                }
            } else {
                f.setInitialValue(String.valueOf(value), fireEvents);
            }
        }
    }

    private void resolvePathExpressionFromValue(String value, ObjectResolveHandler<PathExpression> rh) {
        _pes.resolve(pes -> {
            if (pes != null) {
                for (PathExpression pe : pes) {
                    if (value.equals(pe.expression)) {
                        rh.resolved(pe);
                        return;
                    }
                }
            }
            rh.resolved(null);
        });
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

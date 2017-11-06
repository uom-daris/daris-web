package daris.web.client.gui.sink;

import java.util.Date;
import java.util.Set;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
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
import arc.mf.dtype.DoubleType;
import arc.mf.dtype.FloatType;
import arc.mf.dtype.IntegerType;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.Session;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.sink.SinkType;

public class SinkSettingsForm extends ValidatedInterfaceComponent {

    private CollectionSummary _summary;
    private Sink _sink;

    private VerticalPanel _vp;
    private SimplePanel _formSP;
    private HTML _status;
    private Form _form;

    public SinkSettingsForm(CollectionSummary summary, Sink sink) {

        _summary = summary;
        _sink = sink.copy();

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _formSP = new SimplePanel();
        _formSP.setMarginTop(20);
        _formSP.setMarginLeft(20);
        _formSP.setMarginRight(20);
        _formSP.setMarginBottom(10);
        _formSP.setBorder(1, RGB.GREY_DDD);

        _formSP.fitToParent();
        _vp.add(_formSP);

        _status = new HTML();
        _status.setHeight(22);
        _status.setFontSize(11);
        _status.setFontWeight(FontWeight.BOLD);
        _status.setMarginLeft(20);
        _status.setColour(RGBA.RED);
        _vp.add(_status);

        _sink.loadFromUserSettings(new ObjectMessageResponse<Null>() {

            @Override
            public void responded(Null r) {
                try {
                    updateForm();
                } catch (Throwable e) {
                    Session.displayError("", e);
                }
            }
        });
    }

    private void updateForm() throws Throwable {
        if (_form != null) {
            removeMustBeValid(_form);
            _form = null;
        }
        _form = new Form();
        _form.setBooleanAs(BooleanAs.CHECKBOX);
        _form.setPaddingLeft(10);
        _form.setPaddingTop(10);
        _form.fitToParent();

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
                _form.add(fieldFor(_sink, argName));
            }
        }
        _form.render();
        addMustBeValid(_form);
        _formSP.setContent(new ScrollPanel(_form, ScrollPolicy.AUTO));
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid() && _form == null) {
            v = new IsNotValid("loading from previous user settings...");
        }
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @SuppressWarnings({ "rawtypes" })
    private static Field<?> fieldFor(final Sink sink, final String argName) throws Throwable {
        final SinkType.ArgumentDefinition argDefn = sink.type().argDefn(argName);
        boolean mutable = sink.isMutableArg(argName);
        DataType type = argDefn.type();
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
                sink.setArg(argName, f.valueAsString());
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
    private static void setInitialValue(Field f, DataType type, Object value, boolean fireEvents) throws Throwable {
        if (value == null) {
            f.setValue(null, fireEvents);
        } else {
            if (type instanceof IntegerType) {
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
                    f.setInitialValue(DateType.parseDate(String.valueOf(value)), fireEvents);
                }
            } else {
                f.setInitialValue(String.valueOf(value), fireEvents);
            }
        }
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public Sink sink() {
        return _sink;
    }

}

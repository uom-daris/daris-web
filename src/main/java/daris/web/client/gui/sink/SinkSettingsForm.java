package daris.web.client.gui.sink;

import java.util.Date;
import java.util.List;
import java.util.Set;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.form.HasFormItem;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.ListUtil;
import arc.mf.client.util.Validity;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.DataType;
import arc.mf.dtype.DateType;
import arc.mf.dtype.DoubleType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.FloatType;
import arc.mf.dtype.IntegerType;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.Session;
import daris.web.client.model.object.exports.PathExpression;
import daris.web.client.model.object.exports.PathExpressionEnum;
import daris.web.client.model.object.exports.PathExpressionSetRef;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.sink.SinkConstants;
import daris.web.client.model.sink.SinkType;

public class SinkSettingsForm extends Form {

    public static final List<String> ARGS_HIDDEN_FOR_SHOPPINGCART = ListUtil.list("unarchive", "decompress",
            "layout-pattern", "parts");

    private PathExpressionSetRef _pes;

    private Sink _sink;

    private boolean _mayContainMultipleItems;

    public SinkSettingsForm(Sink sink, boolean mayContainMultipleItems, PathExpressionSetRef pes) {

        _sink = sink;
        _mayContainMultipleItems = mayContainMultipleItems;
        _pes = pes;

        setSpacing(10);
        setBooleanAs(BooleanAs.CHECKBOX);
        fitToParent();

        addSinkArgumentFields(this, FormEditMode.UPDATE, _sink, _mayContainMultipleItems, _pes, false);
    }

    public void setMayContainMultipleItems(boolean mayContainMultipleItems) {
        _mayContainMultipleItems = mayContainMultipleItems;
    }

    public boolean mayContainMultipleItems() {
        return _mayContainMultipleItems;
    }

    public void setAvailablePathExpressions(PathExpressionSetRef pes) {
        _pes = pes;
    }

    public PathExpressionSetRef availablePathExpressions() {
        return _pes;
    }

    public static void addSinkArgumentFields(HasFormItem form, FormEditMode mode, Sink sink,
            boolean mayContainMultipleItems, PathExpressionSetRef pes, boolean viaShoppingCart) {
        Set<String> argNames = sink.type().argNames();
        if (argNames != null) {
            for (String argName : argNames) {
                if (mayContainMultipleItems && sink.isAssetSpecific(argName)) {
                    // skip single consumption arg if there are multiple inputs.
                    continue;
                }
                if (sink.isAdminArg(argName)) {
                    // skip admin only arguments.
                    continue;
                }
                if (viaShoppingCart && ARGS_HIDDEN_FOR_SHOPPINGCART.contains(argName)) {
                    // skip the args that should hidden if calling from shopping
                    // cart.
                    continue;
                }
                if (mode == FormEditMode.READ_ONLY) {
                    if(sink.argValue(argName)==null){
                        continue;
                    }
                }
                form.add(fieldFor(sink, argName, pes));
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private static Field<?> fieldFor(final Sink sink, final String argName, PathExpressionSetRef pes) {
        final SinkType.ArgumentDefinition argDefn = sink.type().argDefn(argName);
        boolean mutable = sink.isMutableArg(argName);
        DataType type = argDefn.type();
        if (SinkType.isLayoutPatternArg(argName)) {
            type = new EnumerationType<PathExpression>(new PathExpressionEnum(pes));
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
            setInitialValue(field, type, argDefn.defaultValue(), false, pes);
        } else {
            setInitialValue(field, type, argValue, false, pes);
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
    private static void setInitialValue(Field f, DataType type, Object value, boolean fireEvents,
            PathExpressionSetRef pes) {
        if (value == null) {
            f.setValue(null, fireEvents);
        } else {
            if ((type instanceof EnumerationType) && SinkConstants.SINK_ARG_LAYOUT_PATTERN.equals(f.name())) {
                if (value instanceof PathExpression) {
                    f.setInitialValue(value, fireEvents);
                } else {
                    resolvePathExpressionFromValue(pes, String.valueOf(value), pe -> {
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

    private static void resolvePathExpressionFromValue(PathExpressionSetRef pathExpressions, String value,
            ObjectResolveHandler<PathExpression> rh) {
        pathExpressions.resolve(pes -> {
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

}

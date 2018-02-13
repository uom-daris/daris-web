package daris.web.client.gui.shoppingcart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.FieldSet;
import arc.gui.form.Form;
import arc.gui.form.Form.BooleanAs;
import arc.gui.form.FormEditMode;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.StringType;
import daris.web.client.gui.form.FormUtil;
import daris.web.client.gui.sink.SinkSettingsForm;
import daris.web.client.model.object.exports.PathExpression;
import daris.web.client.model.object.exports.PathExpressionEnum;
import daris.web.client.model.object.exports.PathExpressionSetRef;
import daris.web.client.model.shoppingcart.Destination;
import daris.web.client.model.shoppingcart.DestinationEnum;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.DeliveryMethod;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.transcode.Transcode;
import daris.web.client.model.transcode.TranscodeEnum;

public class ShoppingCartSettingsForm extends ValidatedInterfaceComponent {

    private ShoppingCart _cart;

    private SimplePanel _formSP;

    private Form _form;

    private Map<String, Sink> _sinks;

    private Sink _sink;

    private PathExpressionSetRef _pes;

    public ShoppingCartSettingsForm(ShoppingCart cart) {

        _pes = new PathExpressionSetRef(null);

        _formSP = new SimplePanel();
        _formSP.setBorderBottom(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _formSP.fitToParent();

        setCart(cart);

        // addChangeListener(() -> {
        // if (valid().valid()) {
        // if (_sink != null) {
        // _cart.importDeliveryArgsFromSink(_sink);
        // }
        // }
        // });
    }

    public ShoppingCartSettingsForm() {
        this(null);
    }

    @Override
    public Widget gui() {
        return _formSP;
    }

    void setCart(ShoppingCart cart) {
        _cart = cart;
        updateForm();
    }

    private void updateForm() {
        _formSP.clear();
        if (_form != null) {
            removeMustBeValid(_form);
            _form = null;
        }
        if (_cart == null) {
            return;
        }

        _form = FormUtil.createForm(_cart.status() == Status.EDITABLE ? FormEditMode.UPDATE : FormEditMode.READ_ONLY);
        _form.setBooleanAs(BooleanAs.CHECKBOX);
        _form.setWidth100();
        _form.setMargin(20);
        _form.setSpacing(15);

        /*
         * ID
         */
        Field<Long> cartId = new Field<Long>(
                new FieldDefinition("id", ConstantType.DEFAULT, "Shopping cart ID", null, 1, 1));
        cartId.setInitialValue(_cart.id(), false);
        cartId.setVisible(false);
        _form.add(cartId);

        /*
         * Name
         */
        if (_cart.name() != null) {
            Field<String> cartName = new Field<String>(
                    new FieldDefinition("name", StringType.DEFAULT, "Shopping cart name", null, 0, 1));
            cartName.setInitialValue(_cart.name(), false);
            cartName.addListener(new FormItemListener<String>() {

                @Override
                public void itemValueChanged(FormItem<String> f) {
                    _cart.setName(f.value());
                }

                @Override
                public void itemPropertyChanged(FormItem<String> f, Property property) {

                }
            });
            _form.add(cartName);
        }

        /*
         * Description
         */
        if (_cart.description() != null) {
            Field<String> cartDescription = new Field<String>(
                    new FieldDefinition("description", StringType.DEFAULT, "Shopping cart description", null, 0, 1));
            cartDescription.setInitialValue(_cart.description(), false);
            cartDescription.addListener(new FormItemListener<String>() {

                @Override
                public void itemValueChanged(FormItem<String> f) {
                    _cart.setDescription(f.value());
                }

                @Override
                public void itemPropertyChanged(FormItem<String> f, Property property) {

                }
            });
            _form.add(cartDescription);
        }

        /*
         * Decompress
         */
        Field<Boolean> decompressArchive = new Field<Boolean>(new FieldDefinition("unarchive",
                BooleanType.DEFAULT_TRUE_FALSE, "Unpack archive contents", null, 0, 1));
        decompressArchive.setInitialValue(_cart.decompressArchive(), false);
        decompressArchive.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                _cart.setDecompressArchive(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        _form.add(decompressArchive);

        /*
         * layout-pattern
         */
        Field<PathExpression> layoutPattern = new Field<PathExpression>(
                new FieldDefinition("layout-pattern", new EnumerationType<PathExpression>(new PathExpressionEnum(_pes)),
                        "Expression to generate the output path", null, 1, 1));
        if (_cart.layoutPattern() != null) {
            layoutPattern.setInitialValue(_cart.layoutPattern(), false);
        } else {
            _pes.resolve(pes -> {
                if (pes != null && !pes.isEmpty()) {
                    _cart.setLayoutPattern(pes.get(0));
                    layoutPattern.setInitialValue(pes.get(0));
                }
            });
        }
        layoutPattern.addListener(new FormItemListener<PathExpression>() {

            @Override
            public void itemValueChanged(FormItem<PathExpression> f) {
                _cart.setLayoutPattern(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<PathExpression> f, Property property) {

            }
        });
        _form.add(layoutPattern);

        /*
         * transcodes
         */
        Collection<Transcode> transcodes = _cart.transcodesAvailable();
        if (transcodes != null && !transcodes.isEmpty()) {
            for (Transcode transcode : transcodes) {
                Field<Transcode> tf = new Field<Transcode>(
                        new FieldDefinition("transcode '" + transcode.from() + "' to", transcode.from(),
                                new EnumerationType<Transcode>(new TranscodeEnum(transcode.from())),
                                "trancode from '" + transcode.from() + "'", null, 0, 1));
                tf.setInitialValue(transcode, false);
                tf.addListener(new FormItemListener<Transcode>() {

                    @Override
                    public void itemValueChanged(FormItem<Transcode> f) {
                        if (f.value() == null) {
                            _cart.setTranscode(Transcode.none(transcode.from()));
                        } else {
                            _cart.setTranscode(f.value());
                        }
                    }

                    @Override
                    public void itemPropertyChanged(FormItem<Transcode> f, Property property) {

                    }
                });
                _form.add(tf);
            }
        }

        /*
         * Destination
         */
        Field<Destination> destination = new Field<Destination>(new FieldDefinition("destination",
                new EnumerationType<Destination>(new DestinationEnum()), "Shopping cart destination", null, 1, 1));
        destination.setInitialValue(_cart.destination(), false);
        destination.addListener(new FormItemListener<Destination>() {

            @Override
            public void itemValueChanged(FormItem<Destination> f) {
                if (_cart.setDestination(f.value())) {
                    if (_cart.deliveryMethod() == DeliveryMethod.DOWNLOAD) {
                        _sink = null;
                        updateForm();
                    } else {
                        _cart.destination().sink().resolve(sink -> {
                            _sink = sink;
                            updateForm();
                        });
                    }
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<Destination> f, Property property) {

            }
        });
        _form.add(destination);

        if (_cart.deliveryMethod() == DeliveryMethod.DEPOSIT) {

            FieldGroup sinkFG = new FieldGroup(new FieldDefinition(_cart.destination().url, DocType.DEFAULT,
                    "Destination sink settings", null, 1, 1));
            String sinkName = _cart.destination().sinkName();
            if (_sinks != null && _sinks.containsKey(sinkName)) {
                _sink = _sinks.get(sinkName);
                _cart.applyDeliveryArgsToSink(_sink);
                addSinkArgumentFields(sinkFG,
                        _cart.status() == Status.EDITABLE ? FormEditMode.UPDATE : FormEditMode.READ_ONLY, _sink);
            } else {
                _cart.destination().sink().resolve(sink -> {
                    _sink = sink;
                    if (_sinks == null) {
                        _sinks = new HashMap<String, Sink>();
                    }
                    _sinks.put(_sink.name(), _sink);
                    _cart.applyDeliveryArgsToSink(_sink);
                    addSinkArgumentFields(sinkFG,
                            _cart.status() == Status.EDITABLE ? FormEditMode.UPDATE : FormEditMode.READ_ONLY, _sink);
                });
            }
            _form.add(sinkFG);
        }

        _form.render();

        _formSP.setContent(new ScrollPanel(_form, ScrollPolicy.AUTO));

        addMustBeValid(_form);
    }

    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_form == null || _form.needToRender()) {
                v = new IsNotValid("Initialising form...");
            }
        }
        return v;
    }

    public Sink sink() {
        return _sink;
    }

    private static void addSinkArgumentFields(FieldSet fields, FormEditMode mode, Sink sink) {
        SinkSettingsForm.addSinkArgumentFields(fields, mode, sink, true, new PathExpressionSetRef(null), true);
    }

}

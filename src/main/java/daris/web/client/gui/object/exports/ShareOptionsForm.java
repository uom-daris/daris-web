package daris.web.client.gui.object.exports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.Output;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.DateType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.IntegerType;
import arc.mf.dtype.TextType;
import arc.mf.model.authentication.Actor;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.exports.ArchiveFormat;
import daris.web.client.model.object.exports.Parts;
import daris.web.client.model.object.exports.ShareOptions;

public class ShareOptionsForm extends ExportOptionsForm<ShareOptions> implements AsynchronousAction {

    private VerticalPanel _vp;
    private SimplePanel _formSP;
    private HTML _status;
    private Form _form;
    private Field<String> _urlField;

    public ShareOptionsForm(DObject object, CollectionSummary summary) {
        super(object, summary, new ShareOptions());
        _vp = new VerticalPanel();
        _vp.fitToParent();

        _formSP = new SimplePanel();
        _formSP.fitToParent();
        _vp.add(_formSP);

        _status = new HTML();
        _status.setWidth100();
        _status.setHeight(20);
        _status.setFontSize(10);
        _status.setMarginLeft(3);
        _status.setMarginRight(3);
        _status.element().getStyle().setLineHeight(20, Unit.PX);
        _status.setBorder(1, RGB.GREY_EEE);
        _status.setTextAlign(TextAlign.CENTER);
        _status.setPaddingLeft(15);
        _status.setColour(RGB.RED);
        _vp.add(_status);

        updateForm();
    }

    private void updateForm() {

        _formSP.clear();

        /*
         * update options
         */
        if (summary.totalContentSize() == 0) {
            options.setParts(Parts.META);
            options.setDecompress(false);
            options.setCompress(false);
            options.setArchiveFormat(ArchiveFormat.ZIP);
            if (options.includeAttachments()) {
                options.setIncludeAttachments(false);
            }
        }
        if (summary.numberOfAttachments() == 0) {
            options.setIncludeAttachments(false);
        }
        if (object.isLeaf()) {
            if (object.hasContent()) {
                if (object.hasArchiveContent()) {
                    if (object.content().isAAR()) {
                        options.setDecompress(true);
                        options.setArchiveFormat(ArchiveFormat.ZIP);
                    }
                } else {
                    options.setDecompress(false);
                }
            } else {
                options.setParts(Parts.META);
            }
            if (!object.hasAttachments()) {
                options.setIncludeAttachments(false);
            }
        }

        /*
         * update form
         */
        if (_form != null) {
            removeMustBeValid(_form);
        }
        _form = new Form();
        _form.setPaddingTop(25);
        _form.setPaddingLeft(25);
        // parts
        Field<Parts> partsField = new Field<Parts>(new FieldDefinition("Parts", "parts",
                new EnumerationType<Parts>(
                        summary.totalContentSize() != 0 ? Parts.values() : new Parts[] { Parts.META }),
                null, null, 1, 1));
        partsField.setInitialValue(options.parts(), false);
        partsField.addListener(new FormItemListener<Parts>() {

            @Override
            public void itemValueChanged(FormItem<Parts> f) {
                options.setParts(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Parts> f, Property property) {

            }
        });
        if (summary.totalContentSize() == 0) {
            partsField.setReadOnly();
        }
        _form.add(partsField);

        /*
         * include attachments?
         */
        if (summary.numberOfAttachments() > 0) {
            Field<Boolean> includeAttachmentsField = new Field<Boolean>(new FieldDefinition("Include attchments",
                    "Include attachments", BooleanType.DEFAULT_TRUE_FALSE, "Include attachments", null, 1, 1));
            includeAttachmentsField.setInitialValue(options.includeAttachments());
            includeAttachmentsField.addListener(new FormItemListener<Boolean>() {

                @Override
                public void itemValueChanged(FormItem<Boolean> f) {
                    options.setIncludeAttachments(f.value());
                }

                @Override
                public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

                }
            });
            _form.add(includeAttachmentsField);
        }

        /*
         * decompress
         */
        if (!object.isLeaf() || object.hasArchiveContent()) {
            final Field<Boolean> decompressField = new Field<Boolean>(new FieldDefinition("Unpack", "Unpack",
                    BooleanType.DEFAULT_TRUE_FALSE, "Unpack archive content", null, 1, 1));
            decompressField.setInitialValue(options.decompress());
            decompressField.addListener(new FormItemListener<Boolean>() {

                @Override
                public void itemValueChanged(FormItem<Boolean> f) {
                    options.setDecompress(f.value());
                }

                @Override
                public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

                }
            });
            _form.add(decompressField);
        }

        /*
         * transcodes.
         */
        if (summary.hasTranscodes()) {
            Map<String, Collection<String>> transcodes = summary.transcodes();
            if (transcodes != null && !transcodes.isEmpty()) {
                Set<String> froms = transcodes.keySet();
                for (String from : froms) {
                    List<String> tos = new ArrayList<String>();
                    tos.add("none");
                    tos.addAll(transcodes.get(from));
                    Field<String> tf = new Field<String>(new FieldDefinition("Transcode " + from + " to", from,
                            new EnumerationType<String>(tos), null, null, 0, 1));
                    String to = options.transcodeFor(from);
                    tf.setInitialValue(to == null ? "none" : to);
                    tf.addListener(new FormItemListener<String>() {

                        @Override
                        public void itemValueChanged(FormItem<String> f) {
                            if ("none".equals(f.value())) {
                                options.removeTranscode(f.name());
                            } else {
                                options.addTranscode(f.name(), f.value());
                            }
                        }

                        @Override
                        public void itemPropertyChanged(FormItem<String> f, Property property) {

                        }
                    });
                    _form.add(tf);
                }
            }
        }

        /*
         * number of uses
         */
        Field<Integer> usesField = new Field<Integer>(
                new FieldDefinition("Max Uses", "Max Uses", IntegerType.POSITIVE_ONE, null, null, 0, 1));
        usesField.setInitialValue(options.maxUses());
        usesField.addListener(new FormItemListener<Integer>() {

            @Override
            public void itemValueChanged(FormItem<Integer> f) {
                if (f.value() == null || f.value() <= 0) {
                    options.setMaxUses(null);
                } else {
                    options.setMaxUses(f.value());
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<Integer> f, Property property) {

            }
        });
        _form.add(usesField);

        /*
         * expiry date
         */
        Field<Date> expiryDateField = new Field<Date>(
                new FieldDefinition("Expiry date", "expiry date", DateType.DATE_ONLY, null, null, 0, 1));
        expiryDateField.setInitialValue(options.expiryDate());
        expiryDateField.addListener(new FormItemListener<Date>() {

            @Override
            public void itemValueChanged(FormItem<Date> f) {
                options.setExpiryDate(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Date> f, Property property) {

            }
        });
        _form.add(expiryDateField);

        /*
         * url
         */
        _urlField = new Field<String>(new FieldDefinition("Download URL", "url", TextType.DEFAULT, null, null, 0, 1));
        _urlField.setWidth(400);
        _urlField.setHeight(150);
        _form.add(_urlField);

        _form.render();
        addMustBeValid(_form);
        _formSP.setContent(new ScrollPanel(_form, ScrollPolicy.AUTO));

    }

    @Override
    public Validity valid() {
        Validity validity = super.valid();
        if (!validity.valid()) {
            _status.setHTML(validity.reasonForIssue());
            return validity;
        }
        if (options.maxUses() == null && options.expiryDate() == null) {
            validity = new IsNotValid("Either 'Max uses' or 'Expiry date' must be set.");
        }
        if (options.expiryDate() != null && options.expiryDate().getTime() < new Date().getTime()) {
            validity = new IsNotValid("'Expiry date' is invalid. It is earlier than today.");
        }
        if (validity.valid()) {
            _status.clear();
        } else {
            _status.setHTML(validity.reasonForIssue());
        }
        return validity;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(ActionListener al) {

        Session.execute("actor.self.describe", (xe, outputs) -> {
            Actor actor = new Actor(xe.value("actor/@name"), xe.value("actor/@type"));
            if (object.hasContent() && summary.numberOfObjects() == 1 && options.parts() == Parts.CONTENT
                    && !options.includeAttachments() && !options.hasTranscodes() && !options.decompress()) {
                generateContentUrl(actor, al);
            } else {
                generateArchiveUrl(actor, al);
            }
        });
    }

    private void generateContentUrl(Actor actor, ActionListener al) {
        XmlStringWriter w = new XmlStringWriter();
        w.add("role", new String[] { "type", actor.actorType() }, actor.actorName());
        w.push("service", new String[] { "name", "asset.get" });
        w.add("cid", object.citeableId());
        w.pop();
        w.add("min-token-length", 20);
        w.add("max-token-length", 20);
        w.add("grant-caller-transient-roles", true);

        if (options.expiryDate() != null && options.expiryDate().getTime() > new Date().getTime()) {
            w.add("to", options.expiryDate());
        }
        if (options.maxUses() != null && options.maxUses() > 0) {
            w.add("use-count", options.maxUses());
        }
        w.add("tag", "daris-share-url-" + object.citeableId());
        Session.execute("secure.identity.token.create", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                String token = xe.value("token");
                _urlField.setValue(contentUrlFor(object, token));
                _urlField.setFocus(true);
                _urlField.selectValue();
                if (al != null) {
                    al.executed(false);
                }
            }
        });
    }

    private void generateArchiveUrl(Actor actor, ActionListener al) {

        XmlStringWriter w = new XmlStringWriter();
        w.add("role", new String[] { "type", actor.actorType() }, actor.actorName());
        w.push("service", new String[] { "name", "daris.collection.archive.create" });
        w.add("cid", object.citeableId());
        w.add("parts", options.parts());
        w.add("include-attachments", options.includeAttachments());
        w.add("decompress", options.decompress());
        w.add("format", options.archiveFormat());
        if (options.compressionLevel() != null) {
            w.add("clevel", options.compressionLevel());
        }
        Map<String, String> transcodes = options.transcodes();
        if (transcodes != null) {
            for (String from : transcodes.keySet()) {
                w.push("transcode");
                w.add("from", from);
                w.add("to", transcodes.get(from));
                w.pop();
            }
        }
        w.pop();
        w.add("min-token-length", 20);
        w.add("max-token-length", 20);
        w.add("grant-caller-transient-roles", true);

        if (options.expiryDate() != null && options.expiryDate().getTime() > new Date().getTime()) {
            w.add("to", options.expiryDate());
        }
        if (options.maxUses() != null && options.maxUses() > 0) {
            w.add("use-count", options.maxUses());
        }
        w.add("tag", "daris-share-url-" + object.citeableId());
        Session.execute("secure.identity.token.create", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                String token = xe.value("token");
                String ext = null;
                if (object.hasContent() && !options.decompress()) {
                    ext = object.content().ext();
                } else {
                    ext = options.archiveFormat().name();
                }
                _urlField.setValue(archiveUrlFor(object.citeableId(), ext, token));
                _urlField.setFocus(true);
                _urlField.selectValue();
                if (al != null) {
                    al.executed(false);
                }
            }
        });
    }

    private static String archiveUrlFor(String cid, String ext, String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(com.google.gwt.user.client.Window.Location.getProtocol());
        sb.append("//");
        sb.append(com.google.gwt.user.client.Window.Location.getHost());
        sb.append("/mflux/execute.mfjp?token=");
        sb.append(token);
        sb.append("&filename=");
        sb.append(cid);
        if (ext != null) {
            sb.append(".");
            sb.append(ext);
        }
        return sb.toString();
    }

    private static String contentUrlFor(DObject object, String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(com.google.gwt.user.client.Window.Location.getProtocol());
        sb.append("//");
        sb.append(com.google.gwt.user.client.Window.Location.getHost());
        sb.append("/mflux/execute.mfjp?token=");
        sb.append(token);
        sb.append("&filename=");
        if (object.filename() != null) {
            sb.append(object.filename());
        } else {
            sb.append(object.objectType());
            sb.append("_");
            sb.append(object.citeableId());
            if (object.content().ext() != null) {
                sb.append(".").append(object.content().ext());
            }
        }
        return sb.toString();
    }

}

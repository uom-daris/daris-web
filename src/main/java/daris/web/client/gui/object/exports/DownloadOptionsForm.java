package daris.web.client.gui.object.exports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.EnumerationType;
import daris.web.client.gui.form.FormUtil;
import daris.web.client.model.collection.messages.CollectionArchiveCreate;
import daris.web.client.model.object.CollectionSummary;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.exports.ArchiveFormat;
import daris.web.client.model.object.exports.DownloadOptions;
import daris.web.client.model.object.exports.Parts;
import daris.web.client.util.DownloadUtil;

public class DownloadOptionsForm extends ExportOptionsForm<DownloadOptions> implements AsynchronousAction {

    private VerticalPanel _vp;
    private SimplePanel _formSP;
    private Form _form;

    public DownloadOptionsForm(DObject object, String where, CollectionSummary summary) {
        super(object, where, summary, new DownloadOptions());
        _vp = new VerticalPanel();
        _vp.fitToParent();

        _formSP = new SimplePanel();
        _formSP.fitToParent();
        _vp.add(_formSP);
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
        if (object != null && object.isLeaf()) {
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
        _form = FormUtil.createForm();
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
        if (object == null || !object.isLeaf() || object.hasArchiveContent()) {
            final Field<Boolean> decompressField = new Field<Boolean>(new FieldDefinition("Unpack Archives",
                    "Unpack Archives", BooleanType.DEFAULT_TRUE_FALSE, "Unpack archive contents", null, 1, 1));
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

        _form.render();
        addMustBeValid(_form);
        _formSP.setContent(new ScrollPanel(_form, ScrollPolicy.AUTO));
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(ActionListener l) {
        if (object != null && object.hasContent() && summary.numberOfObjects() == 1 && options.parts() == Parts.CONTENT
                && !options.includeAttachments() && !options.hasTranscodes() && !options.decompress()) {
            l.executed(true);
            DownloadUtil.download(object.contentDownloadUrl());
        } else {
            new CollectionArchiveCreate(new DObjectRef(object), where, options).send(r -> {
                l.executed(true);
            });
        }
    }

}

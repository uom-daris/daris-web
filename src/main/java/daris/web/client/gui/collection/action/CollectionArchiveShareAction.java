package daris.web.client.gui.collection.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.dialog.DialogProperties;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.Output;
import arc.mf.client.util.Action;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.Validity;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.dtype.DateType;
import arc.mf.dtype.IntegerType;
import arc.mf.dtype.TextType;
import arc.mf.model.authentication.Actor;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.gui.collection.CollectionArchiveOptionsForm;
import daris.web.client.model.collection.ArchiveOptions;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;

public class CollectionArchiveShareAction extends CollectionArchiveOptionsForm implements AsynchronousAction, Action {

    private int _useCount;
    private Date _expiryDate;

    private VerticalPanel _vp;
    private Field<String> _urlField;
    private HTML _status;

    public CollectionArchiveShareAction(DObjectRef obj, arc.gui.window.Window owner) {
        super(obj, owner);

        _expiryDate = new Date(new Date().getTime() + (1000L * 60L * 60L * 24L * 3L));

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _vp.add(super.gui());

        _status = new HTML();
        _status.setWidth100();
        _status.setHeight(22);
        _status.setPaddingLeft(15);
        _status.setColour(RGB.RED);
        _vp.add(_status);
    }

    @Override
    public Validity valid() {
        Validity validity = super.valid();
        if (!validity.valid()) {
            _status.setHTML(validity.reasonForIssue());
            return validity;
        }
        if (_useCount < 0 && _expiryDate == null) {
            validity = new IsNotValid("Either uses or expiry date must be set.");
        }
        if (_expiryDate != null && _expiryDate.getTime() < new Date().getTime()) {
            validity = new IsNotValid("Expiry date is invalid. It is earlier than today.");
        }
        if (validity.valid()) {
            _status.clear();
        } else {
            _status.setHTML(validity.reasonForIssue());
        }
        return validity;
    }

    protected void appendToForm(Form form) {
        /*
         * number of uses
         */
        Field<Integer> usesField = new Field<Integer>(
                new FieldDefinition("Uses", "uses", IntegerType.POSITIVE_ONE, null, null, 0, 1));
        usesField.addListener(new FormItemListener<Integer>() {

            @Override
            public void itemValueChanged(FormItem<Integer> f) {
                if (f.value() == null || f.value() == 0) {
                    _useCount = -1;
                } else {
                    _useCount = f.value();
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<Integer> f, Property property) {

            }
        });
        form.add(usesField);

        /*
         * expiry date
         */
        Field<Date> expiryDateField = new Field<Date>(
                new FieldDefinition("Expiry date", "expiry date", DateType.DATE_ONLY, null, null, 0, 1));
        expiryDateField.setInitialValue(_expiryDate);
        expiryDateField.addListener(new FormItemListener<Date>() {

            @Override
            public void itemValueChanged(FormItem<Date> f) {
                _expiryDate = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<Date> f, Property property) {

            }
        });
        form.add(expiryDateField);

        /*
         * url
         */
        _urlField = new Field<String>(new FieldDefinition("Download URL", "url", TextType.DEFAULT, null, null, 0, 1));
        _urlField.setWidth(400);
        _urlField.setHeight(150);
        form.add(_urlField);

    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(final ActionListener al) {
        Session.execute("actor.self.describe", (xe, outputs) -> {
            Actor actor = new Actor(xe.value("actor/@name"), xe.value("actor/@type"));
            generateSecureIdentityToken(actor, al);
        });
    }

    private void generateSecureIdentityToken(Actor actor, final ActionListener al) {
        XmlStringWriter w = new XmlStringWriter();
        w.add("role", new String[] { "type", actor.actorType() }, actor.actorName());
        DObjectRef o = object();
        ArchiveOptions options = archiveOptions();
        w.push("service", new String[] { "name", "daris.collection.archive.create" });
        w.add("cid", o.citeableId());
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

        if (_expiryDate != null && _expiryDate.getTime() > new Date().getTime()) {
            w.add("to", _expiryDate);
        }
        if (_useCount > 0) {
            w.add("use-count", _useCount);
        }
        w.add("tag", "daris-share-url-" + o.citeableId());
        Session.execute("secure.identity.token.create", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                final String token = xe.value("token");
                object().resolve(new ObjectResolveHandler<DObject>() {

                    @Override
                    public void resolved(DObject o) {
                        String ext = null;
                        if (o.hasContent() && !archiveOptions().decompress()) {
                            ext = o.content().ext();
                        } else {
                            ext = archiveOptions().archiveFormat().name();
                        }
                        _urlField.setValue(urlFor(o.citeableId(), ext, token));
                        _urlField.setFocus(true);
                        _urlField.selectValue();
                        if (al != null) {
                            al.executed(false);
                        }
                    }
                });
            }
        });
    }

    private static String urlFor(String cid, String ext, String token) {
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

    @Override
    public void execute() {
        DialogProperties dp = new DialogProperties(DialogProperties.Type.ACTION, "Generate Download URL", this);
        dp.setButtonAction(this);
        dp.setButtonLabel("Generate");
        dp.setActionEnabled(true);
        dp.setModal(false);
        dp.setOwner(ownerWindow());
        dp.setCancelLabel("Dismiss");
        dp.setSize(640, 440);
        Dialog.postDialog(dp).show();
    }

}

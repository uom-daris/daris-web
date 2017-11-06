package daris.web.client.gui.dicom.imports;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.file.FileFilter;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.Form.BooleanAs;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.file.LocalFile;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.Validity;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import daris.web.client.gui.dataset.FileForm;
import daris.web.client.gui.object.imports.FileUploadTaskManager;
import daris.web.client.model.dicom.imports.DicomIngestSettings;
import daris.web.client.model.dicom.imports.DicomIngestTask;
import daris.web.client.model.object.DObjectRef;

public class DicomIngestForm extends ValidatedInterfaceComponent implements AsynchronousAction {

    private DicomIngestSettings _settings;
    private VerticalPanel _vp;

    private FileForm _fileForm;
    private FileFilter _fileFilter;
    private HTML _status;

    public DicomIngestForm(DObjectRef po) {
        _settings = new DicomIngestSettings(po);

        _fileFilter = new FileFilter() {

            @Override
            public boolean accept(LocalFile f) {
                return f != null && !f.isDirectory() && (f.name().endsWith(".dcm") || f.name().endsWith(".DCM"));
            }
        };

        _vp = new VerticalPanel();
        _vp.fitToParent();

        Form settingsForm = new Form();
        settingsForm.setBooleanAs(BooleanAs.CHECKBOX);
        settingsForm.setHeight(160);
        settingsForm.setPadding(20);
        settingsForm.setWidth100();

        Field<String> cid = new Field<String>(new FieldDefinition("Destination " + po.referentTypeName(), "cid",
                ConstantType.DEFAULT, null, null, 1, 1));
        cid.setValue(po.citeableId(), false);
        settingsForm.add(cid);

        Field<Boolean> anonymize = new Field<Boolean>(new FieldDefinition("Anonymize", "anonymize",
                BooleanType.DEFAULT_TRUE_FALSE, "Anonymize Patient's Name (0010,0010) element", null, 0, 1));
        anonymize.setValue(_settings.anonymize(), false);
        anonymize.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                _settings.setAnonymize(f.value());
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        settingsForm.add(anonymize);

        Field<Boolean> includeOnlyDcmFiles = new Field<Boolean>(new FieldDefinition("Include only *.dcm files",
                "inc_only_dcm", BooleanType.DEFAULT_TRUE_FALSE, "Include only *.dcm files.", null, 0, 1));
        includeOnlyDcmFiles.setInitialValue(true, false);
        includeOnlyDcmFiles.addListener(new FormItemListener<Boolean>() {

            @Override
            public void itemValueChanged(FormItem<Boolean> f) {
                Boolean includeOnlyDcmFiles = f.value();
                if (includeOnlyDcmFiles != null && includeOnlyDcmFiles) {
                    _fileForm.setFileFilter(_fileFilter);
                } else {
                    _fileForm.setFileFilter(null);
                }
            }

            @Override
            public void itemPropertyChanged(FormItem<Boolean> f, Property property) {

            }
        });
        settingsForm.add(includeOnlyDcmFiles);

        settingsForm.render();
        _vp.add(settingsForm);
        addMustBeValid(settingsForm);

        _fileForm = new FileForm(true);
        _fileForm.setFileFilter(f -> {
            return f != null && !f.isDirectory() && (f.name().endsWith(".dcm") || f.name().endsWith(".DCM"));
        });
        _fileForm.fitToParent();
        _vp.add(_fileForm.widget());
        addMustBeValid(_fileForm);

        _status = new HTML();
        _status.setFontSize(10);
        _status.setColour(RGB.RED);
        _status.setTextAlign(TextAlign.CENTER);
        _status.setHeight(22);
        _status.setWidth100();
        _status.setBorder(1, ListGridHeader.HEADER_COLOUR_LIGHT);
        _vp.add(_status);
        addChangeListener(() -> {
            Validity v = valid();
            if (v.valid()) {
                _status.clear();
            } else {
                _status.setHTML(v.reasonForIssue());
            }
        });
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(ActionListener l) {
        l.executed(true);
        new DicomIngestTask(_settings, _fileForm.files().values()).execute(null, FileUploadTaskManager.get());
        FileUploadTaskManager.get().show(((BaseWidget) gui()).window());
    }

}

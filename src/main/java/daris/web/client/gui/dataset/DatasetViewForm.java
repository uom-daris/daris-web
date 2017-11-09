package daris.web.client.gui.dataset;

import java.util.List;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldGroup;
import arc.gui.form.Form;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.ConstantType;
import arc.mf.dtype.StringType;
import daris.web.client.gui.object.DObjectViewForm;
import daris.web.client.model.dataset.Dataset;
import daris.web.client.model.dataset.DerivedDataset;
import daris.web.client.model.dataset.DicomDataset;
import daris.web.client.model.dataset.NiftiDataset;
import daris.web.client.model.dataset.PrimaryDataset;
import daris.web.client.model.dataset.SourceType;

public class DatasetViewForm extends DObjectViewForm<Dataset> {

    public static final String PAPAYA_DICOM_VIEWER_TAB_NAME = "Papaya DICOM Viewer";
    public static final String SIMPLE_DICOM_VIEWER_TAB_NAME = "Simple DICOM Viewer";
    public static final String DICOM_STRUCTURED_REPORT_TAB_NAME = "DICOM Structure Report";
    public static final String NIFTI_VIEWER_TAB_NAME = "NIFTI Viewer";

    protected DatasetViewForm(Dataset o) {
        super(o);
    }

    @Override
    protected void appendToInterfaceForm(Form interfaceForm) {
        Dataset ds = object();

        /*
         * source
         */
        FieldGroup fg = new FieldGroup(new FieldDefinition("Source", "source", ConstantType.DEFAULT, null, null, 1, 1));
        Field<SourceType> sourceTypeField = new Field<SourceType>(
                new FieldDefinition("Type", "type", StringType.DEFAULT, "Source Type", null, 1, 1));
        sourceTypeField.setValue(ds.sourceType());
        fg.add(sourceTypeField);
        interfaceForm.add(fg);

        /*
         * vid
         */
        Field<String> vidField = new Field<String>(
                new FieldDefinition("VID", "vid", StringType.DEFAULT, null, null, 1, 1));
        vidField.setValue(ds.contentVid());
        interfaceForm.add(vidField);

        /*
         * (asset mime) type
         */
        Field<String> typeField = new Field<String>(
                new FieldDefinition("Type", "type", StringType.DEFAULT, "MIME type of the object.", null, 0, 1));
        typeField.setValue(ds.mimeType());
        interfaceForm.add(typeField);

        if (ds instanceof PrimaryDataset) {
            PrimaryDataset pds = (PrimaryDataset) ds;
            FieldGroup fgAcquisition = new FieldGroup(
                    new FieldDefinition("Acquisition", "acquisition", ConstantType.DEFAULT, null, null, 1, 1));
            FieldGroup fgSubject = new FieldGroup(
                    new FieldDefinition("Subject", "subject", ConstantType.DEFAULT, null, null, 1, 1));
            Field<String> subjectIdField = new Field<String>(
                    new FieldDefinition("ID", "id", StringType.DEFAULT, null, null, 1, 1));
            subjectIdField.setValue(pds.subjectCid());
            fgSubject.add(subjectIdField);
            Field<String> subjectStateField = new Field<String>(
                    new FieldDefinition("State", "state", StringType.DEFAULT, null, null, 1, 1));
            subjectStateField.setValue(pds.subjectState());
            fgSubject.add(subjectStateField);
            fgAcquisition.add(fgSubject);
            interfaceForm.add(fgAcquisition);
        }
        if (ds instanceof DerivedDataset) {
            DerivedDataset dds = (DerivedDataset) ds;
            FieldGroup fgDerivation = new FieldGroup(
                    new FieldDefinition("Derivation", "derivation", ConstantType.DEFAULT, null, null, 1, 1));
            Field<Boolean> processedField = new Field<Boolean>(
                    new FieldDefinition("Processed", "processed", BooleanType.DEFAULT_TRUE_FALSE, null, null, 0, 1));
            processedField.setValue(dds.processed());
            fgDerivation.add(processedField);

            Field<Boolean> anonymizedField = new Field<Boolean>(
                    new FieldDefinition("Anonymized", "anonymized", BooleanType.DEFAULT_TRUE_FALSE, null, null, 0, 1));
            anonymizedField.setValue(dds.anonymized());
            fgDerivation.add(anonymizedField);

            List<DerivedDataset.Input> inputs = dds.inputs();
            if (inputs != null && !inputs.isEmpty()) {
                for (DerivedDataset.Input input : inputs) {
                    FieldGroup fgInput = new FieldGroup(
                            new FieldDefinition("Input", "input", ConstantType.DEFAULT, null, null, 1, 1));
                    Field<String> inputVidField = new Field<String>(
                            new FieldDefinition("VID", "vid", ConstantType.DEFAULT, null, null, 1, 1));
                    inputVidField.setInitialValue(input.vid(), false);
                    Field<String> inputIdField = new Field<String>(
                            new FieldDefinition(null, ConstantType.DEFAULT, null, null, 1, 1));
                    inputIdField.setInitialValue(input.citeableId(), false);
                    fgInput.add(inputVidField);
                    fgInput.add(inputIdField);
                    fgDerivation.add(fgInput);
                }
            }

            // TODO transform
            interfaceForm.add(fgDerivation);
        }

        FieldGroup fgMethod = new FieldGroup(
                new FieldDefinition("Method", "method", ConstantType.DEFAULT, null, null, 1, 1));
        Field<String> methodIdField = new Field<String>(
                new FieldDefinition("ID", "id", StringType.DEFAULT, "Method id", null, 1, 1));
        methodIdField.setValue(ds.methodCid());
        fgMethod.add(methodIdField);
        Field<String> methodStepField = new Field<String>(
                new FieldDefinition("Step", "step", StringType.DEFAULT, "Method step", null, 1, 1));
        methodStepField.setValue(ds.methodStep());
        fgMethod.add(methodStepField);
        interfaceForm.add(fgMethod);

    }

    protected void updateOtherTabs() {
        Dataset ds = object();
        if (ds instanceof DicomDataset) {
            DicomDataset dds = (DicomDataset) ds;
            if (dds.isStructuredReport()) {
                updateDicomStructuredReportTab(dds);
            } else {
                updateSimpleDicomViewerTab(dds);
                updatePapayaDicomViewerTab(dds);
            }
        } else if (ds instanceof NiftiDataset) {
            NiftiDataset nds = (NiftiDataset) ds;
            updateNiftiViewerTab(nds);
        }

    }

    private void updateDicomStructuredReportTab(DicomDataset ds) {
        DicomStructuredReportViewer srViewer = new DicomStructuredReportViewer(ds);
        putTab(DICOM_STRUCTURED_REPORT_TAB_NAME, "DICOM Structured Report", srViewer);
    }

    private void updateSimpleDicomViewerTab(DicomDataset ds) {
        SimpleDicomViewer simpleDicomViewer = new SimpleDicomViewer(ds);
        putTab(SIMPLE_DICOM_VIEWER_TAB_NAME, "Simple DICOM Viewer", simpleDicomViewer);
    }

    private void updatePapayaDicomViewerTab(DicomDataset ds) {
        PapayaDicomViewer papayaDicomViewer = new PapayaDicomViewer(ds);
        putTab(PAPAYA_DICOM_VIEWER_TAB_NAME, "Papaya DICOM Viewer", papayaDicomViewer);

    }

    private void updateNiftiViewerTab(NiftiDataset ds) {
        NiftiViewer niftiViewer = new NiftiViewer(ds);
        putTab(NIFTI_VIEWER_TAB_NAME, "Papaya NIFTI Viewer", niftiViewer);
    }

    public static DatasetViewForm create(Dataset dataset) {
        return new DatasetViewForm(dataset);
    }

}

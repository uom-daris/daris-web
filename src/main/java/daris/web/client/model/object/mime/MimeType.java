package daris.web.client.model.object.mime;

public enum MimeType {

    DICOM_SERIES("dicom/series", null), NIFTI_SERIES("nifti/series", null), DICOM("application/dicom",
            "dcm"), NIFTI("image/x-nifti", "nii"), NIFTI_GZ("image/x-nifit-gz", "nii.gz");
    private String _type;
    private String _ext;

    MimeType(String type, String ext) {
        _type = type;
        _ext = ext;
    }

    @Override
    public String toString() {
        return _type;
    }

    public String type() {
        return _type;
    }

    public String extension() {
        return _ext;
    }

    public boolean equals(String type) {
        return type().equals(type);
    }
}

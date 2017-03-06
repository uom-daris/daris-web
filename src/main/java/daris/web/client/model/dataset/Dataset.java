package daris.web.client.model.dataset;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public abstract class Dataset extends DObject {

    private SourceType _sourceType;
    private String _contentVid;
    private String _mimeType;

    protected Dataset(XmlElement de) {
        super(de);
        _sourceType = SourceType.fromString(de.value("source/type"), SourceType.derivation);
        _contentVid = de.value("vid");
        _mimeType = de.value("type");
    }

    @Override
    public Type type() {
        return DObject.Type.DATASET;
    }

    public SourceType sourceType() {
        return _sourceType;
    }

    public String mimeType() {
        return _mimeType;
    }

    public String contentVid() {
        return _contentVid;
    }

    public abstract String methodCid();

    public abstract String methodStep();

    public static Dataset create(XmlElement oe) {
        String sourceType = oe.value("source/type");
        if ("derivation".equals(sourceType)) {
            String mimeType = oe.value("type");
            if (DicomDataset.ASSET_MIME_TYPE.equals(mimeType)) {
                return new DicomDataset(oe);
            } else if (NiftiDataset.ASSET_MIME_TYPE.equals(mimeType)) {
                return new NiftiDataset(oe);
            } else {
                return new DerivedDataset(oe);
            }
        } else if ("primary".equals(sourceType)) {
            return new PrimaryDataset(oe);
        } else {
            throw new AssertionError("Unexpected dataset source/type: " + sourceType);
        }
    }

}

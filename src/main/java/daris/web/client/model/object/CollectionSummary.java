package daris.web.client.model.object;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import arc.mf.client.xml.XmlElement;
import arc.mf.session.Session;

public class CollectionSummary {

    private long _nbObjects;
    private long _nbDatasets;
    private long _nbAttachments;
    private long _nbDicomDatasets;
    private long _totalSize;
    private long _totalContentSize;
    private long _totalAttachmentSize;
    private long _totalDatasetSize;
    private long _totalDicomDatasetSize;
    private Map<String, Collection<String>> _transcodes;

    CollectionSummary(XmlElement xe) {

        try {
            _nbObjects = xe.longValue("number-of-objects", 0);
            _nbAttachments = xe.longValue("number-of-attachments", 0);
            _nbDatasets = xe.longValue("number-of-datasets", 0);
            _nbDicomDatasets = xe.longValue("number-of-dicom-datasets", 0);
            _totalContentSize = xe.longValue("total-content-size", 0);
            _totalAttachmentSize = xe.longValue("total-attachment-size", 0);
            _totalDatasetSize = xe.longValue("total-dataset-size", 0);
            _totalDicomDatasetSize = xe.longValue("total-dicom-dataset-size", 0);
            _totalSize = xe.longValue("total-size", 0);
            List<XmlElement> tes = xe.elements("transcode");
            if (tes != null && !tes.isEmpty()) {
                _transcodes = new LinkedHashMap<String, Collection<String>>();
                for (XmlElement te : tes) {
                    String from = te.value("@from");
                    _transcodes.put(from, te.values("to"));
                }
            }
        } catch (Throwable e) {
            Session.displayError("Executing service: ", e);
        }
    }

    public long numberOfObjects() {
        return _nbObjects;
    }

    public long numberOfDatasets() {
        return _nbDatasets;
    }

    public long numberOfDicomDatasets() {
        return _nbDicomDatasets;
    }

    public long numberOfAttachments() {
        return _nbAttachments;
    }

    public long totalSize() {
        return _totalSize;
    }

    public long totalContentSize() {
        return _totalContentSize;
    }

    public long totalAttachmentSize() {
        return _totalAttachmentSize;
    }

    public long totalDatasetSize() {
        return _totalDatasetSize;
    }

    public long totalDicomDatasetSize() {
        return _totalDicomDatasetSize;
    }

    public Map<String, Collection<String>> transcodes() {
        return _transcodes;
    }

    public boolean hasTranscodes() {
        return _transcodes != null && !_transcodes.isEmpty();
    }

}

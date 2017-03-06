package daris.web.client.model.dataset;

import arc.mf.client.RemoteServer;
import arc.mf.client.xml.XmlElement;

public class DicomDataset extends DerivedDataset {

    public static final String ASSET_MIME_TYPE = "dicom/series";

    private int _size;
    private String _modality;

    public DicomDataset(XmlElement oe) {

        super(oe);
        try {
            _size = oe.intValue("meta/mf-dicom-series/size", 0);
        } catch (Throwable e) {
            _size = 0;
        }
        _modality = oe.value("meta/mf-dicom-series/modality");
    }

    public int size() {

        return _size;
    }

    public String modality() {
        return _modality;
    }

    public boolean isStructuredReport() {
        return "SR".equalsIgnoreCase(_modality);
    }

    public boolean isImage() {
        return !"SR".equalsIgnoreCase(_modality);
    }

    public String papayaViewerUrl() {
        if (hasContent() && RemoteServer.haveSession()) {
            StringBuilder sb = new StringBuilder();
            sb.append(com.google.gwt.user.client.Window.Location.getProtocol());
            sb.append("//");
            sb.append(com.google.gwt.user.client.Window.Location.getHost());
            sb.append("/daris/dicom.mfjp?_skey=");
            sb.append(RemoteServer.sessionId());
            sb.append("&module=view&id=");
            sb.append(assetId());
            return sb.toString();
        } else {
            return null;
        }
    }

    public String simpleViewerUrl() {
        if (hasContent() && RemoteServer.haveSession()) {
            StringBuilder sb = new StringBuilder();
            sb.append(com.google.gwt.user.client.Window.Location.getProtocol());
            sb.append("//");
            sb.append(com.google.gwt.user.client.Window.Location.getHost());
            sb.append("/daris/dicom.mfjp?_skey=");
            sb.append(RemoteServer.sessionId());
            sb.append("&module=simpleview&id=");
            sb.append(assetId());
            return sb.toString();
        } else {
            return null;
        }
    }

}

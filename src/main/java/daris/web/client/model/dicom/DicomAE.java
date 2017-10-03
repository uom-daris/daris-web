package daris.web.client.model.dicom;

import arc.mf.client.xml.XmlElement;
import arc.mf.session.Session;

public class DicomAE {

    public static enum Type {
        PUBLIC, PRIVATE;

        public String toString() {
            return name().toLowerCase();
        }

        public static Type fromString(String s) {
            if (s != null) {
                if (s.equalsIgnoreCase(PUBLIC.name())) {
                    return PUBLIC;
                } else if (s.equalsIgnoreCase(PRIVATE.name())) {
                    return PRIVATE;
                }
            }
            return null;
        }
    }

    private Type _type;
    private String _assetId;
    private String _name;
    private String _title;
    private String _description;
    private String _host;
    private int _port;
    private Boolean _ssl;

    public DicomAE(XmlElement ae) {
        _type = Type.fromString(ae.value("@type"));
        _assetId = ae.value("@id");
        _name = ae.value("@name");
        _title = ae.value("@title");
        _description = ae.value("@description");
        _host = ae.value("@host");
        try {
            _port = ae.intValue("@port", 104);
        } catch (Throwable e) {
            Session.displayError("Instantiating DICOM application entity object", e);
        }
        String ssl = ae.value("@ssl");
        if (ssl != null) {
            _ssl = Boolean.parseBoolean(ssl);
        }
    }

    public String name() {
        return _name;
    }

    public String title() {
        return _title;
    }

    public String host() {
        return _host;
    }

    public int port() {
        return _port;
    }

    public Boolean ssl() {
        return _ssl;
    }

    public String description() {
        return _description;
    }

    public Type type() {
        return _type;
    }

    public String assetId() {
        return _assetId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (_name != null) {
            sb.append(_name);
        } else {
            sb.append(_title);
        }
        sb.append(": ");
        sb.append(_title);
        sb.append("@");
        sb.append(_host);
        sb.append(":");
        sb.append(_port);
        return sb.toString();
    }
}

package daris.web.client.model.object;

public class Attachment {
    private String _assetId;
    private String _name;
    private String _description;
    private String _mimeType;
    private String _ext;
    private long _size;
    private String _sizeHR;

    public Attachment(String assetId, String name, String desc, String mimeType, String ext, long size, String sizeHR) {

        _assetId = assetId;
        _name = name;
        _description = desc;
        _mimeType = mimeType;
        _ext = ext;
        _size = size;
        _sizeHR = sizeHR;
    }

    public String assetId() {

        return _assetId;
    }

    public String name() {

        return _name;
    }

    public String description() {
        return _description;
    }

    public String extension() {

        return _ext;
    }

    public String mimeType() {

        return _mimeType;
    }

    public long size() {

        return _size;
    }

    public String humanReadableSize() {
        return _sizeHR;
    }

    public String toHTML() {

        String html = "<table><thead><tr><th align=\"center\" colspan=\"2\">Attachment</th></tr><thead>";
        html += "<tbody>";
        html += "<tr><td><b>asset id:</b></td><td>" + _assetId + "</td></tr>";
        html += "<tr><td><b>name:</b></td><td>" + _name + "</td></tr>";
        html += "<tr><td><b>extension:</b></td><td>" + _ext + "</td></tr>";
        html += "<tr><td><b>mime type:</b></td><td>" + _mimeType + "</td></tr>";
        html += "<tr><td><b>size:</b></td><td>" + humanReadableSize() + "</td></tr>";
        html += "</tbody></table>";
        return html;
    }
}

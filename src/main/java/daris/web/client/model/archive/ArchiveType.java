package daris.web.client.model.archive;

public enum ArchiveType {

    ZIP("application/zip", "zip"), AAR("application/arc-archive", "aar");

    private String _type;
    private String _ext;

    ArchiveType(String type, String ext) {
        _type = type;
        _ext = ext;
    }

    public String mimeType() {
        return _type;
    }

    public String fileExtension() {
        return _ext;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}

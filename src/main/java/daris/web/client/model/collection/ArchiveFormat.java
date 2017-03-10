package daris.web.client.model.collection;

import java.util.List;

import arc.mf.client.util.ListUtil;

public enum ArchiveFormat {

    AAR("aar", "Arcitecta archive"), ZIP("zip", "ZIP archive"), TGZ("tar.gz", "Gzipped TAR archive");

    private String _ext;
    private String _description;

    ArchiveFormat(String ext, String description) {
        _ext = ext;
        _description = description;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String fileExtension() {
        return _ext;
    }

    public String description() {
        return _description;
    }

    public static ArchiveFormat fromString(String name, ArchiveFormat defaultFormat) {
        ArchiveFormat[] vs = values();
        for (ArchiveFormat v : vs) {
            if (v.name().equals(name)) {
                return v;
            }
        }
        return defaultFormat;
    }

    public static final long GiB = 1073741824L;

    public static List<ArchiveFormat> availableFormatsForSize(long size) {
        if (size > GiB * 8) {
            return ListUtil.list(AAR, ZIP);
        } else {
            return ListUtil.list(AAR, ZIP, TGZ);
        }
    }

}

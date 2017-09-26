package daris.web.client.model.object.exports;

public class DownloadOptions extends ExportOptions {

    private ArchiveFormat _archiveFormat;
    private int _clevel;

    public DownloadOptions() {
        _archiveFormat = ArchiveFormat.ZIP;
        _clevel = 0;
    }

    public void setArchiveFormat(ArchiveFormat format) {
        _archiveFormat = format;
    }

    public ArchiveFormat archiveFormat() {
        return _archiveFormat;
    }

    public Integer compressionLevel() {
        return _clevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel <= 0) {
            _clevel = 0;
        } else if (compressionLevel >= 9) {
            _clevel = 9;
        } else {
            _clevel = compressionLevel;
        }
    }

    public boolean compress() {
        return _clevel > 0;
    }

    public void setCompress(boolean compress) {
        if (compress) {
            _clevel = 6;
        } else {
            _clevel = 0;
        }
    }
}

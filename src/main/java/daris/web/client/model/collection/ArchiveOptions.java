package daris.web.client.model.collection;

import java.util.Map;
import java.util.TreeMap;

public class ArchiveOptions {

    private Parts _parts;
    private boolean _includeAttachments;
    private boolean _decompress;
    private ArchiveFormat _archiveFormat;
    private Map<String, String> _transcodes;
    private Integer _clevel;

    public ArchiveOptions() {
        _parts = Parts.CONTENT;
        _includeAttachments = true;
        _decompress = true;
        _archiveFormat = ArchiveFormat.ZIP;
    }

    public void setParts(Parts parts) {
        _parts = parts;
    }

    public void setIncludeAttachments(boolean includeAttachments) {
        _includeAttachments = includeAttachments;
    }

    public void setDecompress(boolean decompress) {
        _decompress = decompress;
    }

    public void setArchiveFormat(ArchiveFormat format) {
        _archiveFormat = format;
    }

    public Parts parts() {
        return _parts;
    }

    public boolean includeAttachments() {
        return _includeAttachments;
    }

    public boolean decompress() {
        return _decompress;
    }

    public ArchiveFormat archiveFormat() {
        return _archiveFormat;
    }

    public Map<String, String> transcodes() {
        return _transcodes;
    }

    public boolean hasTranscodes() {
        return _transcodes != null && !_transcodes.isEmpty();
    }

    public void addTranscode(String from, String to) {
        if (_transcodes == null) {
            _transcodes = new TreeMap<String, String>();
        }
        _transcodes.put(from, to);
    }

    public void removeTranscode(String from) {
        if (_transcodes != null && _transcodes.containsKey(from)) {
            _transcodes.remove(from);
        }
    }

    public String transcodeFor(String from) {
        if (_transcodes != null) {
            return _transcodes.get(from);
        }
        return null;
    }

    public Integer compressionLevel() {
        return _clevel;
    }

    public void setCompressionLevel(Integer compressionLevel) {
        if (compressionLevel == null) {
            _clevel = null;
        } else {
            if (compressionLevel <= 0) {
                _clevel = 0;
            } else if (compressionLevel >= 9) {
                _clevel = 9;
            } else {
                _clevel = compressionLevel;
            }
        }
    }

    public boolean compress() {
        return _clevel != null && _clevel > 0;
    }

    public void setCompress(Boolean compress) {
        if (compress == null) {
            _clevel = null;
        } else if (compress) {
            _clevel = 6;
        } else {
            _clevel = 0;
        }
    }
}

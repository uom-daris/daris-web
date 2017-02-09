package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;

public class ContentInfo {

    // private String _atime;
    // private Long _atimeMillisec;
    // private String _ctime;
    // private Long _ctimeMillisec;
    // private String _ltype;
    // private String _type;
    // private String _ext;
    // private Long _size;
    // private String _sizeUnits;
    // private String _csum;
    // private int _csumBase;
    // private String _store;
    // private String _url;

    private XmlElement _de;
    private long _size;
    private String _sizeHR;
    private String _type;
    private String _ltype;
    private String _ext;
    private String _csum;

    public ContentInfo(XmlElement de) {

        _de = de;

        try {
            _size = de.longValue("size", 0);
        } catch (Throwable e) {
        }

        _sizeHR = de.value("size/@h");

        _type = de.value("type");

        _ext = de.value("type/@ext");

        _csum = de.value("csum[@base='16']");

    }

    public String ctime() {

        return _de.value("ctime");
    }

    public String type() {

        return _type;
    }

    public String ltype() {
        return _ltype;
    }

    public String ext() {

        return _ext;
    }

    public long size() {

        return _size;
    }

    public String humanReadableSize() {
        return _sizeHR;
    }

    public String csum() {

        return _csum;
    }

    public String store() {

        return _de.value("store");
    }

    public String url() {

        return _de.value("url");
    }

    public boolean isSupportedArchive() {
        return daris.web.client.model.archive.ArchiveRegistry.isSupportedArchive(type());
    }

    public XmlElement xmlElement() {
        return _de;
    }
}

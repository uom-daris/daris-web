package daris.web.client.model.object;

import arc.mf.client.xml.XmlElement;

public abstract class DObjectBuilder {

    private String _name;
    private String _description;
    private String _type;
    private boolean _allowIncompleteMeta;
    private boolean _allowInvalidMeta;
    private XmlElement _metadata;

    public void setName(String name) {
        _name = name;
    }

    public String name() {
        return _name;
    }

    public void setType(String type) {
        _type = type;
    }

    public String type() {
        return _type;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String description() {
        return _description;
    }

    public void setAllowIncompleteMeta(boolean allowIncompleteMeta) {
        _allowIncompleteMeta = allowIncompleteMeta;
    }

    public boolean allowIncompleteMeta() {
        return _allowIncompleteMeta;
    }

    public void setAllowInvalidMeta(boolean allowInvalidMeta) {
        _allowInvalidMeta = allowInvalidMeta;
    }

    public boolean allowInvalidMeta() {
        return _allowInvalidMeta;
    }

    public XmlElement metadata() {
        return _metadata;
    }

    public void setMetadata(XmlElement metadata) {
        _metadata = metadata;
    }

}

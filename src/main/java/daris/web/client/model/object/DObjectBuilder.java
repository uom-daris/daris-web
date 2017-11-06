package daris.web.client.model.object;

public abstract class DObjectBuilder {

    private String _name;
    private String _description;
    private boolean _allowIncompleteMeta;
    private boolean _allowInvalidMeta;

    private MetadataSetter _metadataSetter;

    public void setName(String name) {
        _name = name;
    }

    public String name() {
        return _name;
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

    public void setMetadataSetter(MetadataSetter metadataSetter) {
        _metadataSetter = metadataSetter;
    }
    
    public MetadataSetter metadataSetter(){
        return _metadataSetter;
    }
}

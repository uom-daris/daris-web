package daris.web.client.model.user.messages;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;

public class ActorSelfHave extends ObjectMessage<Boolean> {

    private String _role;
    private String _roleType;
    private String _resource;
    private String _resourceType;
    private String _resourceAccess;

    public ActorSelfHave(String role, String roleType) {
        _role = role;
        _roleType = roleType;
    }

    public ActorSelfHave(String resource, String resourceType, String resourceAccess) {
        _resource = resource;
        _resourceType = resourceType;
        _resourceAccess = resourceAccess;
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {
        if (_role != null) {
            w.add("role", new String[] { "type", _roleType }, _role);
        } else {
            w.push("perm");
            w.add("resource", new String[] { "type", _resourceType }, _resource);
            w.add("access", _resourceAccess);
            w.pop();
        }
    }

    @Override
    protected String messageServiceName() {
        return "actor.self.have";
    }

    @Override
    protected Boolean instantiate(XmlElement xe) throws Throwable {
        if (_role != null) {
            return xe.booleanValue("role");
        } else {
            return xe.booleanValue("perm");
        }
    }

    @Override
    protected String objectTypeName() {
        return "actor";
    }

    @Override
    protected String idToString() {
        return null;
    }
}

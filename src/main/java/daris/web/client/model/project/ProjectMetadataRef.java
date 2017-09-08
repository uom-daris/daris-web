package daris.web.client.model.project;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class ProjectMetadataRef extends ObjectRef<XmlElement> {

    public ProjectMetadataRef() {

    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {

    }

    @Override
    protected String resolveServiceName() {
        return "om.pssd.project.metadata.describe";
    }

    @Override
    protected XmlElement instantiate(XmlElement xe) throws Throwable {
        return xe == null ? null : xe.element("meta");
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return null;
    }

}

package daris.web.client.model.project;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class Project extends DObject {

    public Project(XmlElement oe) {
        super(oe);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Type type() {
        return DObject.Type.PROJECT;
    }

}

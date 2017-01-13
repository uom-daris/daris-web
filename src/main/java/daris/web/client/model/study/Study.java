package daris.web.client.model.study;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class Study extends DObject {

    public Study(XmlElement oe) {
        super(oe);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Type type() {
        return DObject.Type.STUDY;
    }

}

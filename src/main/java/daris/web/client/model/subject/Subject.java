package daris.web.client.model.subject;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class Subject extends DObject{

    public Subject(XmlElement oe) {
        super(oe);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Type type() {
        return DObject.Type.SUBJECT;
    }

}

package daris.web.client.model.exmethod;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class ExMethod extends DObject {

    public ExMethod(XmlElement oe) {
        super(oe);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Type type() {
        return DObject.Type.EX_METHOD;
    }

}

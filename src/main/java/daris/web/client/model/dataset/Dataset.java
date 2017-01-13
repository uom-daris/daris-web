package daris.web.client.model.dataset;

import arc.mf.client.xml.XmlElement;
import daris.web.client.model.object.DObject;

public class Dataset extends DObject{

    public Dataset(XmlElement oe) {
        super(oe);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Type type() {
        return DObject.Type.DATASET;
    }

}

package daris.web.client.model.study;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectUpdater;

public class StudyUpdater extends DObjectUpdater<Study> {

    public StudyUpdater(Study obj) {
        super(obj);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String serviceName() {
        return "om.pssd.study.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        // TODO Auto-generated method stub
        
    }

}

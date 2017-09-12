package daris.web.client.model.exmethod;

import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.DObjectUpdater;

public class ExMethodUpdater extends DObjectUpdater<ExMethod> {

    public ExMethodUpdater(ExMethod obj) {
        super(obj);
    }

    @Override
    public String serviceName() {
        return "om.pssd.object.update";
    }

    @Override
    public void serviceArgs(XmlWriter w) {
        w.add("id", object().citeableId());
        if (name() != null) {
            w.add("name", name());
        }
        if (description() != null) {
            w.add("description", description());
        }
    }

}

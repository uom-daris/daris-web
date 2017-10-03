package daris.web.client.model.dicom.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;

public class DicomSendCallingAETitleList extends ObjectMessage<List<String>> {

    @Override
    protected void messageServiceArgs(XmlWriter w) {

    }

    @Override
    protected String messageServiceName() {
        return "daris.dicom.send.calling-ae-title.list";
    }

    @Override
    protected List<String> instantiate(XmlElement xe) throws Throwable {
        Collection<String> titles = xe.values("calling-ae-title");
        if (titles != null && !titles.isEmpty()) {
            return new ArrayList<String>(titles);
        }
        return null;
    }

    @Override
    protected String objectTypeName() {
        return null;
    }

    @Override
    protected String idToString() {
        return null;
    }

}

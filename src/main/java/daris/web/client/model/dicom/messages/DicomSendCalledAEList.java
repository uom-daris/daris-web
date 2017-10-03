package daris.web.client.model.dicom.messages;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.dicom.DicomAE;

public class DicomSendCalledAEList extends ObjectMessage<List<DicomAE>> {

    @Override
    protected void messageServiceArgs(XmlWriter w) {

    }

    @Override
    protected String messageServiceName() {
        return "daris.dicom.send.called-ae.list";
    }

    @Override
    protected List<DicomAE> instantiate(XmlElement xe) throws Throwable {
        List<XmlElement> aes = xe.elements("ae");
        if (aes != null&&!aes.isEmpty()) {
            List<DicomAE> daes = new ArrayList<DicomAE>(aes.size());
            for(XmlElement ae : aes){
                daes.add(new DicomAE(ae));
            }
            return daes;
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

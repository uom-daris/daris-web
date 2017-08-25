package daris.web.client.model.dicom.messages;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.upload.FileSetUploadTask;

public class DicomIngestTask extends FileSetUploadTask<List<String>> {

    @Override
    protected String consumeServiceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        // TODO Auto-generated method stub

    }

    @Override
    protected List<String> instantiate(XmlElement xe) {
        // TODO Auto-generated method stub
        return null;
    }

}

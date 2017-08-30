package daris.web.client.model.dicom.messages;

import java.util.Collection;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.object.upload.FileEntry;
import daris.web.client.model.object.upload.FileUploadTask;

public class DicomIngestTask extends FileUploadTask<List<String>> {

    protected DicomIngestTask(Collection<FileEntry> files) {
        super(files);
        // TODO Auto-generated constructor stub
    }

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

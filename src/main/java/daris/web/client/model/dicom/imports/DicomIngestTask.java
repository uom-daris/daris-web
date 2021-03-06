package daris.web.client.model.dicom.imports;

import java.util.Collection;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import daris.web.client.model.archive.ArchiveType;
import daris.web.client.model.object.imports.FileEntry;
import daris.web.client.model.object.imports.FileUploadTask;

public class DicomIngestTask extends FileUploadTask<Void> {

    private DicomIngestSettings _settings;

    public DicomIngestTask(DicomIngestSettings settings, Collection<FileEntry> files) {
        super(files);
        setArchiveType(ArchiveType.AAR);
        _settings = settings;
        setName("dicom ingest");
    }

    @Override
    protected String consumeServiceName() {
        return "dicom.ingest";
    }

    @Override
    protected void consumeServiceArgs(XmlWriter w) {
        _settings.setArchiveType(ArchiveType.AAR);
        _settings.setWait(false);
        _settings.saveServiceArgs(w);
    }

    @Override
    protected Void instantiate(XmlElement xe) {
        return null;
    }

}

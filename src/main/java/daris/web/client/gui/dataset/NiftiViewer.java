package daris.web.client.gui.dataset;

import daris.web.client.model.archive.ArchiveEntry;
import daris.web.client.model.dataset.NiftiDataset;

public class NiftiViewer extends IFrameViewer {

    public NiftiViewer(NiftiDataset ds) {
        super(ds.niftiViewerUrl());
    }

    public NiftiViewer(NiftiDataset ds, ArchiveEntry entry) {
        super(ds.niftiViewerUrl(entry));
    }
}

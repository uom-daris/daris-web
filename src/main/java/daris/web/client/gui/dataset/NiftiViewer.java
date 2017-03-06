package daris.web.client.gui.dataset;

import daris.web.client.model.dataset.NiftiDataset;

public class NiftiViewer extends IFrameViewer {

    public NiftiViewer(NiftiDataset ds) {
        super(ds.niftiViewerUrl());
    }

}

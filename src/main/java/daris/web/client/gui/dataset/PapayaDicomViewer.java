package daris.web.client.gui.dataset;

import daris.web.client.model.dataset.DicomDataset;

public class PapayaDicomViewer extends IFrameViewer {

    public PapayaDicomViewer(DicomDataset ds) {
        super(ds.papayaViewerUrl());
    }

}
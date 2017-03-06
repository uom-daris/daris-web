package daris.web.client.gui.dataset;

import daris.web.client.model.dataset.DicomDataset;

public class SimpleDicomViewer extends IFrameViewer {

    public SimpleDicomViewer(DicomDataset ds) {
        super(ds.simpleViewerUrl());
    }

}
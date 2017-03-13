package daris.web.client.model.object.menu;

import daris.web.client.model.dataset.DicomDataset;
import daris.web.client.model.object.DObjectRef;

public class MenuPreConditions {

    private Boolean _contentExists;
    private Integer _nbDatasets;
    private Integer _nbDicomDatasets;

    MenuPreConditions(Boolean contentExists, Integer nbDatasets, Integer nbDicomDatasets) {
        _contentExists = contentExists;
        _nbDatasets = nbDatasets;
        _nbDicomDatasets = nbDicomDatasets;
    }

    public Boolean contentExists() {
        return _contentExists;
    }

    public Integer numberOfDatasets() {
        return _nbDatasets;
    }

    public Integer numberOfDicomDatasets() {
        return _nbDicomDatasets;
    }

    static MenuPreConditions menuPreConditionsFor(DObjectRef o) {
        Boolean contentExists = null;
        Integer nbDatasets = null;
        Integer nbDicomDatasets = null;

        if (o.isDataset() || (o.resolved() && o.referent().hasContent())) {
            contentExists = true;
        }
        if (o.isDataset()) {
            nbDatasets = 1;
        } else if (o.isStudy() && o.numberOfChildren() >= 0) {
            nbDatasets = o.numberOfChildren();
        }
        if (o.referent() != null && (o.referent() instanceof DicomDataset)) {
            nbDicomDatasets = 1;
        }
        return new MenuPreConditions(contentExists, nbDatasets, nbDicomDatasets);
    }

}

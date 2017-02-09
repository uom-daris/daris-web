package daris.web.client.model.dataset;

import arc.mf.client.xml.XmlElement;

public class PrimaryDataset extends Dataset {

    private String _subjectCid;
    private String _subjectState;
    private String _methodCid;
    private String _methodStep;

    public PrimaryDataset(XmlElement oe) {
        super(oe);
        _subjectCid = oe.value("acquisition/subject/id");
        _subjectState = oe.value("acquisition/subject/state");
        _methodCid = oe.value("acquisition/method/id");
        _methodStep = oe.value("acquisition/method/step");
    }

    @Override
    public String methodCid() {
        return _methodCid;
    }

    @Override
    public String methodStep() {
        return _methodStep;
    }

    public String subjectCid() {
        return _subjectCid;
    }

    public String subjectState() {
        return _subjectState;
    }

}

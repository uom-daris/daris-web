package daris.web.client.model.dataset;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.session.Session;

public class DerivedDataset extends Dataset {

    public static class Input {

        private String _cid;
        private String _vid;

        public Input(String cid, String vid) {

            _cid = cid;
            _vid = vid;
        }

        public String citeableId() {

            return _cid;
        }

        public String vid() {

            return _vid;
        }

    }

    private List<Input> _inputs;

    private String _methodCid;

    private String _methodStep;

    private Boolean _processed;

    private Boolean _anonymized;

    public DerivedDataset(XmlElement oe) {
        super(oe);

        List<XmlElement> ies = oe.elements("derivation/input");
        if (ies != null && !ies.isEmpty()) {
            _inputs = new ArrayList<Input>(ies.size());
            for (XmlElement ie : ies) {
                Input input = new Input(ie.value(), ie.value("@vid"));
                _inputs.add(input);
            }
        }
        try {
            if (oe.value("derivation/processed") != null) {
                _processed = oe.booleanValue("derivation/processed");
            }
            if (oe.value("derivation/anonymized") != null) {
                _anonymized = oe.booleanValue("derivation/anonymized");
            }
        } catch (Throwable e) {
            Session.displayError("Instantiating derived dataset...", e);
        }
        _methodCid = oe.value("derivation/method/id");
        _methodStep = oe.value("derivation/method/step");
    }

    @Override
    public String methodCid() {
        return _methodCid;
    }

    @Override
    public String methodStep() {
        return _methodStep;
    }

    public Boolean processed() {
        return _processed;
    }

    public Boolean anonymized() {
        return _anonymized;
    }

    public List<Input> inputs() {
        return _inputs;
    }
}

package daris.web.client.model.dicom.exports;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;
import daris.web.client.model.dicom.DicomAE;
import daris.web.client.model.dicom.messages.DicomSendCalledAEList;

public class DicomSendCalledAEEnum implements DynamicEnumerationDataSource<DicomAE> {

    @Override
    public boolean supportPrefix() {
        return false;
    }

    @Override
    public void exists(String value, DynamicEnumerationExistsHandler handler) {
        // VicNode: VicNode@localhost:6667
        if (value == null || value.trim().isEmpty()) {
            handler.exists(value, false);
            return;
        }
        int idx = value.indexOf(':');
        final String v = idx == -1 ? value : value.substring(0, idx);
        new DicomSendCalledAEList().send(aes -> {
            if (aes != null) {
                for (DicomAE ae : aes) {
                    if (ae.name().equals(v)) {
                        handler.exists(value, true);
                        return;
                    }
                }
            }
            handler.exists(value, false);
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<DicomAE> handler) {
        new DicomSendCalledAEList().send(aes -> {
            if (aes != null) {
                List<Value<DicomAE>> vs = new ArrayList<Value<DicomAE>>(aes.size());
                for (DicomAE ae : aes) {
                    Value<DicomAE> v = new Value<DicomAE>(ae.toString(), ae.description(), ae);
                    vs.add(v);
                }
                if (!vs.isEmpty()) {
                    handler.process(0, vs.size(), vs.size(), vs);
                    return;
                }
            }
            handler.process(0, 0, 0, null);
        });
    }

}

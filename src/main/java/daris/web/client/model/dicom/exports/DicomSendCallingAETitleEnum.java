package daris.web.client.model.dicom.exports;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;
import daris.web.client.model.dicom.messages.DicomSendCallingAETitleList;

public class DicomSendCallingAETitleEnum implements DynamicEnumerationDataSource<String> {

    @Override
    public boolean supportPrefix() {
        return true;
    }

    @Override
    public void exists(String value, DynamicEnumerationExistsHandler handler) {
        if (value == null || value.trim().isEmpty()) {
            handler.exists(value, false);
            return;
        }
        new DicomSendCallingAETitleList().send(titles -> {
            handler.exists(value, titles != null && titles.contains(value));
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<String> handler) {
        new DicomSendCallingAETitleList().send(titles -> {
            if (titles != null) {
                List<Value<String>> vs = new ArrayList<Value<String>>();
                for (String title : titles) {
                    if (prefix == null || title.startsWith(prefix)) {
                        Value<String> v = new Value<String>(title);
                        vs.add(v);
                    }
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

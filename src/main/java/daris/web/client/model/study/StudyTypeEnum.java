package daris.web.client.model.study;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.model.study.messages.StudyTypeList;

public class StudyTypeEnum implements DynamicEnumerationDataSource<String> {

    private String _exmCid;

    public StudyTypeEnum() {

        this(null);
    }

    public StudyTypeEnum(String exmCid) {

        _exmCid = exmCid;
    }

    @Override
    public boolean supportPrefix() {

        return false;
    }

    @Override
    public void exists(final String value, final DynamicEnumerationExistsHandler handler) {
        if (value == null || value.isEmpty()) {
            handler.exists(value, false);
            return;
        }
        new StudyTypeList(_exmCid).send(new ObjectMessageResponse<List<String>>() {
            @Override
            public void responded(List<String> types) {
                if (types == null || types.isEmpty()) {
                    handler.exists(value, false);
                    return;
                }
                handler.exists(value, types.contains(value));
            }
        });
    }

    @Override
    public void retrieve(String prefix, final long start, final long end,
            final DynamicEnumerationDataHandler<String> handler) {

        new StudyTypeList(_exmCid).send(new ObjectMessageResponse<List<String>>() {
            @Override
            public void responded(List<String> types) {
                if (types == null || types.isEmpty()) {
                    handler.process(0, 0, 0, null);
                    return;
                }
                List<Value<String>> vs = new ArrayList<Value<String>>();
                for (String type : types) {
                    vs.add(new Value<String>(type));
                }
                List<Value<String>> rvs = vs;
                int start1 = (int) start;
                int end1 = (int) end;
                long total = vs.size();
                if (start1 > 0 || end1 < vs.size()) {
                    if (start1 >= vs.size()) {
                        rvs = null;
                    } else {
                        if (end1 > vs.size()) {
                            end1 = vs.size();
                        }
                        rvs = vs.subList(start1, end1);
                    }
                }
                handler.process(start1, end1, total, rvs);
            }
        });
    }
}

package daris.web.client.model.sink;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;

public class SinkEnum implements DynamicEnumerationDataSource<Sink> {

    @Override
    public boolean supportPrefix() {
        return false;
    }

    @Override
    public void exists(final String value, DynamicEnumerationExistsHandler handler) {
        if (value == null) {
            handler.exists(value, false);
            return;
        }
        final String sinkName = value.trim();
        SinkSetRef.DARIS_SINKS.resolve(sinks -> {
            boolean exists = false;
            if (sinks != null) {
                for (Sink sink : sinks) {
                    if (sinkName.equals(sink.name())) {
                        exists = true;
                        break;
                    }
                }
            }
            handler.exists(value, exists);
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<Sink> handler) {

        SinkSetRef.DARIS_SINKS.resolve(sinks -> {
            List<Value<Sink>> values = new ArrayList<Value<Sink>>();
            if (sinks != null) {
                for (Sink sink : sinks) {
                    values.add(new Value<Sink>(sink));
                }
            }
            if (values.isEmpty()) {
                handler.process(0, 0, 0, null);
            } else {
                handler.process(0, values.size(), values.size(), values);
            }
        });
    }

}

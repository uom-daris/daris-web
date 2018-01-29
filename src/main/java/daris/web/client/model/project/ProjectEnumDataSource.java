package daris.web.client.model.project;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.object.DObjectRef;

public class ProjectEnumDataSource implements DynamicEnumerationDataSource<DObjectRef> {

    private ProjectCollectionRef _pc;

    public ProjectEnumDataSource() {
        _pc = new ProjectCollectionRef();
    }

    @Override
    public boolean supportPrefix() {
        return false;
    }

    @Override
    public void exists(String value, DynamicEnumerationExistsHandler handler) {
        if (value == null) {
            handler.exists(value, false);
            return;
        }
        String cid = value.split(":")[0].trim();
        XmlStringWriter w = new XmlStringWriter();
        w.add("cid", cid);
        Session.execute("asset.exists", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                boolean exists = xe.booleanValue("exists");
                handler.exists(value, exists);
            }
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<DObjectRef> handler) {
        _pc.resolve(start, end, projects -> {
            if (projects != null && !projects.isEmpty()) {
                List<EnumerationType.Value<DObjectRef>> values = new ArrayList<EnumerationType.Value<DObjectRef>>();
                for (DObjectRef project : projects) {
                    EnumerationType.Value<DObjectRef> value = new EnumerationType.Value<DObjectRef>(
                            project.citeableId() + ": " + project.name(), project.name(), project);
                    values.add(value);
                }
                handler.process(start, end, _pc.totalNumberOfMembers(), values);
                return;
            }
            handler.process(0, 0, 0, null);
        });
    }

}

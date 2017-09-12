package daris.web.client.model.exmethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectRef;

public class ExMethodStudyStepEnum implements DynamicEnumerationDataSource<ExMethodStudyStepRef> {

    private String _exmCid;
    private String _studyType;

    public ExMethodStudyStepEnum(String exmCid, String studyType) {
        _exmCid = exmCid;
        _studyType = studyType;
    }

    public ExMethodStudyStepEnum(DObjectRef parentObject, String studyType) {
        this(parentObject.citeableId(), studyType);
    }

    public void setStudyType(String studyType) {
        _studyType = studyType;
    }

    @Override
    public boolean supportPrefix() {
        return false;
    }

    @Override
    public void exists(String value, DynamicEnumerationExistsHandler handler) {
        if (value == null || value.isEmpty()) {
            handler.exists(value, false);
            return;
        }
        int idx = value.indexOf(':');
        String stepPath = idx != -1 ? value.substring(0, idx) : value;
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", _exmCid);
        if (_studyType != null) {
            w.add("type", _studyType);
        }
        Session.execute("om.pssd.ex-method.study.step.find", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                Collection<String> steps = xe.values("ex-method/step");
                handler.exists(value, steps != null && steps.contains(stepPath));
            }
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end,
            DynamicEnumerationDataHandler<ExMethodStudyStepRef> handler) {
        resolve(steps -> {
            if (steps == null) {
                handler.process(0, 0, 0, null);
                return;
            }
            List<Value<ExMethodStudyStepRef>> values = new ArrayList<Value<ExMethodStudyStepRef>>(steps.size());
            for (ExMethodStudyStepRef step : steps) {
                Value<ExMethodStudyStepRef> value = new Value<ExMethodStudyStepRef>(step.toString(), step.toString(),
                        step);
                values.add(value);
            }
            handler.process(0, values.size(), values.size(), values);
        });
    }

    public void resolve(ObjectResolveHandler<List<ExMethodStudyStepRef>> rh) {
        XmlStringWriter w = new XmlStringWriter();
        w.push("service", new String[] { "name", "om.pssd.ex-method.step.list" });
        w.add("id", _exmCid);
        w.pop();
        w.push("service", new String[] { "name", "om.pssd.ex-method.study.step.find" });
        w.add("id", _exmCid);
        if (_studyType != null) {
            w.add("type", _studyType);
        }
        w.pop();
        Session.execute("service.execute", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                XmlElement re1 = xe.element("reply[@service='om.pssd.ex-method.step.list']/response");
                XmlElement re2 = xe.element("reply[@service='om.pssd.ex-method.study.step.find']/response");
                List<XmlElement> ses = re2.elements("ex-method/step");
                if (ses == null || ses.isEmpty()) {
                    rh.resolved(null);
                    return;
                }
                List<ExMethodStudyStepRef> steps = new ArrayList<ExMethodStudyStepRef>(ses.size());
                for (XmlElement se : ses) {
                    String stepPath = se.value();
                    String studyType = se.value("@type");
                    String dicomModality = se.value("@dicom-modality");
                    int depth = CiteableIdUtils.depth(stepPath);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < depth; i++) {
                        if (i > 0) {
                            sb.append("/");
                        }
                        sb.append("step");
                    }
                    sb.append("[@path='").append(stepPath).append("']/@name");
                    String stepName = re1.value(sb.toString());
                    ExMethodStudyStepRef step = new ExMethodStudyStepRef(_exmCid, stepPath, stepName, studyType,
                            dicomModality);
                    steps.add(step);
                }
                if (!steps.isEmpty()) {
                    rh.resolved(steps);
                }
            }
        });

    }

}

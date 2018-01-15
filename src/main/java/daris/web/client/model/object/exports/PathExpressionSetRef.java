package daris.web.client.model.object.exports;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;
import arc.mf.object.ObjectResolveHandler;

public class PathExpressionSetRef extends ObjectRef<List<PathExpression>> {

    private String _projectCID;

    public PathExpressionSetRef(String projectCID) {
        _projectCID = projectCID;
    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        if (_projectCID != null) {
            w.add("project", _projectCID);
        }
    }

    @Override
    protected String resolveServiceName() {
        return "daris.path.expression.list";
    }

    @Override
    protected List<PathExpression> instantiate(XmlElement xe) throws Throwable {
        List<XmlElement> ees = xe.elements("expression");
        if (ees != null) {
            List<PathExpression> pes = new ArrayList<PathExpression>(ees.size());
            for (XmlElement ee : ees) {
                pes.add(new PathExpression(ee));
            }
            if (!pes.isEmpty()) {
                return pes;
            }
        }
        return null;
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return _projectCID;
    }

    public void resolvePathExpression(String expression, ObjectResolveHandler<PathExpression> rh) {
        resolve(pes -> {
            if (pes != null) {
                for (PathExpression pe : pes) {
                    if (pe.expression.equals(expression)) {
                        rh.resolved(pe);
                        return;
                    }
                }
            }
            rh.resolved(null);
        });
    }

}

package daris.web.client.model.object.exports;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType.Value;

public class PathExpressionEnum implements DynamicEnumerationDataSource<PathExpression> {

    private PathExpressionSetRef _pes;

    public PathExpressionEnum(String projectCID) {
        this(new PathExpressionSetRef(projectCID));
    }

    public PathExpressionEnum(PathExpressionSetRef pes) {
        _pes = pes == null ? new PathExpressionSetRef(null) : pes;
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
        _pes.resolve(pes -> {
            if (pes != null) {
                for (PathExpression pe : pes) {
                    if (value.equals(pe.name)) {
                        handler.exists(value, true);
                        return;
                    }
                }
            }
            handler.exists(value, false);
        });

    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<PathExpression> handler) {
        _pes.resolve(pes -> {
            if (pes != null && !pes.isEmpty()) {
                List<Value<PathExpression>> values = new ArrayList<Value<PathExpression>>(pes.size());
                for (PathExpression pe : pes) {
                    values.add(new Value<PathExpression>(pe));
                }
                if (!values.isEmpty()) {
                    handler.process(0, values.size(), values.size(), values);
                    return;
                }
            }
            handler.process(0, 0, 0, null);
        });

    }

}

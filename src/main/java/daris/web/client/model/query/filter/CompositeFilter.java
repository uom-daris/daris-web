package daris.web.client.model.query.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompositeFilter implements Filter {

    private LogicalOperator _logicalOp;

    private List<Filter> _filters;

    public CompositeFilter(LogicalOperator op, Filter... filters) {
        _logicalOp = op;
        _filters = new ArrayList<Filter>();
        if (filters != null) {
            for (Filter filter : filters) {
                _filters.add(filter);
            }
        }
    }

    public CompositeFilter(LogicalOperator op, Collection<Filter> filters) {
        _logicalOp = op;
        _filters = new ArrayList<Filter>();
        if (filters != null) {
            for (Filter filter : filters) {
                _filters.add(filter);
            }
        }
    }

    public LogicalOperator logicalOperator() {
        return _logicalOp;
    }

    public List<Filter> filters() {
        return Collections.unmodifiableList(_filters);
    }

    public void addFilter(Filter filter) {
        _filters.add(filter);
    }

    @Override
    public String asQueryString() {
        StringBuilder sb = new StringBuilder();
        saveToQuery(sb);
        return sb.toString();
    }

    @Override
    public void saveToQuery(StringBuilder sb) {
        int n = _filters.size();
        if (n > 1) {
            sb.append("(");
        }
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(" ").append(_logicalOp.value()).append(" ");
            }
            _filters.get(i).saveToQuery(sb);
        }
        if (n > 1) {
            sb.append(")");
        }
    }

}

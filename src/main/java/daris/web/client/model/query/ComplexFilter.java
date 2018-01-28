package daris.web.client.model.query;

import java.util.ArrayList;
import java.util.List;

import arc.mf.model.asset.query.AssetQueryClause.Operand;

public class ComplexFilter extends AbstractFilter {

    private List<Filter> _filters;
    private Operand _operand;

    public ComplexFilter(Operand operand) {
        _operand = operand;
        _filters = new ArrayList<Filter>();
    }

    public ComplexFilter() {
        this(Operand.AND);
    }

    public void addFilter(Filter filter) {
        _filters.add(filter);
    }

    public void removeFilter(Filter filter) {
        _filters.remove(filter);
    }

    @Override
    public void save(StringBuilder qb) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (Filter filter : _filters) {
            String qs = filter.toQueryString();
            if (qs != null && !qs.isEmpty()) {
                if (n > 0) {
                    if (_operand == Operand.AND) {
                        sb.append(" and ");
                    } else {
                        sb.append(") or (");
                    }
                }
                sb.append(qs);
                n++;
            }
        }
        if (n > 1 && _operand == Operand.OR) {
            qb.append("(").append(sb.toString()).append(")");
        } else {
            qb.append(sb.toString());
        }
    }

}

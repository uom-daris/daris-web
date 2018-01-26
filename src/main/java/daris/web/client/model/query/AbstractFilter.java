package daris.web.client.model.query;

import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;

public abstract class AbstractFilter implements Filter {

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        save(sb);
        return sb.toString();
    }

    @Override
    public Validity valid() {
        return IsValid.INSTANCE;
    }

}
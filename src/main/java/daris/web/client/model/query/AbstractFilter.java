package daris.web.client.model.query;

public abstract class AbstractFilter implements Filter {

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        save(sb);
        return sb.toString();
    }

}
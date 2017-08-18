package daris.web.client.model.object.filter;

public abstract class AbstractFilter implements Filter {

    @Override
    public final String toAQLString() {
        StringBuilder sb = new StringBuilder();
        saveAQL(sb);
        return sb.toString();
    }

    @Override
    public final String toString() {
        return toAQLString();
    }

    protected abstract void saveAQL(StringBuilder sb);

}

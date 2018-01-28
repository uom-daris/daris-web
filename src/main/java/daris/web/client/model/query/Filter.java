package daris.web.client.model.query;

public interface Filter {

    void save(StringBuilder sb);

    String toQueryString();

}

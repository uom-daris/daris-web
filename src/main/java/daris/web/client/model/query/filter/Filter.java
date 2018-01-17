package daris.web.client.model.query.filter;

public interface Filter {

    String asQueryString();

    void saveToQuery(StringBuilder sb);

}

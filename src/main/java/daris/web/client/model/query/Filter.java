package daris.web.client.model.query;

import arc.mf.client.util.Validity;

public interface Filter {

    void save(StringBuilder sb);

    String toQueryString();
    
    Validity valid();

}

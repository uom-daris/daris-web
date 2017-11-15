package daris.web.client.model.object.filter;

import arc.mf.client.util.Validity;

public interface Filter {

    String toAQLString();

    Validity valid();

    Filter duplicate();
}

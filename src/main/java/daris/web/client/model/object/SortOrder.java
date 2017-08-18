package daris.web.client.model.object;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.EnumerationType;

public enum SortOrder {

    DESC, ASC;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static EnumerationType<SortOrder> toEnumerationType() {
        List<EnumerationType.Value<SortOrder>> evs = new ArrayList<EnumerationType.Value<SortOrder>>(2);
        evs.add(new EnumerationType.Value<SortOrder>("descend", "descend", DESC));
        evs.add(new EnumerationType.Value<SortOrder>("ascend", "ascend", ASC));
        return new EnumerationType<SortOrder>(evs);
    }

}

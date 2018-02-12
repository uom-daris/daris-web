package daris.web.client.gui.explorer;

import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.query.sort.SortKey;

public interface ContextView {

    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final SortKey DEFAULT_SORT_KEY = SortKey.citeableId();

    public static interface Listener {

        void opened(DObjectRef o);

        void selected(DObjectRef o);

        void deselected(DObjectRef o);

        void updated(DObjectRef o);

    }

    void open(DObjectRef o);

    void seekTo(DObjectRef o, boolean refresh);

    void addListener(Listener l);

    void removeListener(Listener l);

}

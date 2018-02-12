package daris.web.client.model.query.sort;

import arc.mf.client.util.ObjectUtil;

public class SortKey {

    public static enum Order {
        ASCENDING("asc"), DESCENDING("desc");
        private String _value;

        Order(String value) {
            _value = value;
        }

        public String value() {
            return _value;
        }

        @Override
        public String toString() {
            return _value;
        }
    }

    private String _key;
    private Order _order;

    public SortKey(String key, Order order) {
        _key = key;
        _order = order;
    }

    public SortKey(String key) {
        this(key, Order.ASCENDING);
    }

    public String key() {
        return _key;
    }

    public Order order() {
        return _order;
    }

    public static SortKey citeableId(SortKey.Order order) {
        return new SortKey("cid", order);
    }

    public static SortKey objectName(SortKey.Order order) {
        return new SortKey("meta/daris:pssd-object/name", order);
    }

    public static SortKey modificationTime(SortKey.Order order) {
        return new SortKey("mtime", order);
    }

    public static SortKey mimeType(SortKey.Order order) {
        return new SortKey("type", order);
    }

    public static SortKey contentType(SortKey.Order order) {
        return new SortKey("content/type", order);
    }

    public static SortKey citeableId() {
        return new SortKey("cid");
    }

    public static SortKey objectName() {
        return new SortKey("meta/daris:pssd-object/name");
    }

    public static SortKey modificationTime() {
        return new SortKey("mtime");
    }

    public static SortKey mimeType() {
        return new SortKey("type");
    }

    public static SortKey contentType() {
        return new SortKey("content/type");
    }

    public SortKey copy() {
        return new SortKey(key(), order());
    }

    public boolean keyEquals(SortKey sortKey) {
        if (sortKey != null) {
            return ObjectUtil.equals(key(), sortKey.key());
        }
        return false;
    }

}

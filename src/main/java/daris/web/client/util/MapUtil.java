package daris.web.client.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {
    public static <K, V> Map<K, V> map(K k, V v) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        map.put(k, v);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> map(SimpleEntry<K, V>... entries) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (SimpleEntry<K, V> e : entries) {
            map.put(e.getKey(), e.getValue());
        }
        return map;
    }

    public static <K, V> Map<K, V> map(K[] ks, V[] vs) {
        assert ks != null && vs != null && ks.length == vs.length;
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < ks.length; i++) {
            map.put(ks[i], vs[i]);
        }
        return map;
    }

    public static <K, V> Map<K, V> map(K[] ks, V v) {
        assert ks != null && v != null;
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < ks.length; i++) {
            map.put(ks[i], v);
        }
        return map;
    }

    public static <K, V> Map<K, V> map(List<K> ks, List<V> vs) {
        assert ks != null && vs != null && ks.size() == vs.size();
        int n = ks.size();
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < n; i++) {
            map.put(ks.get(i), vs.get(i));
        }
        return map;
    }

    public static <K, V> Map<K, V> map(Collection<K> ks, V v) {
        assert ks != null && v != null;
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (K k : ks) {
            map.put(k, v);
        }
        return map;
    }
}

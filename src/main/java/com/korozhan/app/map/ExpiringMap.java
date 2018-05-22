package com.korozhan.app.map;

import java.util.Map;

public interface ExpiringMap<K, V> extends Map<K, V> {

    public boolean renewKey(K key);

    public V put(K key, V value, long lifeTimeMillis);

}

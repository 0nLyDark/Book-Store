package com.dangphuoctai.BookStore.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.units.qual.K;

import java.util.List;
import java.util.Map;

public interface BaseRedisService<K, F, V> {
    void set(K key, V value);

    void setTimeToLive(K key, long timeout, TimeUnit timeUnit);

    void setTimeToLiveOnce(K key, long timeout, TimeUnit unit);

    void hashSet(K key, F field, V value);

    boolean hashExists(K key, F field);

    V get(K key);

    Map<F, V> getField(K key);

    V hashGet(K key, F field);

    List<V> hashGetByFieldPrefix(K key, F fieldPrefix);

    List<V> hashGetByField(K key, List<F> fieldPrefix);

    Set<F> getFieldPrefixes(K key);

    void delete(K key);

    void delete(K key, F field);

    void delete(K key, List<F> fields);
}

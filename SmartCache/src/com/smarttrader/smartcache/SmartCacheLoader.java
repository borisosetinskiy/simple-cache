package com.smarttrader.smartcache;

import java.util.concurrent.atomic.AtomicInteger;

public interface SmartCacheLoader<K, C, V> {
    void load(SmartCacheKey<K> key, C c, SmartCacheListener<K,V> listener);
}

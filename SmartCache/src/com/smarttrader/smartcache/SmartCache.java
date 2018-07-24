package com.smarttrader.smartcache;

import java.util.Collection;
import java.util.Collections;

public interface SmartCache<K, V, R> {
    Collection<R> loadValue(SmartCacheLoader<K, V, R> loader, SmartCacheListener<K,R> listener);
}

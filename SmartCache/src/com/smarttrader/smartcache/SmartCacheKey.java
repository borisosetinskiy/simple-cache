package com.smarttrader.smartcache;

import java.util.concurrent.locks.Lock;

public interface SmartCacheKey<K> {
    Lock getLock();
    K getKey();
    boolean isLoaded();
    int size();
    int totalSize();
    void setLoaded();
    void free();
}

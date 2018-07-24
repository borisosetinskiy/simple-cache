package com.smarttrader.smartcache;

import java.util.concurrent.atomic.AtomicInteger;

public interface SmartCacheKeyCollection<K>  {
    SmartCacheKey<K> nextKey();
    void reset();
    int size();
    boolean isCompleted();
}

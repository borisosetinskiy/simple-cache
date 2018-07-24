package com.smarttrader.smartcache;

public interface SmartCacheListener<K, T> {
    void onSnapshot(SmartCacheSnapshot<T> snapshot, boolean end);
    void onUpdateSnapshot(SmartCacheSnapshot<T> snapshot, K key);
}

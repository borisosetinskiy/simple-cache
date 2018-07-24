package com.smarttrader.smartcache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmartCacheImpl<K , V>  implements SmartCache<K , Map<K, V>,  V > , SmartCacheListener<K,V> {
    private Map<K, V> cache = new ConcurrentHashMap<>();
    private SmartCacheKeyCollection<K> smartCacheKeyCollection;
    private Set<SmartCacheListener<K,V>> listeners = new HashSet<>();
    public SmartCacheImpl(SmartCacheKeyCollection<K> smartCacheKeyCollection) {
        this.smartCacheKeyCollection = smartCacheKeyCollection;
    }

    @Override
    public Collection<V> loadValue(SmartCacheLoader<K , Map<K, V>, V> loader, SmartCacheListener<K, V> listener) {
        if(smartCacheKeyCollection.isCompleted())
            return cache.values();
        if(!listeners.contains(listener)) {
            synchronized (listeners) {
                if(!listeners.contains(listener))
                    listeners.add(listener);
            }
        }
        SmartCacheKey<K> key = smartCacheKeyCollection.nextKey();
        if(key != null && key.getLock().tryLock()){
            try{
                loader.load(key, cache, this);
            }finally {
                key.getLock().unlock();
            }
        }
        return null;
    }

    @Override
    public void onSnapshot(SmartCacheSnapshot<V> snapshot, boolean end) {
        synchronized (listeners){
            for(SmartCacheListener smartCacheListener : listeners){
                smartCacheListener.onSnapshot(snapshot, end);
            }
        }
    }

    @Override
    public void onUpdateSnapshot(SmartCacheSnapshot<V> snapshot, K key) {

    }


}

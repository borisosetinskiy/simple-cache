package com.smarttrader.smartcache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class SmartCacheKeyCollectionImpl<K> implements SmartCacheKeyCollection<K> {
    private List<SmartCacheKey<K>> keys;
    private AtomicInteger completedSize = new AtomicInteger();
    private AtomicInteger index = new AtomicInteger();
    public SmartCacheKeyCollectionImpl(K[] values) {
        if(values == null)
            throw new RuntimeException();
        keys = Arrays.asList(values).stream().map((Function<K, SmartCacheKey<K>>) k -> new SmartCacheKeyImpl(k)).collect(Collectors.toList());

    }

    private int next(){
        return index.updateAndGet(operand -> {
            if(operand >= keys.size()-1)
                return 0;
            return ++operand;
        });
    }
    @Override
    public SmartCacheKey<K> nextKey() {
        final SmartCacheKey<K> key = keys.get(next());
        if(key.isLoaded())
            return null;
        return key;
    }

    @Override
    public void reset() {
        for(SmartCacheKey key: keys){
            key.free();
        }
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isCompleted() {
        return completedSize.get() == size();
    }

    class SmartCacheKeyImpl implements SmartCacheKey<K> {
        private Lock lock = new ReentrantLock();
        private K value;
        private volatile boolean loaded;
        public SmartCacheKeyImpl(K value) {
            this.value = value;
        }

        @Override
        public Lock getLock() {
            return lock;
        }

        @Override
        public K getKey() {
            return value;
        }

        @Override
        public synchronized boolean isLoaded() {
            return loaded;
        }

        @Override
        public int size() {
            return completedSize.get();
        }

        @Override
        public int totalSize() {
            return keys.size();
        }


        @Override
        public synchronized void setLoaded() {
            loaded = true;
            completedSize.incrementAndGet();
        }

        @Override
        public synchronized void free() {
            loaded = false;
            completedSize.decrementAndGet();
        }
    }


}

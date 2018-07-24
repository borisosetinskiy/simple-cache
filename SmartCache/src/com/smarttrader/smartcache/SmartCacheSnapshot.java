package com.smarttrader.smartcache;

import java.util.Collection;

public interface SmartCacheSnapshot<T> {
    Collection<T> getValue();
}

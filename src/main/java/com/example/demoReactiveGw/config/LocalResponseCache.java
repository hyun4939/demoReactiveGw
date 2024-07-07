package com.example.demoReactiveGw.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 간단한 로컬 캐시 구현
enum LocalResponseCache {
    INSTANCE;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        cache.put(key, value);
    }

    public String get(String key) {
        return cache.get(key);
    }
}
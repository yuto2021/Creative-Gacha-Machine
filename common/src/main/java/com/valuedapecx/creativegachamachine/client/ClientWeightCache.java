package com.valuedapecx.creativegachamachine.client;

import java.util.HashMap;
import java.util.Map;

public class ClientWeightCache {
    private static final Map<String, Float> CACHE = new HashMap<>();

    public static void setWeight(int page, int slot, float weight) {
        CACHE.put(page + ":" + slot, weight);
    }

    public static float getWeight(int page, int slot) {
        return CACHE.getOrDefault(page + ":" + slot, 1.0F);
    }
}
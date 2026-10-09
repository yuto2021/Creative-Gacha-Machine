package com.valuedapecx.creativegachamachine.client;

import com.valuedapecx.creativegachamachine.data.GachaPage;

import java.util.HashMap;
import java.util.Map;

public class ClientPageCache {
    private static final Map<Integer, GachaPage> PAGES = new HashMap<>();

    public static void setPage(int page, GachaPage data) {
        PAGES.put(page, data);
    }

    public static GachaPage getPage(int page) {
        return PAGES.get(page);
    }
}

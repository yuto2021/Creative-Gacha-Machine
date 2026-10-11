package com.valuedapecx.creativegachamachine.client;

import com.valuedapecx.creativegachamachine.data.GachaGroup;

import java.util.ArrayList;
import java.util.List;

public final class ClientGroupCache {
    private static final List<GachaGroup> GROUPS = new ArrayList<>();

    public static void setGroups(List<GachaGroup> groups) {
        GROUPS.clear();
        GROUPS.addAll(groups);
    }

    public static List<GachaGroup> getGroups() {
        return GROUPS;
    }

}
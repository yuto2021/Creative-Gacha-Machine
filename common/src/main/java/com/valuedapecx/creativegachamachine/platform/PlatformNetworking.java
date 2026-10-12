package com.valuedapecx.creativegachamachine.platform;

import com.valuedapecx.creativegachamachine.data.GachaGroup;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PlatformNetworking {

    @ExpectPlatform
    public static void sendAddGroup() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSelectGroup(int groupIndex) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendDuplicateGroup(int groupIndex) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendDeleteGroup(int groupIndex) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendGachaMissSlot(int slot) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendGachaPageAction(PageAction action) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendOpenNormalPayment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendOpenTicketPayment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSaveGroupSettings(int groupIndex, String name, int rollCount) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSaveTicketSettings(String ticketName, int ticketType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSaveWeight(int page, int slot, float weight) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSyncGroup(CompoundTag tag) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendSyncPage(ServerPlayer player, int page, CompoundTag pageTag) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void syncGroups(ServerPlayer player, List<GachaGroup> groups, HolderLookup.Provider registries) {
        throw new AssertionError();
    }
}
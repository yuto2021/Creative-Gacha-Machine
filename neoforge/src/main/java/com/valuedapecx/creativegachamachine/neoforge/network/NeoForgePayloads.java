package com.valuedapecx.creativegachamachine.neoforge.network;

import com.valuedapecx.creativegachamachine.CreativeGachaMachine;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.client.ClientGroupCache;
import com.valuedapecx.creativegachamachine.client.ClientPageCache;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.data.GachaPage;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.menu.GachaPaymentMenu;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(
        modid = CreativeGachaMachine.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public final class NeoForgePayloads {

    private NeoForgePayloads() {
    }

    @SubscribeEvent
    public static void register(
            RegisterPayloadHandlersEvent event
    ) {
        PayloadRegistrar registrar =
                event.registrar("1");

        registerC2S(registrar);
    }

    private static void registerC2S(
            PayloadRegistrar registrar
    ) {
        registrar.playToServer(
                AddGroupPayload.TYPE,
                AddGroupPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            GachaGroup group =
                                    new GachaGroup();

                            group.setName(
                                    "New Group"
                            );

                            group.setRollCount(
                                    1
                            );

                            blockEntity.getGroups()
                                    .add(group);

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    SyncGroupPayload.create(
                                            blockEntity.getGroups(),
                                            player.serverLevel()
                                                    .registryAccess()
                                    )
                            );

                            player.containerMenu
                                    .broadcastChanges();

                        })
        );
        registrar.playToServer(
                GachaMissSlotPayload.TYPE,
                GachaMissSlotPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            blockEntity.toggleMissSlot(
                                    payload.slot()
                            );

                            player.containerMenu
                                    .broadcastChanges();

                            System.out.println(
                                    "TOGGLE SLOT = "
                                            + payload.slot()
                            );

                        })
        );
        registrar.playToServer(
                GachaPageActionPayload.TYPE,
                GachaPageActionPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            switch (payload.action()) {

                                case PREVIOUS -> {
                                    blockEntity.previousPage();
                                    player.containerMenu.broadcastChanges();
                                }

                                case NEXT -> {
                                    blockEntity.nextPage();
                                    player.containerMenu.broadcastChanges();
                                }

                                case ADD -> {
                                    blockEntity.addPage();
                                    player.containerMenu.broadcastChanges();
                                }

                                case REMOVE -> {

                                    GachaMachineBlockEntity.RemovePageResult result =
                                            blockEntity.removePage();

                                    switch (result) {

                                        case LAST_PAGE ->
                                                player.displayClientMessage(
                                                        Component.translatable(
                                                                "message.creative_gacha_machine.cannot_delete_last_page"
                                                        ).withStyle(
                                                                ChatFormatting.RED
                                                        ),
                                                        true
                                                );

                                        case NOT_EMPTY ->
                                                player.displayClientMessage(
                                                        Component.translatable(
                                                                "message.creative_gacha_machine.page_not_empty"
                                                        ).withStyle(
                                                                ChatFormatting.RED
                                                        ),
                                                        true
                                                );
                                    }
                                }
                            }

                            blockEntity.setChanged();

                        })
        );
        registrar.playToServer(
                OpenNormalPaymentPayload.TYPE,
                OpenNormalPaymentPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaTicketPaymentMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            player.openMenu(
                                    blockEntity.createPaymentMenu()
                            );

                        })
        );
        registrar.playToClient(
                SyncGroupPayload.TYPE,
                SyncGroupPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            List<GachaGroup> groups =
                                    new ArrayList<>();

                            if (payload.tag() == null) {
                                return;
                            }

                            ListTag list =
                                    payload.tag()
                                            .getList(
                                                    "Groups",
                                                    10
                                            );

                            for (int i = 0; i < list.size(); i++) {

                                groups.add(
                                        GachaGroup.load(
                                                list.getCompound(i),
                                                Minecraft.getInstance()
                                                        .level != null
                                                        ? Minecraft.getInstance()
                                                        .level
                                                        .registryAccess()
                                                        : null
                                        )
                                );
                            }

                            ClientGroupCache.setGroups(
                                    groups
                            );

                        })
        );
        registrar.playToServer(
                DeleteGroupPayload.TYPE,
                DeleteGroupPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            if (payload.groupIndex() < 0
                                    || payload.groupIndex()
                                    >= blockEntity.getGroupCount()) {
                                return;
                            }

                            if (!blockEntity.canDeleteGroup()) {
                                return;
                            }

                            blockEntity.removeGroup(
                                    payload.groupIndex()
                            );

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    SyncGroupPayload.create(
                                            blockEntity.getGroups(),
                                            player.serverLevel()
                                                    .registryAccess()
                                    )
                            );

                            player.containerMenu
                                    .broadcastChanges();
                        })
        );
        registrar.playToServer(
                DuplicateGroupPayload.TYPE,
                DuplicateGroupPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            if (payload.groupIndex() < 0
                                    || payload.groupIndex()
                                    >= blockEntity.getGroups().size()) {
                                return;
                            }

                            GachaGroup original =
                                    blockEntity.getGroups()
                                            .get(payload.groupIndex());

                            GachaGroup copy =
                                    GachaGroup.load(
                                            original.save(
                                                    player.serverLevel()
                                                            .registryAccess()
                                            ),
                                            player.registryAccess()
                                    );

                            copy.setName(
                                    original.getName()
                                            + " Copy"
                            );

                            blockEntity.getGroups()
                                    .add(copy);

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    SyncGroupPayload.create(
                                            blockEntity.getGroups(),
                                            player.serverLevel()
                                                    .registryAccess()
                                    )
                            );

                            player.containerMenu
                                    .broadcastChanges();
                        })
        );
        registrar.playToServer(
                OpenTicketPaymentPayload.TYPE,
                OpenTicketPaymentPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaPaymentMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            player.openMenu(
                                    blockEntity.createTicketPaymentMenu()
                            );
                        })
        );
        registrar.playToServer(
                SaveGroupSettingsPayload.TYPE,
                SaveGroupSettingsPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            if (payload.groupIndex() < 0
                                    || payload.groupIndex()
                                    >= blockEntity.getGroups().size()) {
                                return;
                            }

                            GachaGroup group =
                                    blockEntity.getGroups()
                                            .get(payload.groupIndex());

                            group.setName(
                                    payload.name()
                            );

                            group.setRollCount(
                                    Math.max(
                                            1,
                                            payload.rollCount()
                                    )
                            );

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    SyncGroupPayload.create(
                                            blockEntity.getGroups(),
                                            player.serverLevel()
                                                    .registryAccess()
                                    )
                            );
                        })
        );
        registrar.playToServer(
                SaveTicketSettingsPayload.TYPE,
                SaveTicketSettingsPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaTicketPaymentMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            blockEntity.setRequiredTicketName(
                                    payload.ticketName()
                            );

                            blockEntity.setRequiredTicketType(
                                    payload.ticketType()
                            );

                            blockEntity.setTicketOnlyPayment(
                                    true
                            );

                            blockEntity.setChanged();
                        })
        );
        registrar.playToServer(
                SelectGroupPayload.TYPE,
                SelectGroupPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            blockEntity.setCurrentGroup(
                                    payload.groupIndex()
                            );

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    SyncGroupPayload.create(
                                            blockEntity.getGroups(),
                                            player.serverLevel()
                                                    .registryAccess()
                                    )
                            );
                        })
        );
        registrar.playToClient(
                SyncPagePayload.TYPE,
                SyncPagePayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            Minecraft minecraft =
                                    Minecraft.getInstance();

                            if (minecraft.level == null) {
                                return;
                            }

                            GachaPage page =
                                    GachaPage.load(
                                            payload.pageTag(),
                                            minecraft.level.registryAccess()
                                    );

                            System.out.println(
                                    "CLIENT SYNC PAGE="
                                            + payload.page()
                            );

                            System.out.println(
                                    "CLIENT WEIGHT SLOT0="
                                            + page.getWeight(0)
                            );

                            System.out.println(
                                    "CLIENT WEIGHT SLOT9="
                                            + page.getWeight(9)
                            );

                            ClientPageCache.setPage(
                                    payload.page(),
                                    page
                            );
                        })
        );
        registrar.playToServer(
                SaveWeightPayload.TYPE,
                SaveWeightPayload.STREAM_CODEC,
                (payload, context) ->
                        context.enqueueWork(() -> {

                            System.out.println(
                                    "SAVE PAGE=" + payload.page()
                                            + " SLOT=" + payload.slot()
                                            + " WEIGHT=" + payload.weight()
                            );

                            ServerPlayer player =
                                    (ServerPlayer) context.player();

                            if (!(player.containerMenu
                                    instanceof GachaMachineMenu menu)) {
                                return;
                            }

                            GachaMachineBlockEntity blockEntity =
                                    menu.getBlockEntity();

                            if (blockEntity == null) {
                                return;
                            }

                            System.out.println(
                                    "CURRENT PAGE=" +
                                            blockEntity.getCurrentPage()
                            );

                            GachaPage page =
                                    blockEntity.getCurrentPageData();

                            page.setWeight(
                                    payload.slot(),
                                    payload.weight()
                            );

                            blockEntity.setChanged();

                            PacketDistributor.sendToPlayer(
                                    player,
                                    new SyncPagePayload(
                                            blockEntity.getCurrentPage(),
                                            page.save(
                                                    player.serverLevel()
                                                            .registryAccess()
                                            )
                                    )
                            );
                        })
        );
    }
}
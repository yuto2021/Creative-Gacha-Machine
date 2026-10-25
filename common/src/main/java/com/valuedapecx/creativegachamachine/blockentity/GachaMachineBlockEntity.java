package com.valuedapecx.creativegachamachine.blockentity;

import com.valuedapecx.creativegachamachine.block.GachaMachineBlock;
import com.valuedapecx.creativegachamachine.data.*;
import com.valuedapecx.creativegachamachine.menu.GachaMachineMenu;
import com.valuedapecx.creativegachamachine.menu.GachaPaymentMenu;
import com.valuedapecx.creativegachamachine.menu.GachaTicketPaymentMenu;
import com.valuedapecx.creativegachamachine.registry.ModBlockEntities;
import com.valuedapecx.creativegachamachine.registry.ModItems;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GachaMachineBlockEntity extends BlockEntity implements Container, MenuProvider, ExtendedMenuProvider {
    private ItemStack paymentItem = ItemStack.EMPTY;

    private final List<GachaGroup> groups = new ArrayList<>();

    private int currentGroup = 0;
    private int currentPage = 0;

    public enum RemovePageResult {
        SUCCESS,
        LAST_PAGE,
        NOT_EMPTY
    }

    private GachaMachineMode mode = GachaMachineMode.CONFIG;

    private boolean ticketOnlyPayment;
    private String requiredTicketName = "";
    private int requiredTicketType = 0;

    private DisplayState displayState = DisplayState.READY;

    private int rollable = 0;

    private int displayTicks = 0;

    private UUID displayUuid;

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GACHA_MACHINE.get(), pos, state);
        groups.add(new GachaGroup());
    }

    public List<GachaGroup> getGroups() {
        return groups;
    }

    public GachaGroup getCurrentGroupData() {
        return groups.get(currentGroup);
    }

    /*
     * ページ
     */

    public GachaPage getCurrentPageData() {
        return getCurrentGroupData().getPages().get(currentPage);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageCount() {
        return getCurrentGroupData().getPages().size();
    }

    public void addPage() {
        getCurrentGroupData().getPages().add(new GachaPage());
        currentPage = getCurrentGroupData().getPages().size() - 1;
        setChanged();
    }

    public void nextPage() {
        if (currentPage < getPageCount() - 1) {
            currentPage++;
            setChanged();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setChanged();
        }
    }

    public RemovePageResult removePage() {
        if (getPageCount() <= 1) {
            return RemovePageResult.LAST_PAGE;
        }

        if (!getCurrentGroupData().getPages().get(currentPage).isEmpty()) {
            return RemovePageResult.NOT_EMPTY;
        }

        getCurrentGroupData().getPages().remove(currentPage);
        currentPage = Math.max(0, currentPage - 1);

        setChanged();

        return RemovePageResult.SUCCESS;
    }

    /*
     * ガチャ設定
     */



    /*
     * NBT
     */

    @Override
    protected void saveAdditional(
            CompoundTag tag, HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        tag.putInt("CurrentPage", currentPage);

        tag.putInt("CurrentGroup", currentGroup);

        ListTag groupsTag = new ListTag();

        for (GachaGroup group : groups) {
            groupsTag.add(group.save(registries));
        }

        tag.put("GachaGroups", groupsTag);
        tag.putString("Mode", mode.name());

        if (!paymentItem.isEmpty()) {
            tag.put("PaymentItem", paymentItem.save(registries));
        }

        tag.putBoolean("TicketOnlyPayment", ticketOnlyPayment);
        tag.putString("RequiredTicketName", requiredTicketName);
        tag.putInt("RequiredTicketType", requiredTicketType);

        if (displayUuid != null) {
            tag.putUUID("DisplayUUID", displayUuid);
        }

        tag.putInt("Rollable", rollable);
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        currentPage = tag.getInt("CurrentPage");
        currentGroup = tag.getInt("CurrentGroup");

        groups.clear();

        ListTag groupsTag = tag.getList("GachaGroups", Tag.TAG_COMPOUND);

        for (int i = 0; i < groupsTag.size(); i++) {
            groups.add(GachaGroup.load(groupsTag.getCompound(i), registries));
        }

        if (groups.isEmpty()) {
            groups.add(new GachaGroup());
        }

        if (currentPage >= getPageCount()) {
            currentPage = 0;
        }

        try {
            mode = GachaMachineMode.valueOf(
                    tag.getString("Mode")
            );
        } catch (Exception e) {
            mode = GachaMachineMode.CONFIG;
        }

        if (tag.contains("PaymentItem")) {
            paymentItem =
                    ItemStack.parse(registries, tag.getCompound("PaymentItem")).orElse(ItemStack.EMPTY);
        }

        ticketOnlyPayment = tag.getBoolean("TicketOnlyPayment");
        requiredTicketName = tag.getString("RequiredTicketName");
        requiredTicketType = tag.getInt("RequiredTicketType");

        if (tag.hasUUID("DisplayUUID")) {
            displayUuid = tag.getUUID("DisplayUUID");
        }

        rollable = tag.getInt("Rollable");
    }

    /*
     * GUI
     */

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(
                "block.creative_gacha_machine.gacha_machine"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GachaMachineMenu(id, inventory, this);
    }

    /*
     * Container
     */

    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    public boolean isEmpty() {
        return getCurrentPageData().isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(
            int slot
    ) {
        return getCurrentPageData().getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {

        ItemStack stack = getItem(slot);

        ItemStack result = stack.split(amount);

        setChanged();

        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);

        setItem(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        getCurrentPageData().setItem(slot, stack);

        setChanged();

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < 54; i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
    }

    public boolean isMissSlot(int slot) {
        return getCurrentPageData().isMissSlot(slot);
    }

    public void toggleMissSlot(int slot) {
        getCurrentPageData().toggleMissSlot(slot);

        System.out.println("MISS=" + getCurrentPageData().isMissSlot(slot));

        setChanged();
    }

    public GachaMachineMode getMode() {
        return mode;
    }

    public void nextMode() {
        mode = mode.next();

        if (mode == GachaMachineMode.RUNNING) {
            setDisplayState(isTollGacha() ? DisplayState.NO_MONEY : DisplayState.READY);
        }

        setChanged();
    }

    public boolean isTollGacha() {
        return ticketOnlyPayment || !paymentItem.isEmpty();
    }

    public ItemStack getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(ItemStack stack) {
        paymentItem = stack.copy();
        setChanged();
    }

    public MenuProvider createPaymentMenu() {
        return new SimpleMenuProvider((containerId, inventory, player) ->
                        new GachaPaymentMenu(containerId, inventory, this),
                        Component.translatable("gui.creative_gacha_machine.payment")
        );
    }

    public void consumePayment(Player player) {
        if (ticketOnlyPayment) {
            consumeTicket(player);
            return;
        }

        consumeNormalPayment(player);
    }

    public void consumeTicket(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            boolean dataHasTag = (data != null);

            if (!stack.is(ModItems.GACHA_TICKET.get())) {
                continue;
            }

            if (dataHasTag) {
                continue;
            }

            if (data != null) {
                CompoundTag tag = data.copyTag();
                if (requiredTicketName.equals(tag.getString("TicketName")) &&
                        requiredTicketType == tag.getInt("TicketType")) {
                    stack.shrink(1);
                    return;
                }
            }
        }
    }

    public void consumeNormalPayment(Player player) {
        if (paymentItem.isEmpty()) {
            return;
        }

        int remaining = paymentItem.getCount();

        for (ItemStack stack : player.getInventory().items) {
            if (!ItemStack.isSameItemSameComponents(stack, paymentItem)) {
                continue;
            }

            int remove = Math.min(remaining, stack.getCount());

            stack.shrink(remove);
            remaining -= remove;

            if (remaining <= 0) {
                break;
            }
        }
    }

    public GachaResult selectReward(GachaGroup group) {
        Level level = getLevel();

        if (level == null) {
            setDisplayState(DisplayState.ERROR);

            return GachaResult.NONE(-1);
        }

        float totalWeight = 0F;

        for (GachaPage page : group.getPages()) {
            for (int slot = 0; slot < 54; slot++) {
                if (page.isMissSlot(slot)) {
                    totalWeight += page.getWeight(slot);

                    continue;
                }

                ItemStack stack = page.getItem(slot);

                if (stack.isEmpty()) {
                    continue;
                }

                totalWeight += page.getWeight(slot);
            }
        }

        if (totalWeight <= 0F) {
            return GachaResult.NONE(0);
        }

        float randomWeight = level.random.nextFloat() * totalWeight;

        for (int pageIndex = 0; pageIndex < getCurrentGroupData().getPages().size(); pageIndex++) {
            GachaPage page = getCurrentGroupData().getPages().get(pageIndex);

            for (int slot = 0; slot < 54; slot++) {
                int totalSlot = (pageIndex * 54) + slot;

                float weight = page.getWeight(slot);

                randomWeight -= weight;

                if (randomWeight > 0F) {
                    continue;
                }

                if (page.isMissSlot(slot)) {
                    return GachaResult.MISS(totalSlot);
                }

                ItemStack stack = page.getItem(slot);

                if (!stack.isEmpty()) {
                    return GachaResult.REWARD(stack.copy(), slot);
                }

                setDisplayState(DisplayState.ERROR);

                return GachaResult.NONE(slot);
            }
        }

        setDisplayState(DisplayState.ERROR);

        return GachaResult.NONE(0);
    }

    public void dispenseReward(GachaResult gachaResult) {
        ItemStack reward = gachaResult.reward();
        if (level == null || reward.isEmpty()) {
            if (level != null) {
                getBlockState().getValue(GachaMachineBlock.FACING);

                double x = worldPosition.getX() + 0.5D;

                double y = worldPosition.getY() + 0.25D;

                double z = worldPosition.getZ() + 0.5D;

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 8, 0.05, 0.05, 0.05, 0.01);
                }
                level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.DISPENSER_DISPENSE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.2F);
            }
            return;
        } else {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.DISPENSER_DISPENSE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        Direction facing = getBlockState().getValue(GachaMachineBlock.FACING);

        double x = worldPosition.getX() + 0.5D;
        double y = worldPosition.getY() + 0.25D;
        double z = worldPosition.getZ() + 0.5D;

        // 正面下の排出口
        x += facing.getStepX() * 0.65D;
        z += facing.getStepZ() * 0.65D;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, reward.copy());

        // ドロッパー風に前へ飛ばす
        itemEntity.setDeltaMovement(facing.getStepX() * 0.20D, 0.05D, facing.getStepZ() * 0.20D);

        level.addFreshEntity(itemEntity);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 8, 0.05, 0.05, 0.05, 0.01);
        }
    }

    public boolean canPay(Player player) {
        if (!ticketOnlyPayment) {
            return canPayNormal(player);
        }

        ItemStack mainHand = player.getMainHandItem();

        if (!mainHand.is(ModItems.GACHA_TICKET.get())) {
            return false;
        }

        CustomData data = mainHand.get(DataComponents.CUSTOM_DATA);

        if (data == null) {
            return false;
        }

        CompoundTag tag = data.copyTag();

        return requiredTicketName.equals(tag.getString("TicketName"))
                && requiredTicketType == tag.getInt("TicketType");
    }

    public boolean canPayNormal(Player player) {
        if (paymentItem.isEmpty()) {
            return true;
        }

        ItemStack mainHand = player.getMainHandItem();

        return ItemStack.isSameItemSameComponents(
                mainHand,
                paymentItem
        ) && mainHand.getCount()
                >= paymentItem.getCount();
    }

    public boolean isTicketOnlyPayment() {
        return ticketOnlyPayment;
    }

    public String getRequiredTicketName() {
        return requiredTicketName;
    }

    public int getRequiredTicketType() {
        return requiredTicketType;
    }

    public void setTicketOnlyPayment(boolean value) {
        this.ticketOnlyPayment = value;
        setChanged();
    }

    public void setRequiredTicketName(String value) {
        this.requiredTicketName = value;
        setChanged();
    }

    public void setRequiredTicketType(int value) {
        this.requiredTicketType = value;
        setChanged();
    }

    public void setTicketPaymentMode() {
        setChanged();
    }

    public MenuProvider createTicketPaymentMenu() {
        return new SimpleMenuProvider((containerId, inventory, player) ->
                new GachaTicketPaymentMenu(containerId, inventory, this),
                Component.translatable("gui.creative_gacha_machine.ticket_payment")
        );
    }

    public @NotNull BlockPos getBlockPos() {
        return worldPosition;
    }

    public void removeGroup(int index) {
        if (groups.size() <= 1) {
            return;
        }

        groups.remove(index);

        if (currentGroup >= groups.size()) {
            currentGroup = groups.size() - 1;
        }

        currentPage = 0;

        setChanged();
    }

    public void setCurrentGroup(int index) {
        if (index < 0 || index >= groups.size()) {
            return;
        }

        currentGroup = index;
        currentPage = 0;

        setChanged();
    }

    public boolean canDeleteGroup() {
        return groups.size() > 1;
    }

    public DisplayState getDisplayState() {
        return displayState;
    }

    public void setDisplayState(
            DisplayState state
    ) {
        this.displayState = state;

        updateDisplay();

        if (level != null) {
            int value = switch (state) {
                case READY -> isTollGacha() ? 1 : 2;
                case LOADED -> 1;
                case DISPENSING -> isRollable() ? 1 : isTollGacha() ? 1 : 2;
                case DISPENSED -> isRollable() ? 1 : isTollGacha() ? 1 : 2;
                case NO_MONEY -> 1;
                case JACKPOT -> 1;
                case SOLD_OUT -> 1;
                case ERROR -> 3;
            };

            level.setBlock(
                    worldPosition,
                    getBlockState()
                            .setValue(
                                    GachaMachineBlock.DISPLAY,
                                    value
                            ),
                    3
            );
        }

        setChanged();
    }

    public boolean isRollable() {
        return rollable > 0;
    }

    public int getRollableCount() {
        return rollable;
    }

    public void addRollable(
            int i
    ) {
        this.rollable += i;

        updateDisplay();
    }

    public void returnPayment(Player player) {
        if (rollable <= 0 || paymentItem.isEmpty()) {
            return;
        }

        ItemStack refund = paymentItem.copy();

        refund.setCount(paymentItem.getCount() * rollable);

        if (!player.addItem(refund)) {
            player.drop(refund, false);
        }

        rollable = 0;

        setChanged();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(
            HolderLookup.Provider registries
    ) {
        CompoundTag tag = new CompoundTag();

        saveAdditional(
                tag,
                registries
        );

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void resetState() {
        if (isRollable()) {
            setDisplayState(DisplayState.LOADED);
        } else {
            setDisplayState(isTollGacha() ? DisplayState.NO_MONEY : DisplayState.READY);
        }
    }

    public void setDisplayStateForTicks(
            DisplayState state,
            int ticks
    ) {
        this.displayState = state;
        this.displayTicks = ticks;
        setChanged();
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            GachaMachineBlockEntity machine
    ) {
        if (level.isClientSide()) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (machine.displayTicks > 0) {
            machine.displayTicks--;

            if (machine.displayTicks <= 0) {
                machine.setDisplayState(
                        machine.isTollGacha()
                                && !machine.isRollable()
                                ? DisplayState.NO_MONEY
                                : DisplayState.READY
                );
            }
        }

        if (!machine.hasDisplay()) {
            machine.createDisplay();
        }
    }

    public boolean hasDisplay() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        return displayUuid != null && serverLevel.getEntity(displayUuid) != null;
    }

    public void createDisplay() {
        System.out.println("CREATE DISPLAY");

        if (!(level instanceof ServerLevel serverLevel)) {
            System.out.println("NOT SERVER");
            return;
        }

        Display.TextDisplay display =
                EntityType.TEXT_DISPLAY.create(
                        serverLevel
                );

        if (display == null) {
            System.out.println("DISPLAY NULL");
            return;
        }

        CompoundTag tag = new CompoundTag();

        tag.putString(
                "text",
                Component.Serializer.toJson(
                        Component.literal("残り0回"),
                        serverLevel.registryAccess()
                )
        );

        display.load(tag);

        Direction facing =
                getBlockState().getValue(
                        GachaMachineBlock.FACING
                );

        float yaw = switch (facing) {
            case SOUTH -> 0F;
            case WEST  -> 90F;
            case NORTH -> 180F;
            case EAST  -> -90F;
            default    -> 0F;
        };

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 2.2;
        double z = worldPosition.getZ() + 0.5;

        // ガチャ正面へ少し移動
        x += facing.getStepX() * 0.35;
        z += facing.getStepZ() * 0.35;

        display.moveTo(
                x,
                y,
                z,
                yaw,
                0F
        );

        display.setCustomNameVisible(false);

        serverLevel.addFreshEntity(display);

        displayUuid = display.getUUID();

        System.out.println("DISPLAY = " + display + ", UUID:" + displayUuid + ", NBT:" + display.saveWithoutId(new CompoundTag()));
    }

    public void updateDisplay() {

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (displayUuid == null) {
            return;
        }

        Entity entity =
                serverLevel.getEntity(
                        displayUuid
                );

        if (!(entity instanceof Display.TextDisplay display)) {
            return;
        }

        CompoundTag tag = display.saveWithoutId(new CompoundTag());

        tag.putString(
                "text",
                Component.Serializer.toJson(
                        Component.literal(
                                "残り" + rollable + "回"
                        ),
                        serverLevel.registryAccess()
                )
        );

        display.load(tag);
    }

    public void removeDisplay() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (displayUuid == null) {
            return;
        }

        Entity entity =
                serverLevel.getEntity(
                        displayUuid
                );

        if (entity != null) {
            entity.discard();
        }
    }
}
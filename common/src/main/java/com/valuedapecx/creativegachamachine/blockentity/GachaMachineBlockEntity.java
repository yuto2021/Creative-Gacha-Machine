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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
            CompoundTag tag
    ) {
        super.saveAdditional(tag);

        tag.putInt("CurrentPage", currentPage);

        tag.putInt("CurrentGroup", currentGroup);

        ListTag groupsTag = new ListTag();

        for (GachaGroup group : groups) {
            groupsTag.add(group.save());
        }

        tag.put("GachaGroups", groupsTag);
        tag.putString("Mode", mode.name());

        if (!paymentItem.isEmpty()) {
            tag.put("PaymentItem", paymentItem.save(new CompoundTag()));
        }

        tag.putBoolean("TicketOnlyPayment", ticketOnlyPayment);
        tag.putString("RequiredTicketName", requiredTicketName);
        tag.putInt("RequiredTicketType", requiredTicketType);
    }

    @Override
    public void load(
            CompoundTag tag
    ) {
        super.load(tag);

        currentPage = tag.getInt("CurrentPage");
        currentGroup = tag.getInt("CurrentGroup");

        groups.clear();

        ListTag groupsTag = tag.getList("GachaGroups", Tag.TAG_COMPOUND);

        for (int i = 0; i < groupsTag.size(); i++) {
            groups.add(GachaGroup.load(groupsTag.getCompound(i)));
        }

        if (groups.isEmpty()) {
            groups.add(new GachaGroup());
        }

        if (currentPage >= getPageCount()) {
            currentPage = 0;
        }

        try {
            mode = GachaMachineMode.valueOf(tag.getString("Mode"));
        } catch (Exception e) {
            mode = GachaMachineMode.CONFIG;
        }

        if (tag.contains("PaymentItem")) {
            paymentItem = ItemStack.of(tag.getCompound("PaymentItem"));
        }

        ticketOnlyPayment = tag.getBoolean("TicketOnlyPayment");
        requiredTicketName = tag.getString("RequiredTicketName");
        requiredTicketType = tag.getInt("RequiredTicketType");
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

        setChanged();
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
            if (!stack.is(ModItems.GACHA_TICKET.get())) {
                continue;
            }

            if (!stack.hasTag()) {
                continue;
            }

            if (stack.getTag() != null && requiredTicketName.equals(stack.getTag().getString("TicketName"))
                    && requiredTicketType == stack.getTag().getInt("TicketType")) {
                stack.shrink(1);
                return;
            }
        }
    }

    public void consumeNormalPayment(Player player) {
        if (paymentItem.isEmpty()) {
            return;
        }

        int remaining = paymentItem.getCount();

        for (ItemStack stack : player.getInventory().items) {
            if (!ItemStack.isSameItemSameTags(stack, paymentItem)) {
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

                return GachaResult.NONE(slot);
            }
        }

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

        for (ItemStack stack : player.getInventory().items) {
            if (!stack.is(ModItems.GACHA_TICKET.get())) {
                continue;
            }

            if (!stack.hasTag()) {
                continue;
            }

            if (stack.getTag() != null && requiredTicketName.equals(stack.getTag().getString("TicketName"))
                    && requiredTicketType == stack.getTag().getInt("TicketType")) {
                return true;
            }
        }

        return false;
    }

    public boolean canPayNormal(Player player) {
        if (paymentItem.isEmpty()) {
            return true;
        }

        int count = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (ItemStack.isSameItemSameTags(stack, paymentItem)) {
                count += stack.getCount();
            }
        }

        return count >= paymentItem.getCount();
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
}
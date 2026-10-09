package com.valuedapecx.creativegachamachine.block;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.data.GachaResult;
import com.valuedapecx.creativegachamachine.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GachaMachineBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public GachaMachineBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();

        if (!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }

        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(HALF, DoubleBlockHalf.LOWER
        );
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (!player.isCreative()) {
            return 0.0F;
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);

        BlockPos otherPos = (half == DoubleBlockHalf.LOWER ? pos.above() : pos.below());

        if (level.getBlockState(otherPos).getBlock() == this) {
            level.removeBlock(otherPos, false);
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GachaMachineBlockEntity(pos, state);
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {

        if (state.getBlock()
                == newState.getBlock()) {
            return;
        }

        DoubleBlockHalf half =
                state.getValue(
                        HALF
                );

        BlockPos otherPos =
                half == DoubleBlockHalf.LOWER

                        ? pos.above()

                        : pos.below();

        BlockState otherState =
                level.getBlockState(
                        otherPos
                );

        if (otherState.is(this)
                && otherState.getValue(HALF)
                != half) {

            level.destroyBlock(
                    otherPos,
                    false
            );
        }

        super.onRemove(
                state,
                level,
                pos,
                newState,
                movedByPiston
        );
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {

        DoubleBlockHalf half = state.getValue(HALF);

        Direction otherDirection = (half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);

        if (direction == otherDirection) {
            if (!neighborState.is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        System.out.println("Right click detected");

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            level.getBlockState(pos);
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof GachaMachineBlockEntity machine) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.is(ModItems.GACHA_WRENCH.get())) {
                if (!level.isClientSide()) {
                    machine.nextMode();

                    player.displayClientMessage(Component.translatable(switch (machine.getMode()) {
                        case CONFIG -> "mode.creative_gacha_machine.config";
                        case PAYMENT -> "mode.creative_gacha_machine.payment";
                        case RUNNING -> "mode.creative_gacha_machine.running";
                    }), true);
                }

                return InteractionResult.SUCCESS;
            }
            switch (machine.getMode()) {
                case CONFIG -> player.openMenu(machine);

                case PAYMENT -> player.openMenu(machine.createPaymentMenu());

                case RUNNING -> {
                    if (!machine.canPay(player)) {
                        player.displayClientMessage(
                                Component.translatable("message.creative_gacha_machine.not_enough_money"), true
                        );

                        return InteractionResult.SUCCESS;
                    }

                    machine.consumePayment(player);

                    for (GachaGroup group : machine.getGroups()) {
                        for (int i = 0; i < group.getRollCount(); i++) {
                            GachaResult result = machine.selectReward(group);
                            machine.dispenseReward(result);
                        }
                    }
                }
            }
        }

        return InteractionResult.CONSUME;
    }
}
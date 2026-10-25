package com.valuedapecx.creativegachamachine.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import com.valuedapecx.creativegachamachine.data.GachaGroup;
import com.valuedapecx.creativegachamachine.data.GachaPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GachaMachineRenderer
        implements BlockEntityRenderer<GachaMachineBlockEntity> {

    public GachaMachineRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        System.out.println(
                "RENDERER CREATED"
        );
    }

    @Override
    public void render(
            GachaMachineBlockEntity entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        if (entity.getLevel() == null) {
            return;
        }

        ItemStack displayItem = getRandomDisplayItem(entity);
        // ItemStack displayItem = new ItemStack(Items.DIAMOND);

        if (displayItem.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // ガラスケース中央
        long gameTime = entity.getLevel().getGameTime();

        // ふわふわ
        float offset =
                Mth.sin(
                        (gameTime + partialTick) * 0.08F
                ) * 0.05F;

        poseStack.translate(
                0.5D,
                1.5D + offset,
                0.5D
        );

        // ゆっくり回転
        float rotation = (gameTime + partialTick) * 2.0F;

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // サイズ
        poseStack.scale(
                0.75F,
                0.75F,
                0.75F
        );

        ItemRenderer itemRenderer =
                Minecraft.getInstance().getItemRenderer();

        itemRenderer.renderStatic(
                displayItem,
                ItemDisplayContext.FIXED,
                0xF000F0,
                packedOverlay,
                poseStack,
                buffer,
                entity.getLevel(),
                0
        );

        poseStack.popPose();
    }

    private ItemStack getRandomDisplayItem(GachaMachineBlockEntity entity) {
        List<ItemStack> rewards = new ArrayList<>();

        for (GachaGroup group : entity.getGroups()) {
            for (GachaPage page : group.getPages()) {
                for (int slot = 0; slot < 54; slot++) {
                    ItemStack stack = page.getItem(slot);

                    if (!stack.isEmpty()) {
                        rewards.add(stack);
                    }
                }
            }
        }

        if (rewards.isEmpty()) {
            return ItemStack.EMPTY;
        }

        long seed = (Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0) / 60L;

        Random random = new Random(seed);

        return rewards.get(
                random.nextInt(rewards.size())
        );
    }
}
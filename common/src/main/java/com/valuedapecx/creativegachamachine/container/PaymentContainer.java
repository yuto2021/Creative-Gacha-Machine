package com.valuedapecx.creativegachamachine.container;

import com.valuedapecx.creativegachamachine.blockentity.GachaMachineBlockEntity;
import net.minecraft.world.SimpleContainer;

public class PaymentContainer extends SimpleContainer {
    private final GachaMachineBlockEntity blockEntity;

    public PaymentContainer(GachaMachineBlockEntity blockEntity) {
        super(1);

        this.blockEntity = blockEntity;

        setItem(0, blockEntity.getPaymentItem());
    }

    @Override
    public void setChanged() {
        blockEntity.setPaymentItem(getItem(0));

        super.setChanged();
    }
}

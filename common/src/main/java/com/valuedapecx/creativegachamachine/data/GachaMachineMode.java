package com.valuedapecx.creativegachamachine.data;

public enum GachaMachineMode {

    CONFIG,
    PAYMENT,
    RUNNING;

    public GachaMachineMode next() {
        return switch (this) {
            case CONFIG -> PAYMENT;
            case PAYMENT -> RUNNING;
            case RUNNING -> CONFIG;
        };
    }
}
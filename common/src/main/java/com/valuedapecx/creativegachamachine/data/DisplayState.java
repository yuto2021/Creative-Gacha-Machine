package com.valuedapecx.creativegachamachine.data;

public enum DisplayState {
    READY,       // 待機中
    LOADED,      // 支払い済み
    NO_MONEY,    // お金不足
    DISPENSING,  // 抽選中
    DISPENSED,   // 排出完了
    JACKPOT,     // 大当たり
    SOLD_OUT,    // 景品切れ
    ERROR        // 設定不備
}

package com.qianmi.ms.starter.rocketmq.enums;

/**
 * Consume Mode
 * Created by aqlu on 2017/9/28.
 */
public enum ConsumeMode {
    /**
     * receive asynchronously delivered messages concurrently
     */
    CONCURRENTLY,

    /**
     * receive asynchronously delivered messages orderly. one queue, one thread
     */
    Orderly
}

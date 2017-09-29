package com.qianmi.ms.starter.rocketmq.core;

/**
 * RocketMQListener
 * Created by aqlu on 2017/9/28.
 */
public interface RocketMQListener<T> {
    void onMessage(T message);
}

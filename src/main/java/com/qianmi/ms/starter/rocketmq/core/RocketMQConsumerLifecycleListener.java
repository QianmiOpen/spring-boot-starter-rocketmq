package com.qianmi.ms.starter.rocketmq.core;

/**
 * RocketMQ Consumer Lifecycle Listener
 * Created by aqlu on 2017/9/30.
 */
public interface RocketMQConsumerLifecycleListener<T> {
    void prepareStart(final T consumer);
}

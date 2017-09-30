package com.qianmi.ms.starter.rocketmq.core;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

/**
 * RocketMQ PushConsumer Lifecycle Listener
 * Created by aqlu on 2017/9/30.
 */
public interface RocketMQPushConsumerLifecycleListener extends RocketMQConsumerLifecycleListener<DefaultMQPushConsumer> {
}

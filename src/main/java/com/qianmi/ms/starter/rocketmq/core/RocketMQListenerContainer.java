package com.qianmi.ms.starter.rocketmq.core;

import org.springframework.beans.factory.DisposableBean;

/**
 * RocketMQListenerContainer
 * Created by aqlu on 2017/9/28.
 */
public interface RocketMQListenerContainer extends DisposableBean {

    /**
     * Setup the message listener to use. Throws an {@link IllegalArgumentException}
     * if that message listener type is not supported.
     */
    void setupMessageListener(RocketMQListener<?> messageListener);
}

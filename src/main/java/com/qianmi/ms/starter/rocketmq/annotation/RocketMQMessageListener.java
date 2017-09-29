package com.qianmi.ms.starter.rocketmq.annotation;

import com.qianmi.ms.starter.rocketmq.enums.ConsumeMode;
import com.qianmi.ms.starter.rocketmq.enums.SelectorType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.*;

/**
 * RocketMQMessageListener
 * Created by aqlu on 2017/9/27.
 */
@SuppressWarnings("unused")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQMessageListener {

    String consumerGroup();

    String topic();

    SelectorType selectorType() default SelectorType.TAG;

    String selectorExpress() default "*";

    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    MessageModel messageModel() default MessageModel.CLUSTERING;

    int consumeThreadMax() default 64;

}

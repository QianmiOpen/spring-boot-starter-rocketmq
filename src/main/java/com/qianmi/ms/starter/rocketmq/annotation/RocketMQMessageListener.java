/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qianmi.ms.starter.rocketmq.annotation;

import com.qianmi.ms.starter.rocketmq.enums.ConsumeMode;
import com.qianmi.ms.starter.rocketmq.enums.SelectorType;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.filter.ExpressionType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.*;

/**
 * RocketMQMessageListener Created by aqlu on 2017/9/27.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQMessageListener {

    /**
     * Consumers of the same role is required to have exactly same subscriptions and consumerGroup to correctly achieve
     * load balance. It's required and needs to be globally unique.
     * </p>
     * <p>
     * See <a href="http://rocketmq.apache.org/docs/core-concept/">here</a> for further discussion.
     */
    String consumerGroup();

    /**
     * Topic name
     */
    String topic();

    /**
     * Control how to selector message
     *
     * @see ExpressionType
     */
    SelectorType selectorType() default SelectorType.TAG;

    /**
     * Control which message can be select. Grammar please see {@link ExpressionType#TAG} and {@link ExpressionType#SQL92}
     */
    String selectorExpress() default "*";

    /**
     * Control consume mode, you can choice receive message concurrently or orderly
     */
    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    /**
     * Control message mode, if you want all subscribers receive message all message, broadcasting is a good choice.
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * Max consumer thread number
     */
    int consumeThreadMax() default 64;

    /**
     * Flow control threshold on topic level, default value is -1(Unlimited)
     *
     * @see DefaultMQPushConsumer#pullThresholdForTopic
     */
    int pullThresholdForTopic() default -1;

    /**
     * Limit the cached message size on topic level, default value is -1 MiB(Unlimited)
     *
     * @see DefaultMQPushConsumer#pullThresholdSizeForTopic
     */
    int pullThresholdSizeForTopic() default -1;
}

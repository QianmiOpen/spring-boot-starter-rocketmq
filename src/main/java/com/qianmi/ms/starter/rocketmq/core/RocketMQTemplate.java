package com.qianmi.ms.starter.rocketmq.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;


/**
 * Helper class for RocketMQ sending Messages
 * Created by aqlu on 2017/9/28.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public class RocketMQTemplate extends AbstractMessageSendingTemplate<String> implements InitializingBean, DisposableBean {

    @Getter
    @Setter
    private DefaultMQProducer producer;

    @Setter
    @Getter
    private ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    @Setter
    private String charset = "UTF-8";

    @Getter
    @Setter
    private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    /**
     * 同步发送消息
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, producer.getSendMsgTimeout());
    }

    /**
     * 同步发送消息，可指定发送超时时间
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.info("syncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = convertToRocketMsg(destination, message);
            SendResult sendResult = producer.send(rocketMsg, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.info("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.info("syncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 同步发送消息，可指定发送超时时间
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Object payload) {
        return syncSend(destination, payload, producer.getSendMsgTimeout());
    }

    /**
     * 同步发送消息，可指定发送超时时间
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Object payload, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSend(destination, message, timeout);
    }

    /**
     * 同步有序发送消息
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey) {
        return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 同步有序发送消息，可指定发送超时时间
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.info("syncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = convertToRocketMsg(destination, message);
            SendResult sendResult =  producer.send(rocketMsg, messageQueueSelector, hashKey, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.info("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.info("syncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 同步有序发送消息
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey) {
        return syncSendOrderly(destination, payload, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 同步有序发送消息，可指定发送超时时间
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSendOrderly(destination, message, hashKey, producer.getSendMsgTimeout());
    }

    /**
     * 异步发送消息，可指定发送超时时间
     *
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.info("asyncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = convertToRocketMsg(destination, message);
            producer.send(rocketMsg, sendCallback, timeout);
        } catch (Exception e) {
            log.info("asyncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }


    /**
     * 异步发送消息
     *
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param sendCallback {@link SendCallback}
     */
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback) {
        asyncSend(destination, message, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 异步发送消息，可指定发送超时时间
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    public void asyncSend(String destination, Object payload, SendCallback sendCallback, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        asyncSend(destination, message, sendCallback, timeout);
    }

    /**
     * 异步发送消息
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param sendCallback {@link SendCallback}
     */
    public void asyncSend(String destination, Object payload, SendCallback sendCallback) {
        asyncSend(destination, payload, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 异步有序发送消息，可指定发送超时时间
     *
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param hashKey      use this key to select queue. for example: orderId, productId ...
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.info("asyncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = convertToRocketMsg(destination, message);
            producer.send(rocketMsg, messageQueueSelector, hashKey, sendCallback, timeout);
        } catch (Exception e) {
            log.info("asyncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 异步有序发送消息
     *
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param hashKey      use this key to select queue. for example: orderId, productId ...
     * @param sendCallback {@link SendCallback}
     */
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, message, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 异步有序发送消息
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param hashKey      use this key to select queue. for example: orderId, productId ...
     * @param sendCallback {@link SendCallback}
     */
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback) {
        asyncSendOrderly(destination, payload, hashKey, sendCallback, producer.getSendMsgTimeout());
    }

    /**
     * 异步有序发送消息，可指定发送超时时间
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param hashKey      use this key to select queue. for example: orderId, productId ...
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        asyncSendOrderly(destination, message, hashKey, sendCallback, timeout);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(producer, "Property 'producer' is required");
        producer.start();
    }

    protected void doSend(String destination, Message<?> message) {
        SendResult sendResult = syncSend(destination, message);
        log.debug("send message to `{}` finished. result:{}", destination, sendResult);
    }

    /**
     * Convert spring message to rocketMQ message
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @return instance of {@link org.apache.rocketmq.common.message.Message}
     */
    private org.apache.rocketmq.common.message.Message convertToRocketMsg(String destination, Message<?> message) {
        Object payloadObj = message.getPayload();
        byte[] payloads;

        if (payloadObj instanceof String) {
            payloads = ((String) payloadObj).getBytes(Charset.forName(charset));
        } else {
            try {
                String jsonObj = this.objectMapper.writeValueAsString(payloadObj);
                payloads = jsonObj.getBytes(Charset.forName(charset));
            } catch (Exception e) {
                throw new RuntimeException("convert to RocketMQ message failed.", e);
            }
        }

        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = "";
        if (tempArr.length > 1) {
            tags = tempArr[1];
        }

        org.apache.rocketmq.common.message.Message rocketMsg = new org.apache.rocketmq.common.message.Message(topic, tags, payloads);

        MessageHeaders headers = message.getHeaders();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            Object keys = headers.get(MessageConst.PROPERTY_KEYS);
            if (!StringUtils.isEmpty(keys)) { // if headers has 'KEYS', set rocketMQ message key
                rocketMsg.setKeys(keys.toString());
            }

            // set rocketMQ message flag
            Object flagObj = headers.getOrDefault("FLAG", "0");
            int flag = 0;
            try {
                flag = Integer.parseInt(flagObj.toString());
            } catch (NumberFormatException e) {
                // ignore
                log.info("flag must be integer, flagObj:{}", flagObj);
            }
            rocketMsg.setFlag(flag);

            // set rocketMQ message waitStoreMsgOkObj
            Object waitStoreMsgOkObj = headers.getOrDefault("WAIT_STORE_MSG_OK", "true");
            boolean waitStoreMsgOK = Boolean.TRUE.equals(waitStoreMsgOkObj);
            rocketMsg.setWaitStoreMsgOK(waitStoreMsgOK);

            headers.entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), MessageConst.PROPERTY_KEYS)
                            && !Objects.equals(entry.getKey(), "FLAG")
                            && !Objects.equals(entry.getKey(), "WAIT_STORE_MSG_OK")) // exclude "KEYS", "FLAG", "WAIT_STORE_MSG_OK"
                    .forEach(entry -> {
                        rocketMsg.putUserProperty("USERS_" + entry.getKey(), String.valueOf(entry.getValue())); // add other properties with prefix "USERS_"
                    });

        }

        return rocketMsg;
    }

    @Override
    protected Message<?> doConvert(Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) {
        String content;
        if (payload instanceof String) {
            content = (String) payload;
        } else {
            // if payload not as string, use objectMapper change it.
            try {
                content = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                log.info("convert to payload to String failed. payload:{}", payload);
                throw new RuntimeException("convert to payload to String failed.", e);
            }
        }

        MessageBuilder<?> builder = MessageBuilder.withPayload(content);
        if (headers != null) {
            builder.copyHeaders(headers);
        }
        builder.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);

        Message<?> message = builder.build();
        if (postProcessor != null) {
            message = postProcessor.postProcessMessage(message);
        }
        return message;
    }

    @Override
    public void destroy() throws Exception {
        if(Objects.nonNull(producer)) {
            producer.shutdown();
        }
    }
}

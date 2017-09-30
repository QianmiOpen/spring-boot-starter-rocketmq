package com.qianmi.ms.starter.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQProperties
 * Created by aqlu on 2017/9/27.
 */
@SuppressWarnings("WeakerAccess")
@ConfigurationProperties(prefix = "spring.rocketmq")
@Data
public class RocketMQProperties {

    /**
     * name server for rocketMQ, formats: `host:port;host:port`
     */
    private String nameServer;

    private Producer producer;

    @Data
    public static class Producer {

        /**
         * name of producer
         */
        private String group;

        /**
         * millis of send message timeout
         */
        private int sendMsgTimeout = 3000;


        /**
         * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
         */
        private int compressMsgBodyOverHowmuch = 1024 * 4;

        /**
         * <p>
         * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
         * </p>
         * This may potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * <p>
         * Maximum number of retry to perform internally before claiming sending failure in asynchronous mode.
         * </p>
         * This may potentially cause message duplication which is up to application developers to resolve.
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * Indicate whether to retry another broker on sending failure internally.
         */
        private boolean retryAnotherBrokerWhenNotStoreOk = false;

        /**
         * Maximum allowed message size in bytes.
         */
        private int maxMessageSize = 1024 * 1024 * 4; // 4M

    }
}

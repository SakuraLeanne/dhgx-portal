package com.dhgx.portal.stream.core;

import java.util.Map;

/**
 * 简单的消息处理接口，各微服务可通过 Lambda 直接实现。
 */
@FunctionalInterface
public interface MessageHandler {

    /**
     * 处理消费到的消息。
     *
     * @param messageId Redis Stream 生成的消息 ID
     * @param body      消息体，使用字段键值对承载业务数据
     */
    void onMessage(String messageId, Map<String, String> body);
}

package com.dhgx.portal.stream.core;

import com.dhgx.portal.stream.config.MessageStreamProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 封装 Redis Stream 的消息发布能力。
 */
@RequiredArgsConstructor
public class MessagePublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final MessageStreamProperties properties;

    /**
     * 发布单字段消息，常用于简单事件通知。
     *
     * @param fieldName 字段名，例如 "event"
     * @param body      字段值，例如 "user.created"
     * @return Redis 生成的 RecordId
     */
    public RecordId publish(String fieldName, String body) {
        return publish(Collections.singletonMap(fieldName, body));
    }

    /**
     * 发布多字段消息，便于携带更丰富的业务数据。
     *
     * @param message 消息字段集合
     * @return Redis 生成的 RecordId
     */
    public RecordId publish(Map<String, String> message) {
        if (CollectionUtils.isEmpty(message)) {
            throw new IllegalArgumentException("message payload must not be empty");
        }
        MapRecord<String, String, String> record = StreamRecords.mapBacked(message)
                .withStreamKey(properties.getStreamKey());
        return stringRedisTemplate.opsForStream().add(record);
    }
}

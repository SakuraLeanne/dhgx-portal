package com.dhgx.portal.stream.config;

import com.dhgx.portal.stream.core.MessagePublisher;
import com.dhgx.portal.stream.core.MessageSubscriber;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import java.time.Duration;

/**
 * 自动装配 Redis Stream 相关的通用组件。
 */
@Configuration
@EnableConfigurationProperties(MessageStreamProperties.class)
public class MessageStreamAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        LettuceClientConfigurationBuilder clientConfig = LettuceClientConfiguration.builder();
        if (redisProperties.getTimeout() != null) {
            clientConfig.commandTimeout(redisProperties.getTimeout());
        }

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisProperties.getHost());
        serverConfig.setPort(redisProperties.getPort());
        serverConfig.setDatabase(redisProperties.getDatabase());
        String password = redisProperties.getPassword();
        if (password != null && !password.isEmpty()) {
            serverConfig.setPassword(RedisPassword.of(password));
        }

        return new LettuceConnectionFactory(serverConfig, clientConfig.build());
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean(destroyMethod = "stop")
    @ConditionalOnMissingBean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            StringRedisTemplate stringRedisTemplate) {
        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.<String, MapRecord<String, String, String>>builder()
                        .batchSize(10)
                        .pollTimeout(Duration.ofSeconds(2))
                        .targetType(MapRecord.class)
                        .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(stringRedisTemplate.getRequiredConnectionFactory(), options);
        container.start();
        return container;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessagePublisher messagePublisher(StringRedisTemplate stringRedisTemplate, MessageStreamProperties properties) {
        return new MessagePublisher(stringRedisTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageSubscriber messageSubscriber(StringRedisTemplate stringRedisTemplate,
                                               StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer,
                                               MessageStreamProperties properties) {
        return new MessageSubscriber(stringRedisTemplate, streamMessageListenerContainer, properties);
    }
}

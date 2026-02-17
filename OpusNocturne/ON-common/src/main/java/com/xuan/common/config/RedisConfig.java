package com.xuan.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * <p>
 * 作用：
 * 1. 自定义RedisTemplate配置，替代Spring Boot默认配置
 * 2. 优化序列化方式，使用Jackson2JsonRedisSerializer替代默认的JDK序列化
 * 3. 增强序列化功能，支持复杂对象和Java 8时间类型
 * 4. 按照Spring Boot 3.4.2配置规范进行自动配置管理
 * <p>
 * 序列化配置：
 * - key和hash key使用StringRedisSerializer（字符串格式）
 * - value和hash value使用Jackson2JsonRedisSerializer（JSON格式）
 * - 支持Java 8时间类型（LocalDateTime等）的序列化
 * - 支持多态类型处理和复杂对象序列化
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用更现代的方式配置序列化
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

        // 直接在构造函数中传入ObjectMapper
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 设置 Java8 时间类型（如 LocalDateTime）
        objectMapper.registerModule(new JavaTimeModule());

        // 设置 key 和 value 的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);

        // 设置 hash 的 key 和 value 的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

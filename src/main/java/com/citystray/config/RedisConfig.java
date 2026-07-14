package com.citystray.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 自定义 RedisTemplate 的序列化方式，
 * Key 使用字符串序列化，Value 使用 JSON 序列化，
 * 避免 Redis 中存储乱码数据。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate 序列化方式
     *
     * @param factory Redis 连接工厂
     * @return RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 使用字符串序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // Value 使用 JSON 序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash 结构的 Key 和 Value 序列化配置
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}

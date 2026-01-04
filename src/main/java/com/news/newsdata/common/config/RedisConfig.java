package com.news.newsdata.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key serializer 설정 (StringRedisSerializer 사용)
        template.setKeySerializer(new StringRedisSerializer());

        // Value serializer 설정 (GenericJackson2JsonRedisSerializer 사용)
        template.setValueSerializer(RedisSerializer.json());

        // Hash key serializer 설정 (StringRedisSerializer 사용)
        //template.setHashKeySerializer(new StringRedisSerializer());

        // Hash value serializer 설정 (GenericJackson2JsonRedisSerializer 사용)
        //template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}

package com.meryem.fashionstore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.meryem.fashionstore.dto.CategoryResponse;
import com.meryem.fashionstore.dto.ProductResponse;
import com.meryem.fashionstore.dto.StoreResponse;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {


    private RedisCacheConfiguration createConfig(
            Jackson2JsonRedisSerializer<?> serializer)
    {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<StoreResponse> storeSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, StoreResponse.class);

        RedisCacheConfiguration storeConfig = createConfig(storeSerializer);

        Jackson2JsonRedisSerializer<CategoryResponse> categorySerializer = new Jackson2JsonRedisSerializer<>(objectMapper, CategoryResponse.class);

        RedisCacheConfiguration categoryConfig = createConfig(categorySerializer);

        Jackson2JsonRedisSerializer<ProductResponse> productSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, ProductResponse.class);

        RedisCacheConfiguration productConfig = createConfig(productSerializer);

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("stores", storeConfig)

                .withCacheConfiguration("storesByUser", storeConfig)

                .withCacheConfiguration("categories", categoryConfig)

                .withCacheConfiguration("products", productConfig).build();}

    @Bean
    public RedisTemplate<String, StoreResponse> storeRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        ObjectMapper objectMapper =
                new ObjectMapper();

        objectMapper.registerModule(
                new JavaTimeModule()
        );

        Jackson2JsonRedisSerializer<StoreResponse>
                serializer =
                new Jackson2JsonRedisSerializer<>(
                        objectMapper,
                        StoreResponse.class);

        RedisTemplate<String, StoreResponse> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;}

    @Bean
    public RedisTemplate<String, CategoryResponse> categoryRedisTemplate(RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<CategoryResponse>
                serializer = new Jackson2JsonRedisSerializer<>(objectMapper, CategoryResponse.class);

        RedisTemplate<String, CategoryResponse> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;}


    @Bean
    public RedisTemplate<String, ProductResponse> productRedisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonRedisSerializer<ProductResponse> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ProductResponse.class);

        RedisTemplate<String, ProductResponse> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());

        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;}

}


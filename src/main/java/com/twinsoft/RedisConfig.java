/**
 *
 */
package com.twinsoft;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twinsoft.domain.Hotel;

/**
 * The Class RedisConfig.
 *
 * @author Miodrag Pavkovic
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	@Value("${spring.redis.host}")
    private String host;
	@Value("${spring.redis.port}")
    private int port;

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(host);
		jedisConnectionFactory.setPort(port);
		jedisConnectionFactory.setUsePool(true);
		return jedisConnectionFactory;
	}

	/**
	 * Here StringRedisSerializer is used for serializing the key and GenericJackson2JsonRedisSerializer for the value.
	 * This template is a generified one, it can be wired to multiple components and reused as it's thread-safe.
	 * 
	 * @return StringRedisSerializer
	 */
	@Bean
	public StringRedisSerializer stringRedisSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisJsonSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}


	/**
	 * Redis template used for handling hotel
	 *
	 * @return the redis template
	 */
	@Bean(name = "hotelRedisTemplate")
	public RedisTemplate<String, Hotel> hotelRedisTemplate() {
		RedisTemplate<String, Hotel> redisTemplate = new RedisTemplate<String, Hotel>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setExposeConnection(true);
		redisTemplate.setKeySerializer(stringRedisSerializer());
		final Jackson2JsonRedisSerializer<Hotel> serializer = new Jackson2JsonRedisSerializer<>(Hotel.class);
		serializer.setObjectMapper(redisObjectMapper());
		redisTemplate.setValueSerializer(serializer);
		return redisTemplate;
	}

	/**
	 * Redis object mapper.
	 *
	 * @return the object mapper
	 */
	@Bean(name = "redisObjectMapper")
	public ObjectMapper redisObjectMapper() {
		final ObjectMapper redisObjectMapper = new ObjectMapper();
		redisObjectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
		redisObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		redisObjectMapper.registerModule(new JavaTimeModule());
		redisObjectMapper.setDateFormat(new SimpleDateFormat(TIMESTAMP_FORMAT));
		return redisObjectMapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cache.annotation.CachingConfigurerSupport#cacheManager()
	 */
	@Bean
	public RedisCacheManager cacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager(hotelRedisTemplate());
		redisCacheManager.setTransactionAware(true);
		redisCacheManager.setLoadRemoteCachesOnStartup(true);
		redisCacheManager.setUsePrefix(true);
		return redisCacheManager;
	}

	/* (non-Javadoc)
	 * @see org.springframework.cache.annotation.CachingConfigurerSupport#keyGenerator()
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			
			@Override
			public Object generate(Object o, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName());
				sb.append(method.getName());
				for (Object param : params) {
					sb.append(param.toString());
				}
				return sb.toString();
			}
		};
	}
}

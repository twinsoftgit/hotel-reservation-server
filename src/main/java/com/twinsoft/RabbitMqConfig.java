package com.twinsoft;

import java.text.SimpleDateFormat;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 * RabbitMQ configuration class
 *
 * @author Miodrag Pavkovic
 */
@Configuration
public class RabbitMqConfig {
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	@Value("${hotelserver.amqp.exchange}")
    private String exchange;

    @Value("${hotelserver.amqp.hotel-queue}")
    private String hotelQueue;
    
    @Value("${hotelserver.amqp.hotel-routing-key}")
    private String hotelRequestRoutingKey;
    
    @Value("${hotelserver.amqp.hotel-reservation-queue}")
    private String reservationQueue;
    
    @Value("${hotelserver.amqp.hotel-reservation-routing-key}")
    private String hotelReservationRequestRoutingKey;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean(name = "hotelEventQueue")
    public Queue hotelEventQueue() {
        return new Queue(hotelQueue);
    }

    @Bean(name = "hotelRequestEventBinding")
    public Binding hotelRequestEventBinding() {
        return BindingBuilder.bind(hotelEventQueue())
                .to(topicExchange())
                .with(hotelRequestRoutingKey);
    }
    
    @Bean(name = "reservationEventQueue")
    public Queue reservationEventQueue() {
        return new Queue(reservationQueue);
    }

    @Bean(name = "reservationRequestEventBinding")
    public Binding reservationRequestEventBinding() {
        return BindingBuilder.bind(reservationEventQueue())
                .to(topicExchange())
                .with(hotelReservationRequestRoutingKey);
    }

    @Bean(name = "eventMessageConverter")
    public MessageConverter messageConverter() {
        final Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // Jackson deserialization point issue
        final ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonObjectMapper.registerModule(new JavaTimeModule());
        jsonObjectMapper.setDateFormat(new SimpleDateFormat(TIMESTAMP_FORMAT));
        converter.setJsonObjectMapper(jsonObjectMapper);
        return converter;
    }
}

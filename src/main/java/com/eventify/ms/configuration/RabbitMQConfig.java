package com.eventify.ms.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_PROCESSED_QUEUE = "payment-processed";

    @Bean
    public ConnectionFactory connectionFactory() {
        var factory = new CachingConnectionFactory("amqps://gmahkjqj:Y8wni_7M6HwbEuUVJKdf7WTeEQVJ9ms-@ostrich.lmq.cloudamqp.com/gmahkjqj"); // change to your RabbitMQ host
        factory.setUsername("gmahkjqj");
        factory.setPassword("Y8wni_7M6HwbEuUVJKdf7WTeEQVJ9ms-");
        factory.setUri("amqps://gmahkjqj:Y8wni_7M6HwbEuUVJKdf7WTeEQVJ9ms-@ostrich.lmq.cloudamqp.com/gmahkjqj");
        factory.setPort(5671);
        return factory;
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return new Queue(PAYMENT_PROCESSED_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}

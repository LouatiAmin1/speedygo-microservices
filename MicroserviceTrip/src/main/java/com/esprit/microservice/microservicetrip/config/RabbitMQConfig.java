package com.esprit.microservice.microservicetrip.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "trip_notifications_exchange";
    public static final String ROUTING_KEY = "trip.notification";
    public static final String QUEUE_NAME = "trip_notification_queue";

    @Bean
    public TopicExchange tripExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue tripQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding tripBinding(Queue tripQueue, TopicExchange tripExchange) {
        return BindingBuilder.bind(tripQueue).to(tripExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

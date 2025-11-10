package com.esprit.microservice.micronotification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTripConfig {

    public static final String TRIP_EXCHANGE_NAME = "trip_notifications_exchange";
    public static final String TRIP_ROUTING_KEY = "trip.notification";
    public static final String TRIP_QUEUE_NAME = "trip_notification_queue";

    @Bean
    public Queue tripNotificationQueue() {
        return new Queue(TRIP_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange tripExchange() {
        return new TopicExchange(TRIP_EXCHANGE_NAME);
    }

    @Bean
    public Binding tripBinding(Queue tripNotificationQueue, TopicExchange tripExchange) {
        return BindingBuilder.bind(tripNotificationQueue).to(tripExchange).with(TRIP_ROUTING_KEY);
    }
}

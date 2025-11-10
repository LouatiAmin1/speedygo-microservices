package com.esprit.microservice.micronotification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Même noms que dans MicroParcel
    public static final String EXCHANGE_NAME = "parcel_notifications_exchange";
    public static final String ROUTING_KEY = "parcel.notification";
    public static final String QUEUE_NAME = "notification_queue";

    // Déclarer la queue
    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // Déclarer l’exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Relier queue ↔ exchange ↔ routing key
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY);
    }

    // Converter JSON ↔ POJO
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate avec conversion JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

package com.hospital.billing.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "hospital.exchange";
    public static final String BILLING_QUEUE = "billing_queue";
    public static final String ROUTING_KEY = "appointment.created";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue billingQueue() {
        return new Queue(BILLING_QUEUE, true); // durable = true
    }

    @Bean
    public Binding binding(Queue billingQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(billingQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    /**
     * Jackson message converter for JSON serialization/deserialization
     * This is required when receiving messages from non-Spring AMQP clients
     * (like the raw RabbitMQ Java client)
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate configured with Jackson converter
     * This ensures both sending and receiving use JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
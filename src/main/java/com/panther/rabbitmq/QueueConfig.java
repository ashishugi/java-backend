package com.panther.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class QueueConfig {
    @Value("${queue.name}")
    private String message;

    @Bean("queue-without-routingKey")
    public Queue sentMessage() {
        return new Queue(message, true);
    }
}

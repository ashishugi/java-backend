package com.amigoscode.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class QueueConsumer {

    private QueueService service;
    private RabbitTemplate rabbitTemplate;

    public QueueConsumer(QueueService service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload QueueMessage payload) {
        service.addMessage(payload);
    }
}

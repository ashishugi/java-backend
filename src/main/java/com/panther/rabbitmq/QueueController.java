package com.panther.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("api/v1/queue")
public class QueueController {
    private QueueProducer producer;

    private final RabbitTemplate producerWithRoutingKey;

    private static final Random RANDOM = new Random();
    private final QueueService service;

    public QueueController(QueueProducer producer, RabbitTemplate producerWithRoutingKey, QueueService service) {
        this.producer = producer;
        this.producerWithRoutingKey = producerWithRoutingKey;
        this.service = service;
    }

    @GetMapping("/old_queue_Mapping")
    public ResponseEntity<String> send() {
        producer.sendMessage("Testing test using testContainers");
        return ResponseEntity.status(HttpStatus.OK).body("Successfully sent a message");
    }

    @GetMapping("/send")
    public ResponseEntity<String> sendMessage() {
        String title = "Testing RabbitMq";
        String description = "Testing with routing Keys";

        SendMessage message = new SendMessage(title, description);

        producerWithRoutingKey
                .convertAndSend("testContainerExchange", "routing-key", message);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully sent message using producerWithRoutingKey");
    }

    @GetMapping("{id}")
    public Optional<QueueMessage> getMessageById(@PathVariable("id") Integer id) {
        return service.getMessageById(id);
    }

    @GetMapping
    public List<QueueMessage> getAllMessage() {
        return service.getAllMessage();
    }

    @DeleteMapping("{id}")
    public void deleteMessageById(@PathVariable("id") Integer id) {
        service.deleteMessageById(id);
    }

    @DeleteMapping
    public void deleteAllMessages() {
        service.deleteAllMessages();
    }
}

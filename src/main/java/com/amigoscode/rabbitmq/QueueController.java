package com.amigoscode.rabbitmq;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/queue")
public class QueueController {
    private QueueProducer producer;

    public QueueController(QueueProducer producer) {
        this.producer = producer;
    }

    @GetMapping
    public ResponseEntity<String> send() {
        producer.sendMessage("Testing test using testContainers");
        return ResponseEntity.status(HttpStatus.OK).body("Successfully sent a message");
    }

}

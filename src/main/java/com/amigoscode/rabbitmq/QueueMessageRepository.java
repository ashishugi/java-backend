package com.amigoscode.rabbitmq;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueMessageRepository extends JpaRepository<QueueMessage, Integer> {
}

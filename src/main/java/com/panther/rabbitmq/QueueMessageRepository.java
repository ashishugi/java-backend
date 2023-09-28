package com.panther.rabbitmq;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueMessageRepository extends JpaRepository<QueueMessage, Integer> {
}

package com.amigoscode.rabbitmq;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QueueService {
    private QueueMessageRepository queueMessageRepository;

    public QueueService(QueueMessageRepository queueMessageRepository) {
        this.queueMessageRepository = queueMessageRepository;
    }

    public void addMessage(QueueMessage message) {
        queueMessageRepository.save(message);
    }

    public Optional<QueueMessage> getMessageById(int id) {
        return Optional.ofNullable(queueMessageRepository.findById(id).orElse(null));
    }

    public List<QueueMessage> getAllMessage() {
        return queueMessageRepository.findAll();
    }

    public void deleteMessageById(int id) {
        queueMessageRepository.deleteById(id);
    }

    public void deleteAllMessages() {
        queueMessageRepository.deleteAll();
    }
}

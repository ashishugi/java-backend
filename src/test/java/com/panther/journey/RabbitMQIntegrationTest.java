package com.panther.journey;

import com.panther.AbstractTestcontainers;
import com.panther.RabbitMQTestContainer;
import com.panther.rabbitmq.QueueMessage;
import com.panther.rabbitmq.SendMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RabbitMQIntegrationTest extends AbstractTestcontainers implements RabbitMQTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    private static final String QUEUE_URI = "/api/v1/queue";
    private final RabbitTemplate producerWithRoutingKey;
    @Autowired
    public RabbitMQIntegrationTest(RabbitTemplate producerWithRoutingKey) {
        this.producerWithRoutingKey = producerWithRoutingKey;
    }

    @BeforeEach
    public void setUp() {
        // increasing timeout - as getting error of timeout - IllegalState Timeout on blocking read for 5000000000 NANOSECONDS
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(50000))
                .build();
    }

    @Test
    void pushMessageToRabbitWhenHitEndPoint() {
//    select * from test_container_queue;

        webTestClient.get()
                .uri(QUEUE_URI + "/send")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        List<QueueMessage> messages = webTestClient.get()
                .uri(QUEUE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<QueueMessage>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(messages).isNotEmpty();
        assertThat(messages.size()).isEqualTo(1);
    }

    @Test
    void shouldGiveMessageWhenStoredMessageInDBThroughRabbitMQ() {
        String title = "Sending Message To Queue";
        String description = "We are testing the testContainer for rabbitMQ";

        SendMessage message = new SendMessage(title, description);

        producerWithRoutingKey
                .convertAndSend("testContainerExchange", "routing-key", message);

        // added await as - RabbitMq - Asynchronous process. refer : https://stackoverflow.com/questions/77166940/getting-null-from-db-while-running-integration-test-using-testcontainer
        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> {
                    List<QueueMessage> listMessages = webTestClient.get()
                            .uri(QUEUE_URI)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .expectBodyList(new ParameterizedTypeReference<QueueMessage>() {
                            })
                            .returnResult()
                            .getResponseBody();
                    return listMessages.stream()
                            .anyMatch(m -> m.getTitle().equals(message.title()));
                });

        List<QueueMessage> listMessages = webTestClient.get()
                .uri(QUEUE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<QueueMessage>() {
                })
                .returnResult()
                .getResponseBody();

        QueueMessage actualMessage = listMessages.stream()
                        .filter(m -> m.getTitle().equals(message.title()))
                        .findFirst()
                        .orElseThrow();

        assertThat(actualMessage.getTitle()).isEqualTo(message.title());
        assertThat(actualMessage.getDescription()).isEqualTo(message.description());
    }
}

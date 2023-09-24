package com.amigoscode.journey;

import com.amigoscode.AbstractTestcontainers;
import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest extends AbstractTestcontainers {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @BeforeEach
    public void setUp() {
        // increasing timeout - as getting error of timeout - IllegalState Timeout on blocking read for 5000000000 NANOSECONDS
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofMillis(60000))
                .build();
    }

    @Test
    void canRegisterACustomer() {

        //1. create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@yahoo.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );
        assertThat(allCustomer).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        // get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteCustomer() {
        //1. create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@yahoo.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        //5. delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        //6. check if delete customer still exists or not
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        //1. create a registration request
        String name = "Aditya";
        String email = "aditya1" + "@yahoo.com";
        int age = 19;

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        //5. update customer
        String updatedName = "Abhishek";
        String updatedEmail = "abhishek1@yahoo.com";
        int updatedAge = 26;
        CustomerUpdateRequest customerUpdateRequest =
                new CustomerUpdateRequest(updatedName, updatedEmail, updatedAge);

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        //6. check the updated customer
        Customer actualCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        Customer expectedCustomer = new Customer(
                id, updatedName, updatedEmail, updatedAge
        );

        assertThat(actualCustomer).isEqualTo(expectedCustomer);
    }

    @Test
    void canUpdateCustomerEmail() {
        //1. create a registration request
        String name = "Aditya";
        String email = "aditya1" + "@yahoo.com";
        int age = 19;

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        //5. update customer
        String updatedEmail = "yash@taoo.com";
        CustomerUpdateRequest customerUpdateRequest =
                new CustomerUpdateRequest(null, updatedEmail, null);

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        //6. check the updated customer
        Customer actualCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        Customer expectedCustomer = new Customer(
                id, name, updatedEmail, age
        );

        assertThat(actualCustomer.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(actualCustomer.getAge()).isEqualTo(expectedCustomer.getAge());
        assertThat(actualCustomer.getName()).isEqualTo(expectedCustomer.getName());
    }
}

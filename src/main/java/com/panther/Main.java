package com.panther;

import com.panther.customer.Customer;
import com.panther.customer.CustomerRepository;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

//        SpringApplication.run(Main.class, args);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class);
//        printBean(applicationContext);
    }

    //command line
    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Name name = faker.name();
            Random random = new Random();
            String firstName = name.firstName();
            String lastName = name.lastName();

            Customer customer = new Customer(firstName+ " " + lastName, firstName.toLowerCase() + "."+ lastName.toLowerCase() + "@example.com", random.nextInt(16, 99)); // [16, 99] close/inner bound

//            customerRepository.save(customer);
        };
    }


    @Bean("rename_foo")
    public Foo getFoo() {
        return new Foo("bar");
    }
    public record Foo(String name) {}

    private static void printBean(ConfigurableApplicationContext ctx) {
        String[] beanDefinationNames = ctx.getBeanDefinitionNames();
        for(String beanDefinationName: beanDefinationNames) {
            System.out.println(beanDefinationName);
        }
    }
}

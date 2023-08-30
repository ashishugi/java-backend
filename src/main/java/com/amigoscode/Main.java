package com.amigoscode;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

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
            Customer alex = new Customer("Harsh", "harsh@gmail.com", 234);
            Customer jas = new Customer("Aditya", "Aditya@gmail.com", 23);
            List<Customer> customers = List.of(alex, jas);

            customerRepository.saveAll(customers);
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

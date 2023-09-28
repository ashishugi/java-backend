package com.panther.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); // this initialises the Mock library for the use of @Mocks and its annotations.
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();// this will destroy the mockito after each test. hence we will get new Mocks before each tests.
    }

    @Test
    void selectAllCustomer() {
        //Given

        //When
        underTest.selectAllCustomer();

        //Then
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // Given
        int id = 1;

        //When
        underTest.selectCustomerById(id);

        //Then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer("Aditya", "aditya@email.com", 23);

        //When
        underTest.insertCustomer(customer);

        //Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        Faker FAKER = new Faker();
        //Given
        String email = "sampleEmail@outlook.com";

        //When
        underTest.existsPersonWithEmail(email);

        //Then
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existPersonWithId() {
        //Given
        int id = 1;

        //When
        underTest.existPersonWithId(id);

        //Then
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;

        //When
        underTest.deleteCustomerById(id);

        //Then
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomerById() {
        //Given
        Customer customer = new Customer();

        //When
        underTest.updateCustomerById(customer);

        //Then
        Mockito.verify(customerRepository).save(customer);
    }
}
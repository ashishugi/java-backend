package com.panther.customer;

import com.panther.exception.DuplicateResourceException;
import com.panther.exception.RequestValidationException;
import com.panther.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //Given - not required here

        //When
        underTest.getAllCustomers();

        //Then
        Mockito.verify(customerDao).selectAllCustomer();
    }

    @Test
    void getAllCustomerById() {
        //Given
        int id = 1;
        Customer customer = new Customer(id,"Aditya", "aditya@yahoo.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getAllCustomerById(id);

        //Then
        verify(customerDao).selectCustomerById(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowExceptionWhenCustomerIdDoNotExist() {
        //Given
        int id = -1;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getAllCustomerById(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "aditya@yahoo.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest("Aditya", email, 23);

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        ); // maps the value from CustomerRegistrationRequest to Customer

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionWhenAddingCustomerWithExistingEmail() {
        //Given
        String email = "aditya@yahoo.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest("Aditya", email, 23);

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;

        when(customerDao.existPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowExceptionWhenCustomerIdDoesNotExistForDeletion() {
        //Given
        int id = 1;

        when(customerDao.existPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Id does not exist");

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomerById() {
        //Given
        int id = 1;
        String newEmail = "aditya@yahoo.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Aditya", newEmail, 26
        );

        Customer customer = new Customer(
                1, "Abhishek", "abhi@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(request.name());
        assertThat(caputredCustomer.getEmail()).isEqualTo(request.email());
        assertThat(caputredCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void updateOnlyCustomerName() {
        //Given
        int id = 1;
        String name = "Shekhar";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name, null, null
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(request.name());
        assertThat(caputredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(caputredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerEmail() {
        //Given
        int id = 1;
        String email = "shekhar@yahoo.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, email, null
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(caputredCustomer.getEmail()).isEqualTo(request.email());
        assertThat(caputredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerAge() {
        //Given
        int id = 1;
        int age = 19;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, null, age
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(caputredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(caputredCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionEmailAlreadyTakenWhenUpdatingExistingEmail() {
        //Given
        int id = 1;
        String email = "aditya@yahoo.com";

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Aditya", email, 26
        );

        Customer customer = new Customer(
                1, "Abhishek", "abhi@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).updateCustomerById(customer);
    }

    @Test
    void willThrowExceptionNoDataChangesWhenUpdatingCustomerDetails() {
        //Given
        int id = 1;
        String email = "aditya@yahoo.com";
        String name = "aditya";
        int age = 25;

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name, email, age
        );

        Customer customer = new Customer(
                1, name, email, age
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes are found");

        //Then
        verify(customerDao, never()).updateCustomerById(customer);
    }
}
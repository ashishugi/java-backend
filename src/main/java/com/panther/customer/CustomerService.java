package com.panther.customer;

import com.panther.exception.DuplicateResourceException;
import com.panther.exception.RequestValidationException;
import com.panther.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomer();
    }
    public Customer getAllCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFound("customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        //check if email exist
        if(customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("email already taken");
        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age());

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        if(!customerDao.existPersonWithId(id)) {
            throw new ResourceNotFound("Id does not exist");
        }
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomerById(Integer id, CustomerUpdateRequest updateRequest) {
        Customer customer = getAllCustomerById(id);

        boolean isChanged = false;

        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            isChanged = true;
        }

        if(updateRequest.age() != null && updateRequest.age() != customer.getAge()) {
            customer.setAge(updateRequest.age());
            isChanged = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if(customerDao.existsPersonWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(updateRequest.email());
            isChanged = true;
        }
        if(!isChanged) {
            throw new RequestValidationException("No data changes are found");
        }
        customerDao.updateCustomerById(customer);
    }
}

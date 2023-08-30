package com.amigoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customers;
    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(1, "Alex", "a@gmail.com", 234);
        customers.add(alex);
        Customer jas = new Customer(2,  "Jasmine", "b@gmail.com", 23);
        customers.add(jas);
    }
    @Override
    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }


}

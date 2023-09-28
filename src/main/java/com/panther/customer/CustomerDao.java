package com.panther.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomer();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    boolean existPersonWithId(Integer id);
    void deleteCustomerById(Integer id);
    void updateCustomerById(Customer customer);
}

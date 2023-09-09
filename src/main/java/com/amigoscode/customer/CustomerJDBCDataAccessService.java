package com.amigoscode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{
    private final JdbcTemplate jdbcTemplate; // this is similar to CustomerRepository that we used in CustomerJPADataAccessService.java
    private final CustomerRowMapper customerRowMapper;
    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer 
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst(); // out of list of customers we are finding the first row and returning.
//      OR
//      return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[] { id }, customerRowMapper));
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                  INSERT INTO customer(name, email, age) 
                  VALUES (?, ?, ?)
                  """;
        int numberOfRowEffected = jdbcTemplate.update(sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()); // update() can be used to insert update and delete operations, it returns number of row effected

        System.out.println("jdbcTemplate.update() : " + numberOfRowEffected);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id) 
                FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE 
                FROM customer
                WHERE id = ?
                """;

        int result = jdbcTemplate.update(sql, id);
        System.out.println("deleteCustomerWithId result: " + result);
    }

    @Override
    public void updateCustomerById(Customer customer) {
        if(customer.getAge() != null) {
            var sql = """
                UPDATE customer
                SET age = ? 
                WHERE id = ?
                """;

            int result = jdbcTemplate.update(sql, customer.getAge(), customer.getId());
            System.out.println("update customer age result: " + result);
        }

        if(customer.getEmail() != null) {
            var sql = """
                UPDATE customer
                SET email = ? 
                WHERE id = ?
                """;

            int result = jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
            System.out.println("update customer email result: " + result);
        }

        if(customer.getName() != null) {
            var sql = """
                UPDATE customer
                SET name = ? 
                WHERE id = ?
                """;

            int result = jdbcTemplate.update(sql, customer.getName(), customer.getId());
            System.out.println("update customer name result: " + result);
        }
    }
}

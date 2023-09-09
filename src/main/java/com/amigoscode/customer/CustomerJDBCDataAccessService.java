package com.amigoscode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{
    private final JdbcTemplate jdbcTemplate; // this is similar to CustomerRepository that we used in CustomerJPADataAccessService.java

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                """;

        RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
            Customer customer = new Customer(
                    rs.getInt("id"), // table column name
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("age")
            );
            return customer;
        }; // this row mapper helps to map the row we get to java object. So after we get a row, it maps to a java object
        // rs -> it is a pointer which points to a row of data in the table and data is mapped to its java object with column name given.
        // rowNum -> It means the current row we are working on, after the current row is mapped, this rowNum is moved to next row and so on.

        List<Customer> customers =  jdbcTemplate.query(sql, customerRowMapper); // sql, row mapper =

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return Optional.empty();
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
        return false;
    }

    @Override
    public boolean existPersonWithId(Integer id) {
        return false;
    }

    @Override
    public void deleteCustomerById(Integer id) {

    }

    @Override
    public void updateCustomerById(Customer customer) {

    }
}

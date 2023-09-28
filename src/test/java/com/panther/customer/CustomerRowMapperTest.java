package com.panther.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper=  new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Aditya");
        when(resultSet.getString("email")).thenReturn("aditya@yahoo.com");
        when(resultSet.getInt("age")).thenReturn(19);

        //When

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        //Then
        Customer expected = new Customer(1, "Aditya", "aditya@yahoo.com", 19);

        assertThat(actual).isEqualTo(expected);

        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }
}
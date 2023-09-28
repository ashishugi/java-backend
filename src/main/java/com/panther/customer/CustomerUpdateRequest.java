package com.panther.customer;

public record CustomerUpdateRequest(String name, String email, Integer age) {
}

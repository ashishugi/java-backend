package com.amigoscode.customer;

import com.amigoscode.Main;
import org.springframework.stereotype.Service;

@Service
public class FooService {

    private final Main.Foo foo;

    public FooService(Main.Foo foo) {
        this.foo = foo;
        System.out.println();
    }

    private String getFoo() {
        return foo.name();
    }

}

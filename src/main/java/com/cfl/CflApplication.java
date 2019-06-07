package com.cfl;

import com.cfl.service.ObjectService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CflApplication {
    public static void main(String[] args) {
        SpringApplication.run(CflApplication.class, args);
        ObjectService objectService = new ObjectService();
        objectService.createCache();
    }
}

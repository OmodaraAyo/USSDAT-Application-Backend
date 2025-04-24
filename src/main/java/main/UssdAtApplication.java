package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class UssdAtApplication {
    public static void main(String[] args) {
        SpringApplication.run(UssdAtApplication.class, args);
    }
}
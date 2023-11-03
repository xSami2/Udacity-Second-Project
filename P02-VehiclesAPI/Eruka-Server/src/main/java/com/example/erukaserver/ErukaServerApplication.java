package com.example.erukaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ErukaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErukaServerApplication.class, args);
    }

}

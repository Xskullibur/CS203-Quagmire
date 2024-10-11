package com.project.G1_T3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableScheduling
@EnableWebSocket
public class G1T3Application {

    public static void main(String[] args) {
        SpringApplication.run(G1T3Application.class, args);
    }
}
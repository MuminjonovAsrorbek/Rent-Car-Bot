package uz.dev.rentcarbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RentCarBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentCarBotApplication.class, args);
    }

}

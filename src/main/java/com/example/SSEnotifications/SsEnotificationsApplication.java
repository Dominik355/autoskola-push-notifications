package com.example.SSEnotifications;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableAsync
public class SsEnotificationsApplication {
    
        @Autowired
        private DiscoveryClient discoveryClient;
    
	public static void main(String[] args) {
		SpringApplication.run(SsEnotificationsApplication.class, args);
	}

}

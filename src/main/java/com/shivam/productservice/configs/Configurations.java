package com.shivam.productservice.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configurations {
    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplateBuilder().build();
    }
}

package com.shivam.productservice.commons;

import com.shivam.productservice.dtos.ValidateTokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationCommons {
    private RestTemplate restTemplate;

    public AuthenticationCommons(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public ValidateTokenResponseDto validateToken(String tokenValue) {
        // 1. Call the validateToken API of the user service.
        // 2. return the response object

        ResponseEntity<ValidateTokenResponseDto> responseEntity = restTemplate.getForEntity(
                "http://localhost:3030/users/validate/" + tokenValue,
                ValidateTokenResponseDto.class
        );

        return responseEntity.getBody();
    }
}

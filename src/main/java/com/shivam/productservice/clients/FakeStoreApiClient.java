package com.shivam.productservice.clients;

import com.shivam.productservice.dtos.FakeStoreProductDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class FakeStoreApiClient {
    private final RestTemplate restTemplate;

    public FakeStoreApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<FakeStoreProductDto> getAllProducts() {
        String fakeStoreUrl = "https://fakestoreapi.com/products";

        ResponseEntity<FakeStoreProductDto[]> responseEntity =
                requestForEntity(fakeStoreUrl,HttpMethod.GET,null,FakeStoreProductDto[].class);

        FakeStoreProductDto[] fakeStoreProductDtos = validAndReturnResponse(responseEntity);

        if(fakeStoreProductDtos == null) return null;

        return Arrays.stream(fakeStoreProductDtos).toList();
    }

    public FakeStoreProductDto createProduct(FakeStoreProductDto fakeStoreProductDto) {
        String fakeStoreUrl = "https://fakestoreapi.com/products";

        ResponseEntity<FakeStoreProductDto> responseEntity =
                requestForEntity(fakeStoreUrl,HttpMethod.POST,fakeStoreProductDto, FakeStoreProductDto.class);

        return validAndReturnResponse(responseEntity);
    }

    public FakeStoreProductDto getProductById(Long productId) {
        String fakeStoreUrl = "https://fakestoreapi.com/products/{id}";

        ResponseEntity<FakeStoreProductDto> responseEntity =
                requestForEntity(fakeStoreUrl,HttpMethod.GET,null, FakeStoreProductDto.class,productId);

        return validAndReturnResponse(responseEntity);
    }

    public FakeStoreProductDto updateProduct(Long productId, FakeStoreProductDto fakeStoreProductDto) {
        String fakeStoreUrl = "https://fakestoreapi.com/products/{id}";

        ResponseEntity<FakeStoreProductDto> responseEntity =
                requestForEntity(fakeStoreUrl,HttpMethod.PUT,fakeStoreProductDto, FakeStoreProductDto.class,productId);

        return validAndReturnResponse(responseEntity);
    }

    public FakeStoreProductDto deleteProduct(Long productId) {
        String fakeStoreUrl = "https://fakestoreapi.com/products/{id}";

        ResponseEntity<FakeStoreProductDto> responseEntity =
                requestForEntity(fakeStoreUrl,HttpMethod.DELETE,null, FakeStoreProductDto.class,productId);

        return validAndReturnResponse(responseEntity);
    }

    private <T> ResponseEntity<T> requestForEntity(String url, HttpMethod httpMethod, @Nullable Object request,
                                                   Class<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }

    private <T> T validAndReturnResponse(ResponseEntity<T> responseEntity) {
        if(responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        }
        return null;
    }
}

package com.shivam.productservice.services;

import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.dtos.Role;
import com.shivam.productservice.dtos.UserDetailsResponseDto;
import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.exceptions.ElementNotFoundException;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.CategoryRepository;
import com.shivam.productservice.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private RestTemplate restTemplate;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              RestTemplate restTemplate){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product replaceProduct(Product product) {
        if (product.getId() == null){
            throw new RuntimeException("parameter id is not passed in the request!");
        }

        Optional<Product> optionalProduct = productRepository.findById(product.getId());

        if (optionalProduct.isEmpty()){
            throw new ElementNotFoundException("product with id " + product.getId() + " does not exists.");
        }

        return createProduct(product);
    }

    @Override
    public Product updateProduct(Product product) {
        if (product.getId() == null){
            throw new RuntimeException("product id should not be null.");
        }

        Optional<Product> optionalProduct = productRepository.findById(product.getId());

        if (optionalProduct.isEmpty()){
            throw new ElementNotFoundException("product with id " + product.getId() + " does not exists.");
        }

        Product saveProduct = optionalProduct.get();

        if(product.getTitle() != null){
            saveProduct.setTitle(product.getTitle());
        }

        if(product.getPrice() != null){
            saveProduct.setPrice(product.getPrice());
        }

        if (product.getCategory() != null && product.getCategory().getId() != null){
            Optional<Category> optionalCategory = categoryRepository.findById(product.getCategory().getId());
            if (optionalCategory.isEmpty()) {
                throw new ElementNotFoundException("category with id " +
                        product.getCategory().getId() + " is not present.");
            }
            saveProduct.setCategory(optionalCategory.get());
        }

        if (product.getDescription() != null){
            saveProduct.setDescription(product.getDescription());
        }

        if (product.getImage() != null){
            saveProduct.setImage(product.getImage());
        }

        return productRepository.save(saveProduct);
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()){
            throw new ElementNotFoundException("product with id " + id + " does not exists.");
        }

        return optionalProduct.get();
    }

    @Override
    public List<Product> getAllProducts(Long id) {
        // This api is not for all users.
        // To call this api, a user should be a MENTOR or a ADMIN

        UserDetailsResponseDto responseDto = restTemplate.getForObject(
                "http://UserService/users/" + id,
                UserDetailsResponseDto.class
        );

        if (responseDto == null){
            throw new RuntimeException("Something went wrong");
        }

        UserDto userDto = responseDto.getUser();

        boolean isMentorOrAdmin = false;

        for (Role role : userDto.getRoles()){
            if ("MENTOR".equals(role.getName()) || "ADMIN".equals(role.getName())){
                isMentorOrAdmin = true;
                break;
            }
        }

        if (!isMentorOrAdmin){
            throw new RuntimeException("Not MENTOR or a ADMIN user");
        }

        return productRepository.findAll();
    }

    @Override
    public ResponseEntity<ResponseDto> deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()){
            throw new ElementNotFoundException("product with id " + id + " does not exists.");
        }

        productRepository.deleteById(id);

        ResponseDto responseDto = new ResponseDto();
        if (!productRepository.existsById(id)){
            responseDto.setMessage("product with id " + id + " is successfully deleted.");
        } else {
            responseDto.setMessage("Something went wrong. Please try again.");
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    public Page<Product> searchProduct(int pageNumber, int pageSize){
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageRequest);
    }

    @Override
    public Page<Product> searchProduct(int pageNumber, int pageSize, String sortingParam) {
        Sort sort = Sort.by(sortingParam).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return productRepository.findAll(pageRequest);
    }
}

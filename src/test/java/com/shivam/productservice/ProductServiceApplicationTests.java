package com.shivam.productservice;

import com.shivam.productservice.repositories.CategoryRepository;
import com.shivam.productservice.repositories.ProductRepository;
import com.shivam.productservice.repositories.projections.ProductWithTitleAndDescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class ProductServiceApplicationTests {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testCategoryRepository(){
        categoryRepository.deleteById(1L);
    }
    @Test
    void testProductRepository(){
        Optional<ProductWithTitleAndDescription> optionalProduct = productRepository.filterSQLTitleDescriptionById(1L);
        ProductWithTitleAndDescription product = optionalProduct.get();
        System.out.println("Title: " + product.getTitle());
        System.out.println("Description: " + product.getDescription());
        System.out.println("DEBUG");
    }
}

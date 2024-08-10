package com.shivam.productservice.repositories;

import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.projections.ProductWithTitleAndDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    // Sample HQL queries
    @Query("select p from products p where p.id = :id")
    Optional<Product> filterProductById(@Param("id") Long id);
    @Query("select p.title as title, p.description as description from products p where p.id = :id")
    Optional<ProductWithTitleAndDescription> getTitleDescriptionById(@Param("id") Long id);
    @Query("select p.title as title, p.description as description from products p join categories c on p.category.id = c.id where p.id = :id")
    Optional<ProductWithTitleAndDescription> getTitleDescriptionByCategoryId(@Param("id") Long id);
    // Sample SQL queries
    @Query(value = "select * from products p where p.id = :id", nativeQuery = true)
    Optional<Product> filterSQLProductById(@Param("id") Long id);
    @Query(value = "select p.title as title, p.description as description from products p where p.id = :id", nativeQuery = true)
    Optional<ProductWithTitleAndDescription> filterSQLTitleDescriptionById(@Param("id") Long id);
}

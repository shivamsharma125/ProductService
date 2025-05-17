package com.shivam.productservice.utils;

import com.shivam.productservice.models.Product;
import com.shivam.productservice.models.State;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> buildSearchSpecifications(String keyword, String category,
                                                                   Double minPrice, Double maxPrice,
                                                                   Boolean isPremium) {
        Specification<Product> specification = new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(criteriaBuilder.equal(root.get("state"), State.ACTIVE));

                if (keyword != null && !keyword.isBlank()){
                    Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
                    Predicate descLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase() + "%");
                    predicates.add(criteriaBuilder.or(titleLike,descLike));
                }

                if (category != null && !category.isBlank()){
                    Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("title")),"%" + category + "%");
                    Predicate descLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("description")),"%" + category + "%");
                    predicates.add(criteriaBuilder.or(titleLike,descLike));
                }

                if (minPrice != null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice));
                }

                if (maxPrice != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice));
                }

                if (isPremium != null){
                    predicates.add(criteriaBuilder.equal(root.get("isPremium"),isPremium));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return specification;
    }
}

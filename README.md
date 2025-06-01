# Product Service

## Overview

The **Product Service** is a modular, RESTful microservice built with **Spring Boot**, designed to handle product-related operations such as creating, retrieving, updating, deleting, and searching for products. It supports integration with external APIs and includes advanced features like **role-based access**, **Redis caching**, and **MockMvc/JUnit test coverage**.

---

## Key Features

* ✅ CRUD operations on products
* 🔍 Full-text & filtered product search (category, price, premium filter)
* 👤 Role-based access control for secure product viewing (CUSTOMER role check)
* 🚀 Redis caching to boost performance (cache hit/miss logic)
* 🧪 Comprehensive unit and integration tests using JUnit 5 & MockMvc
* 🔄 Flyway-based database versioning and migration
* 🌐 Two service implementations:

    * `SelfProductService`: MySQL + Redis
    * `FakeStoreProductService`: External Fake Store API + Redis

---

## API Endpoints

### ProductController - `/products`

| Method   | Endpoint                         | Description                           |
| -------- | -------------------------------- | ------------------------------------- |
| `GET`    | `/products`                      | Get all products                      |
| `POST`   | `/products`                      | Create a new product                  |
| `GET`    | `/products/{id}`                 | Get product by ID                     |
| `GET`    | `/products/{productId}/{userId}` | Get product with user role validation |
| `PUT`    | `/products/{id}`                 | Update product                        |
| `DELETE` | `/products/{id}`                 | Delete product                        |

### SearchController - `/search`

| Method | Endpoint  | Description                  |
| ------ | --------- | ---------------------------- |
| `GET`  | `/search` | Search products with filters |

Search supports filtering by:

* `query` (title/description)
* `category`
* `minPrice`, `maxPrice`
* `isPremium`
* Pagination: `pageNo`, `pageSize`
* Sorting: `sortBy`, `direction`

---

## Services

### SelfProductService

* Uses MySQL for persistence
* Caches product objects in Redis (key: `PRODUCTS`) with put/get/delete logic
* Validates user roles by calling external UserService via RestTemplate

### FakeStoreProductService

* Communicates with external [Fake Store API](https://fakestoreapi.com/)
* Converts between internal `Product` and `FakeStoreProductDto`
* Caches data in Redis like the self service

### SearchService

* Implements flexible product filtering with Spring Data JPA Specifications
* Supports sorting and pagination

---

## Tech Stack

* **Java 17**, **Spring Boot 3**
* **Spring Data JPA**, **Spring Web**
* **Flyway** (database migration)
* **Redis** (Spring Data Redis)
* **JUnit 5**, **Mockito**, **MockMvc** (testing)
* **MySQL** (DB)
* **RestTemplate** for inter-service calls

---

## Testing

* ✅ All Controllers tested using `MockMvc` + `Mockito`
* ✅ All Services tested using `Junit` + `Mockito`
* ✅ Test coverage for:

    * Valid and invalid request handling
    * Exception scenarios
    * Search filtering & sorting logic

---

## Performance Optimizations

* 🔄 Redis used to cache frequently accessed product data

    * Reduces database/API load
    * Improves response times (cache hit check before DB/API access)

---

## Deployment Notes

* Ensure Redis and MySQL instances are up and configured properly
* Flyway will automatically run DB migrations on app startup
* Product data will populate cache after first access (lazy loading)

---

## Future Enhancements

* 🔐 Integrate JWT-based authentication for secured endpoints
* 📈 Add product analytics (click tracking, views per user)
* 🛒 Add Cart & Checkout microservices for full e-commerce experience

---

## 📌 Related Microservices

* 👤 **User Service** – Handle user registration and authentication
* 📧 **Email Service** – Send transactional emails via Kafka events
* 💳 **Payment Service** – Manage payments via Razorpay & Stripe
* 🔍 **Service Discovery** – Eureka-based service registry

---

> *"Built for speed, tested for stability, designed for growth."*

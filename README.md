# Shop-Engine E-commerce Application

## Description
A Spring Boot e-commerce app that provides backend for an online store.
It includes:
- User registration and authentication with JWT tokens
- Product management with categories and search functionality
- Shopping cart operations
- Order processing and management
- Address management for shipping
- Unit tests for the Service layer


## Technologies Used
- **Java 17**
- **Spring Boot 3.4.4** 
  - **Spring Data JPA**
  - **Spring Security**
  - **Spring Web**
- **JWT** 
- **MySQL**
- **Docker** 
- **Maven** 
- **Lombok**
- **ModelMapper**
- **jUnit**
- **Mockito**

### API Documentation
![Swagger UI](assets/swagger-ui.png)

## How to Run Using Docker

### Environment Setup
Create a `.env` file in the project root with the following variables:
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecommerce
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION_MS=86400000
JWT_COOKIE_NAME=JWT-TOKEN
```

### Running the Application
1. Build using:
   ```bash
   mvn clean package
   ```
2. Start the containers:
   ```bash
   docker-compose up -d
   ```

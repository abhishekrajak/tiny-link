# TinyLink - URL Shortener Service

TinyLink is a robust and scalable URL shortening service built with Spring Boot. It allows users to create short, memorable links that redirect to longer URLs, making them easier to share and track.

## Features

- **URL Shortening**: Convert long URLs into short, shareable links
- **Custom Short Codes**: Option to create custom short codes for URLs (for special users)
- **User Authentication**: Secure OAuth 2.0 login with Google
- **Multi-User Support**: Different user types with varying permissions
- **Automatic Code Generation**: Generates random short codes when custom codes aren't provided
- **Prefix Management**: Support for prefix-based URL routing
- **RESTful API**: Easy integration with other services

## Tech Stack

- **Backend**: Java 17
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: JPA with Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## API Endpoints

### Authentication

#### 1. Google OAuth Login
```http
GET /login/oauth2/code/google?code={auth_code}
```
Initiates OAuth flow with Google. Redirects to the callback URL with authentication code.

#### 2. Create Account/Login with Google
```http
POST /login/oauth2/account/create
Content-Type: application/json

{
  "token": "google_auth_token"
}
```

### URL Management

#### 1. Create a TinyLink
```http
POST /api/v1/tiny-link
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "tinyCode": "custom123",  // Optional
  "redirectionLink": "https://example.com/very/long/url"
}
```

#### 2. Get All User's Links
```http
GET /api/v1/links
Authorization: Bearer {jwt_token}
```

#### 3. Redirect to Original URL
```http
GET /{tinyCode}
```

## User Types

1. **BASE**: Can only use auto-generated short codes
2. **SPECIAL**: Can use custom short codes with prefix validation
3. **CORPORATE**: (Future implementation) Will have additional features

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL (or your preferred JPA-compatible database)
- Google OAuth 2.0 credentials

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishekrajak/tiny-link.git
   cd tiny-link
   ```

2. Configure the application:
   ```bash
   cp src/main/resources/application-example.yml src/main/resources/application.yml
   # Update application.yml with your database and OAuth credentials
   ```

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Configuration

Customize the application behavior by modifying `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tinylink
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: your_jwt_secret
  expiration: 86400000  # 24 hours in milliseconds

google:
  client:
    clientId: your_google_client_id
    clientSecret: your_google_client_secret
    redirectUri: http://localhost:8080/login/oauth2/code/google
```

## Error Handling

The API returns standardized error responses with the following format:

```json
{
  "errorCode": "ERROR_CODE",
  "message": "Human readable error message",
  "data": null
}
```

### Common Error Codes

- `AUTH_001`: Authentication required
- `AUTH_002`: Invalid or expired token
- `TL_001`: Tiny code not found
- `TL_002`: Tiny code generation failed
- `TL_003`: Tiny code already exists

## Security

- JWT-based authentication
- OAuth 2.0 with Google
- Role-based access control
- CSRF protection
- CORS configuration

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

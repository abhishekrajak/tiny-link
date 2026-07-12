# TinyLink - URL Shortener Service

TinyLink is a robust and scalable URL shortening service built with Spring Boot. It allows users to create short, memorable links that redirect to longer URLs, making them easier to share and track.

## Features

- **URL Shortening**: Convert long URLs into short, shareable links.
- **Custom Short Codes**: Option to create custom short codes for URLs (available for SPECIAL users).
- **User Authentication**: Secure OAuth 2.0 login with Google.
- **Demo User Access**: Easy testing via instant demo user generation with JWT.
- **Multi-User Support**: Different user types with varying permissions and link limits.
- **Automatic Code Generation**: Generates random short codes when custom codes aren't provided, ensuring no conflict with existing prefixes for BASE users.
- **Prefix Management**: Support for prefix-based URL routing for SPECIAL users.
- **Link Analytics**: Track link usage including IP address, user agent, and referrer.
- **Link Deactivation**: Ability to deactivate existing short links.
- **Error Redirection**: Custom error page for invalid or expired links.
- **Caching**: In-memory/Redis caching for faster redirection lookup.
- **RESTful API**: Easy integration with other services.

## Tech Stack

- **Backend**: Java 17 / Spring Boot 3.x
- **Security**: Spring Security with JWT & OAuth 2.0
- **Database**: PostgreSQL (JPA with Hibernate) & Flyway migrations
- **Caching**: Spring Cache with Redis support
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Testcontainers

## API Endpoints

### Authentication

#### 1. Google OAuth 2.0 Login
```http
GET /oauth2/authorization/google
```
Initiates the OAuth 2.0 flow with Google. Upon successful authentication, the server generates a JWT token and redirects to the configured frontend redirect URL (e.g. `${app.config.redirect-url}?token={jwt_token}`).

#### 2. Create Demo User (For Testing)
```http
POST /api/v1/users/demo
Content-Type: application/json
```
Generates a new demo user in the database and returns a JWT token for API testing without requiring Google login.

**Response (HTTP 200)**:
```json
{
  "errorCode": "0000",
  "errorMessage": "success",
  "data": {
    "emailId": "demo-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx@armatrix.dev",
    "jwtToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

### URL Management

#### 1. Create a TinyLink
```http
POST /api/v1/tiny-link
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "tinyCode": "custom123",  // Optional custom code for SPECIAL users
  "redirectionLink": "https://example.com/very/long/url"
}
```
**Response (HTTP 200)**:
```json
{
  "errorCode": "0000",
  "errorMessage": "success",
  "data": {
    "tinyCode": "custom123",
    "redirectionLink": "https://example.com/very/long/url",
    "custom": true,
    "createdAt": "2026-07-12T16:30:00Z",
    "remainingLinksCount": 99,
    "shortenedUrl": "http://localhost:8080/custom123"
  }
}
```

#### 2. Get All User's Links
```http
GET /api/v1/links
Authorization: Bearer {jwt_token}
```
**Response (HTTP 200)**:
```json
{
  "errorCode": "0000",
  "errorMessage": "success",
  "data": [
    {
      "tinyCode": "custom123",
      "redirectionLink": "https://example.com/very/long/url",
      "custom": true,
      "createdAt": "2026-07-12T16:30:00Z",
      "shortenedUrl": "http://localhost:8080/custom123"
    }
  ]
}
```

#### 3. Redirect to Original URL
```http
GET /{tinyCode}
```
Redirects to the original long URL with HTTP `302 Found`. If the code is inactive, invalid, or expired, it redirects to the custom error page `/error/link-not-found.html`. This endpoint also asynchronously records analytics metadata (IP, user agent, referrer).

#### 4. Update TinyLink Redirection URL
```http
PATCH /api/v1/tiny-link/url
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "tinyCode": "custom123",
  "redirectionLink": "https://new-example.com/updated/url"
}
```

#### 5. Deactivate TinyLink
```http
PATCH /api/v1/tiny-link/status/deactivate
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "tinyCode": "custom123"
}
```
Deactivates an existing tiny link, preventing any future redirections from using it.

---

## User Types and Limits

1. **BASE**: Can only use auto-generated short codes. Limit: Up to 50 links (configurable).
2. **SPECIAL**: Can use custom short codes with prefix validation. Limit: Up to 100 links (configurable).
3. **CORPORATE**: Full prefix control and enterprise features. Limit: Up to 1000 links (configurable).

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (required to run Testcontainers suite)
- PostgreSQL & Redis

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishekrajak/tiny-link.git
   cd tiny-link
   ```

2. Configure the application using your environment values:
   ```bash
   cp application-example.yml application-dev.yml 
   ```

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Configuration

Customize behavior in `application-dev.yml` under `app.config`:

```yaml
app.config:
  tiny-url-code-length: 7
  tiny-url-code-min-length: 7
  tiny-url-code-max-length: 20
  tiny-link-allowed-chars: ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789
  short-code-generation-max-retry-count: 5
  redirect-url: http://localhost:3000/auth.html
  base-url: http://localhost:3000
  base-user-max-links: 50
  special-user-max-links: 100
  corporate-user-max-links: 1000
```

---

## Error Handling

Standardized API response wrapper:

```json
{
  "errorCode": "ERROR_CODE",
  "errorMessage": "Detailed error message or validation status",
  "data": null
}
```

### Common Error Codes

- `0000`: Success
- `TL_0000`: Unknown Error
- `TL_0001`: Tiny code generation retry fail
- `TL_0002`: Prefix belongs to other user
- `TL_0003`: User not found
- `TL_0004`: Client URL exception
- `TL_0005`: Token fetch failed
- `TL_0006`: Tiny code count exceeded
- `TL_0007`: Self-referencing URL
- `TL_0008`: Internal URL forbidden
- `TL_0009`: Invalid URL
- `TL_0010`: Invalid tiny code
- `VALIDATION_ERROR`: Input payload validation failed
- `ACCESS_DENIED`: Unauthorized or forbidden action

---

## Security

- JWT stateless token validation
- Google OAuth 2.0 integration
- Origin CORS mapping
- CSRF Protection (disabled for stateless API)

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

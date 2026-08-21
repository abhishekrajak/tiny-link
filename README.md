# TinyLink - URL Shortener Service

TinyLink is a production-ready URL shortening service built with Spring Boot. It allows users to create short, memorable links that redirect to longer URLs, making them easier to share and track. The service is designed with a layered architecture, emphasizing security, scalability, and observability.

## Features

- **URL Shortening**: Convert long URLs into short, shareable links with configurable code length and character set.
- **Custom Short Codes**: Option to create custom short codes for URLs (available for SPECIAL and CORPORATE users).
- **User Authentication**: Secure OAuth 2.0 login with Google and stateless JWT validation.
- **Demo User Access**: Easy testing via instant demo user generation with JWT.
- **Multi-User Support**: Three user tiers (BASE, SPECIAL, CORPORATE) with varying permissions and link limits.
- **Automatic Code Generation**: Generates random short codes when custom codes aren't provided, ensuring no conflict with existing codes.
- **Prefix Management**: Support for prefix-based URL routing for SPECIAL and CORPORATE users.
- **Link Analytics**: Track link usage including IP address, user agent, and referrer, recorded asynchronously to avoid impacting redirect latency.
- **Link Deactivation**: Ability to deactivate existing short links, preventing future redirections.
- **Error Redirection**: Custom error page for invalid or expired links.
- **Caching**: Redis-backed caching for faster redirection lookup with cache eviction on updates.
- **Rate Limiting**: Tier-based API rate limiting using Bucket4j + Redis + AOP, configurable per endpoint and user tier.
- **Security Hardening**: SSRF protection, self-referencing URL validation, protocol allow-listing (http/https), and CORS origin mapping.
- **Observability**: Spring Actuator health endpoints and Prometheus metrics via Micrometer.
- **API Documentation**: Interactive Swagger UI via Springdoc OpenAPI.
- **Database Migrations**: Schema versioning and evolution managed by Flyway.

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security, JWT (jjwt), OAuth 2.0 (Google OIDC)
- **Database**: PostgreSQL with JPA/Hibernate
- **Migrations**: Flyway
- **Caching**: Spring Cache with Redis (Lettuce client)
- **Rate Limiting**: Bucket4j with distributed Redis proxy
- **Validation**: Jakarta Validation, Hibernate Validator
- **API Docs**: Springdoc OpenAPI (Swagger UI)
- **Observability**: Spring Actuator, Micrometer Prometheus
- **Build Tool**: Maven
- **Utilities**: Lombok, Apache Commons Lang
- **Testing**: JUnit 5, Mockito, Testcontainers, Spring Security Test
- **DevOps**: Docker, Docker Compose

## Architecture

The service follows a layered architecture with clear separation of concerns:

- **Controller Layer**: REST endpoints with input validation, OpenAPI annotations, and rate-limit enforcement via custom AOP aspects.
- **Service Layer**: Business logic for link management, user context resolution, and async analytics event processing.
- **Repository Layer**: Spring Data JPA repositories for data access.
- **Security Layer**: Stateless JWT authentication filter, OAuth2 success handler, and CORS configuration.
- **Cross-Cutting Concerns**: Custom `@RateLimited` annotation with AOP proxying, async exception handling, and request/response logging filters.

### Key Design Patterns
- **AOP (Aspect-Oriented Programming)**: Centralized rate-limiting logic using `@Around` advice on `@RateLimited` annotations.
- **Cache-Aside**: `@Cacheable` for redirection URL lookups and `@CacheEvict` on updates/deactivations.
- **Async Event Processing**: Analytics events are saved asynchronously using `@Async` with `Propagation.REQUIRES_NEW`, decoupled from the critical redirect path.
- **DTO Pattern**: Request/response objects separate from JPA entities.
- **Strategy Pattern**: User-tier-based rate-limit capacity resolution.

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

#### 3. Delete Current User
```http
DELETE /api/v1/users
Authorization: Bearer {jwt_token}
```
Deletes the authenticated user and all associated tiny links.

---

### URL Management

#### 1. Create a TinyLink
```http
POST /api/v1/tiny-link
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "tinyCode": "custom123",  // Optional custom code if required
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
GET /api/v1/links?page=0&size=10&sort=createdAt,desc
Authorization: Bearer {jwt_token}
```
Retrieves a paginated list of tiny links for the authenticated user.

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

- Java 17
- Maven 3.9+
- Docker & Docker Compose (recommended for running dependencies)
- PostgreSQL & Redis (if running locally without Docker)

### Running with Docker Compose (Recommended)

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishekrajak/tiny-link.git
   cd tiny-link
   ```

2. Create a `.env` file in the project root:
   ```env
   DB_USER=myuser
   DB_PASSWORD=mypassword
   DB_NAME=tiny-link
   PG_ADMIN_EMAIL=admin@example.com
   PG_ADMIN_PASSWORD=admin
   ```

3. Start all services:
   ```bash
   docker compose up --build
   ```

4. The application will be available at `http://localhost:8080`.

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishekrajak/tiny-link.git
   cd tiny-link
   ```

2. Configure the application using your environment values:
   ```bash
   cp application-example.yml application-dev.yml
   ```
   Update `application-dev.yml` with your database, Redis, and OAuth2 credentials.

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with Testcontainers (requires Docker)
./mvnw verify
```

---

## Configuration

### Application Configuration

Customize behavior in `application-dev.yml` under `app.config`:

```yaml
app:
  config:
    redirect-url: http://localhost:3000/auth.html
    base-url: http://localhost:3000
    tiny-url-code-length: 7
    tiny-url-code-min-length: 7
    tiny-url-code-max-length: 20
    tiny-link-allowed-chars: ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789
    short-code-generation-max-retry-count: 5
    base-user-max-links: 50
    special-user-max-links: 100
    corporate-user-max-links: 1000
    api-base-url: http://localhost:8080
```

### Rate Limiting Configuration

Rate limits are defined per user tier and endpoint in `application-dev.yml`:

```yaml
app:
  rate-limiting:
    default-value:
      capacity: 30
      durationMinutes: 60
    tiers:
      BASE:
        createTinyLink: 10
        updateTinyLink: 20
        deactivateTinyLink: 10
        deleteTinyLink: 50
        analyticsDashboard: 50
      SPECIAL:
        createTinyLink: 10
        updateTinyLink: 20
        deactivateTinyLink: 10
        deleteTinyLink: 50
        analyticsDashboard: 15
      CORPORATE:
        createTinyLink: 10
        updateTinyLink: 20
        deactivateTinyLink: 10
        deleteTinyLink: 50
        analyticsDashboard: 5
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

- **JWT Stateless Authentication**: Token-based request authorization without server-side sessions.
- **Google OAuth 2.0 Integration**: Secure third-party login with OIDC.
- **SSRF Protection**: URL validation blocks internal network access (`localhost`, `127.0.0.1`) and self-referencing requests to the service itself.
- **Protocol Allow-Listing**: Only `http` and `https` schemes are permitted.
- **CORS Origin Mapping**: Restricted to the configured frontend base URL.
- **CSRF Protection**: Disabled for stateless REST API.
- **Rate Limiting**: Distributed per-user-tier throttling via Bucket4j + Redis.

> **Note**: `application-dev.yml` contains sensitive credentials (database, OAuth2, JWT secrets). It is git-ignored. Use `application-example.yml` as a template and never commit secrets to version control.

---

## Observability

- **Spring Actuator**: Health checks and application metrics exposed at `/actuator`.
- **Prometheus Metrics**: Micrometer registry for monitoring request latency, error rates, and system health.
- **OpenAPI/Swagger**: Interactive API documentation available at `/swagger-ui.html` when the application is running.

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
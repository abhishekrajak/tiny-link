# TinyLink - URL Shortener Service

TinyLink is a robust and scalable URL shortening service built with Spring Boot. It allows users to create short, memorable links that redirect to longer URLs, making them easier to share and track.

## Features

- **URL Shortening**: Convert long URLs into short, shareable links
- **Custom Short Codes**: Option to create custom short codes for URLs (for special users)
- **Multi-User Support**: Different user types with varying permissions
- **Automatic Code Generation**: Generates random short codes when custom codes aren't provided
- **Prefix Management**: Support for prefix-based URL routing
- **RESTful API**: Easy integration with other services

## Tech Stack

- **Backend**: Java 11+
- **Framework**: Spring Boot 2.7.x
- **Database**: JPA with Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito

## API Endpoints

### 1. Create a TinyLink

```http
POST /api/v1/tiny-link
Content-Type: application/json

{
  "user": {
    "userType": "BASE"
  },
  "tinyCode": "custom123",  // Optional
  "redirectionLink": "https://example.com/very/long/url"
}
```

### 2. Redirect to Original URL

```http
GET /{tinyCode}
```

## User Types

1. **BASE**: Can only use auto-generated short codes
2. **SPECIAL**: Can use custom short codes with prefix validation
3. **CORPORATE**: (Future implementation) Will have additional features

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL/PostgreSQL (or your preferred JPA-compatible database)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/tiny-link.git
   cd tiny-link
   ```

2. Configure the database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tinylink
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## Configuration

Customize the application behavior by modifying `application.properties`:

```properties
# TinyLink configuration
tinylink.tiny-url.code-length=6
tinylink.tiny-url.allowed-chars=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
tinylink.tiny-url.generation.max-retry=10
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

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with ❤️ using Spring Boot
- Inspired by popular URL shorteners like Bitly and TinyURL

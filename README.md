# URL Shortener 🔗

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
> A minimal production-ready URL shortener built with Kotlin + Spring Boot 🚀
## 📌Project Description
This project is a URL shortener application that allows users to:
- send a long URL and receive a shortened version
- resolve a shortened URL to its original long URL
- hitting the shortened URL and get redirect to the original long URL

### 🛠Tech Stack
![Kotlin](https://img.shields.io/badge/Kotlin-JVM%20--%20Backend-blueviolet?logo=kotlin)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis)
![Gradle](https://img.shields.io/badge/Gradle-Build%20Tool-02303A?logo=gradle)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker)
![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-%23ClojureGreen?logo=swagger)
![JUnit](https://img.shields.io/badge/JUnit-5-important?logo=java)
![Ktlint](https://img.shields.io/badge/Ktlint-Code%20Formatter-blueviolet?logo=kotlin)
![Flyway](https://img.shields.io/badge/Flyway-DB%20Migration-orange?logo=flyway)
### ✨Features
- ✅ Shorten long URLs with optional expiration time
- ✅ Resolve shortened URLs to their original long URLs
- ✅ Redirect from shortened URLs to original URLs
- ✅ Redis based global ID generator for unique short URLs
- ✅ Base62 encoding for short URL generation
- ✅ Caching of URL mappings in Redis for quick access
- ✅ Expiration of short URLs in a specified time in future
- ✅ RESTful API with Swagger documentation
- ✅ Docker-based local and test environment setup
- ✅ Unit and integration tests

### 🧱 Architecture Decisions
- Why MySQL?
  - MySQL is simple to set up and use but in this case MySQL is better than Postgress in read/write heavy apps.
  - Can it change? of course. The application can be easily adapted to use Postgres or any other SQL database.
  - Why not NoSQL? → simple structure of URLs
- Why Redis?
  - Redis is used for caching the URL mappings to improve performance and reduce database load for the time that request number increases.
  - It also serves as a global ID generator for unique short URLs.
- Handling Expiration:
  - The application supports setting an expiration time for URLs, after which they will no longer be accessible.
  - This is managed by storing the expiration time in the database and checking it during URL resolution.
  - Expired URLs are automatically removed from the database and cache.
- Limitations/TODOs:
  - ❌The application does not currently support custom short URLs.
  - ❌It does not handle URL sanitization.
  - ❌It does not implement user authentication or authorization.
  - ❌The application does not support bulk URL shortening or resolution.
  - ❌The application does not support URLs longer than 512 characters.

### Pre-requisites
- Java 21
- Gradle (or Gradle Wrapper)
- Docker (for running MySQL and Redis)

# 🚀 Getting Started

### 1. Run MySQL and Redis with Docker
> Note: Docker must be running before launching the app.
#### Run the dependencies:
```bash
docker compose up -d
```

### 2. Start the application (Local Profile)
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Debug from IntelliJ:
1. Open `UrlShortenerApplication.kt`
2. Click the green Run icon near the main() method
3. Go to Run > Edit Configurations
4. Set VM options: `-Dspring.profiles.active=local`
5. Click Apply → Debug


### 🧪 Running Tests
```bash
./gradlew clean test
```

### 🧾API Documentation
#### Swagger UI
http://localhost:8080/swagger-ui/index.html
#### Health Check
http://localhost:8080/actuator/health

### 🧹 Code Formatting
#### Ktlint check
```bash
./gradlew ktlintCheck     
```
#### Ktlint format
```bash
./gradlew ktlintFormat
```

### 📂Project Structure
```text
src/
├── main/
│   ├── kotlin/com/tohid/urlShortener/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── repository/
│   │   ├── service/
│   │   └── utils/
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       └── db/migration/
└── test/
    └── kotlin/com/tohid/urlShortener/
```
## 🐳 Run with Docker

### Build the image
```bash
./gradlew bootJar
docker build -t url-shortener .
```


### 📝 License

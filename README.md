
# 🚀 Spring Cloud Gateway Rate Limiter

This project is a standalone microservice built using **Spring Boot 3**, **Spring Cloud Gateway**, and **Java 17**.  
It demonstrates a **pluggable rate limiter architecture** with support for multiple algorithms like **Token Bucket** and **Leaky Bucket**.

---

## 🔧 Features

- ✅ Custom rate limiting per route
- ✅ Pluggable algorithm support (Token Bucket, Leaky Bucket)
- ✅ Easy to extend with new algorithms (e.g., Sliding Window, Redis)
- ✅ Built on Spring WebFlux (Reactive)
- ✅ Config-driven setup (hardcoded in v1, dynamic-ready)
- ✅ Swagger/OpenAPI integration
- ✅ Postman test collection included

---

## 📦 Technologies Used

- Spring Boot 3.x
- Spring Cloud Gateway
- Java 17
- Spring WebFlux
- Springdoc OpenAPI (Swagger)
- Postman (for API testing)

---

## 🏗️ Project Structure

```
spring-cloud-gateway-ratelimiter/
├── controller/       --> Test endpoints
├── filter/           --> Gateway filter to enforce rate limiting
├── model/            --> Rate limit configuration model
├── service/          --> TokenBucket & LeakyBucket algorithms
├── application.yml   --> Route + filter config
├── postman/          --> Postman collection file
└── README.md
```

---

## ⚙️ How to Run

### 1. Clone the project

```bash
git clone https://github.com/dinesh-arney/spring-cloud-gateway-ratelimiter.git
cd spring-cloud-gateway-ratelimiter
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

---

## 📌 Gateway Routes

| Route ID | Path Prefix         | Algorithm     | Limit Details           |
|----------|----------------------|---------------|--------------------------|
| route1   | `/tokenbucket/**`    | Token Bucket  | 5 tokens per 60 sec      |
| route2   | `/leakybucket/**`    | Leaky Bucket  | 1 token leaked/sec (10 max) |

---

## 🔑 How It Works

1. Each request is intercepted by a **custom filter**.
2. A unique key is generated per client (IP).
3. Filter checks the algorithm (based on route).
4. If allowed → forwarded to backend (httpbin.org).
5. If denied → returns HTTP `429 Too Many Requests`.

---

## 🧪 Postman Test Collection

- Import the file `postman/rate-limiter-test.postman_collection.json`.
- Two ready-to-test endpoints:

```
GET http://localhost:8080/tokenbucket/get
GET http://localhost:8080/leakybucket/get
```

Expect rate limiting to kick in if limits are exceeded.

---

## 🔍 Swagger UI

Access documentation at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔄 Ready for Extension

- 🔌 Plug in Redis by replacing `ConcurrentHashMap`
- 📥 Externalize config from DB / Spring Cloud Config
- 🧩 Add more strategies like Fixed Window or Sliding Window

---

## 🧭 Architecture Overview

```
Client → Spring Cloud Gateway → Custom Filter → RateLimiterService (TokenBucket / LeakyBucket)
        → Allow → Forward to Backend
        → Deny → HTTP 429
```

---

## 📂 Future Improvements

- [ ] Redis-backed distributed rate limiter
- [ ] Spring Cloud Config + Actuator `/refresh` support
- [ ] Metrics with Micrometer & Prometheus
- [ ] Admin dashboard for live config

---

## 📄 License

MIT (Free for personal or commercial use)

---

## 🙌 Author

Dinesh Arney  
🔗 [LinkedIn](https://linkedin.com/in/dinesharney)  
🧠 Passionate about scalable systems, cloud-native design, and developer growth.

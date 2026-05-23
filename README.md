# Library Management System

A full-stack Library Management System built with Spring Boot (backend) and Angular (frontend), designed to help administrators manage books, members, and track issue/return transactions.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 4 |
| Database | PostgreSQL (Neon cloud) |
| ORM | Spring Data JPA / Hibernate |
| API Docs | Swagger / OpenAPI |
| Frontend | Angular 17, TypeScript |
| UI Framework | Bootstrap 5, Bootstrap Icons |

---

## Project Structure

```
Library_Management/
├── backend/                  
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/librarymanagement/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
│
├── frontend/                 
│   ├── src/
│   │   ├── app/
│   │   │   ├── books/
│   │   │   ├── members/
│   │   │   ├── transactions/
│   │   │   ├── dashboard/
│   │   │   └── shared/
│   │   └── environments/
│   ├── angular.json
│   └── package.json
│
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- Node.js 18+
- Angular CLI (`npm install -g @angular/cli`)
- PostgreSQL database (local or Neon cloud)

---

### Backend Setup

**Step 1 — Navigate to backend:**
```bash
cd backend
```

**Step 2 — Create `application.properties` under `src/main/resources/`:**
```properties
# Server
server.port=8090

# Database
spring.datasource.url=jdbc:postgresql://your-host/your-db
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Flyway
spring.flyway.enabled=false

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

**Step 3 — Run the application:**
```bash
mvn spring-boot:run
```

**Step 4 — Verify:**
```
Backend    : http://localhost:8090
Swagger UI : http://localhost:8090/swagger-ui/index.html
```

---

### Frontend Setup

**Step 1 — Navigate to frontend:**
```bash
cd frontend
```

**Step 2 — Install dependencies:**
```bash
npm install
```

**Step 3 — Run the application:**
```bash
ng serve
```

**Step 4 — Verify:**
```
Frontend : http://localhost:4200
```

---

## Features

### Dashboard
- Total books, members, active issues and overdue counts
- Quick action shortcuts to common tasks

### Book Management
- Add, edit, soft delete books
- Search by title, author, ISBN, category
- Track total and available copies per book

### Member Management
- Add, edit, deactivate members
- Active / Inactive status tracking

### Issue / Return Workflow
- Issue books to active members
- Return books with automatic stock update
- Full transaction history per book and member
- Status tracking — ISSUED / RETURNED / OVERDUE

### Validation & Error Handling
- Required field validation on all forms
- Duplicate ISBN and email detection
- Global exception handler with consistent error responses
- User friendly success and error messages throughout UI

---

## Business Rules

| Rule | Behaviour |
|------|-----------|
| Book availability | Cannot issue a book with 0 available copies |
| Duplicate issue | Cannot issue same book twice to same member without returning |
| Inactive member | Inactive members cannot issue books |
| Already returned | Cannot return an already returned transaction |
| Due date | Automatically set to 14 days from issue date |
| Overdue detection | Status set to OVERDUE if returned after due date |
| Soft delete | Deleted books still appear in transaction history |

---

## API Endpoints

### Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/books | Get all books |
| GET | /api/books/{id} | Get book by id |
| GET | /api/books/search | Search books |
| POST | /api/books | Create book |
| PUT | /api/books/{id} | Update book |
| DELETE | /api/books/{id} | Soft delete book |

### Members
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/members | Get all members |
| GET | /api/members/{id} | Get member by id |
| POST | /api/members | Create member |
| PUT | /api/members/{id} | Update member |
| PATCH | /api/members/{id}/deactivate | Deactivate member |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/transactions | Get all transactions |
| GET | /api/transactions/member/{id} | Get by member |
| GET | /api/transactions/book/{id} | Get by book |
| POST | /api/transactions/issue | Issue book |
| POST | /api/transactions/{id}/return | Return book |

---

## Architecture

```
Controller → Service → Repository → Database
```

| Layer | Responsibility |
|-------|---------------|
| Controller | Handles HTTP requests, delegates to service |
| Service | Contains all business logic and validations |
| Repository | Handles all database operations via JPA |
| DTO | Separates request/response models from entities |
| Exception Handler | Global handler returns consistent error responses |

---

## Assumptions & Decisions

- **Soft delete on books** — deleted books still appear in transaction history for audit purposes
- **No hard delete on members** — members can only be deactivated to preserve transaction history
- **Due date** — automatically set to 14 days from issue date
- **Available copies in issue dropdown** — only books with available copies > 0 are shown
- **Active members in issue dropdown** — only active members are shown
- **PostgreSQL** used instead of MySQL due to local installation issues — connection string in `application.properties` is the only change needed to switch back to MySQL
- **Flyway disabled** — tables auto created by Hibernate via `ddl-auto=update` for simplicity

---
## Live Demo

| Layer | URL |
|-------|-----|
| Frontend | https://library-management-system-vert-eight.vercel.app |
| Backend API | https://library-management-system-6a61.onrender.com |
| Swagger UI | https://library-management-system-6a61.onrender.com/swagger-ui/index.html |

> Note: Backend is hosted on Render free tier and may take 30-60 seconds to wake up after inactivity.

## Author

**Rohit Sarkar**

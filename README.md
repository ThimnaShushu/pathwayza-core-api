# PathwayZA

## Author

Thimna Shushu — Graduate BSc Computer Science & Business Computing, UCT  
Graduate Software Technical Engineer & Tester, Huawei Technologies South Africa

**AI-powered career guidance for South African matric learners.**

PathwayZA helps Grade 10–12 learners discover careers that match their subjects, APS score, and interests — and shows them exactly what skills they need to get there.

---

## The problem

Over 1.2 million Grade 12 learners write matric every year in South Africa. Most receive no structured, personalised career guidance. They choose degrees based on what their parents did, what sounds prestigious, or what their friends are doing — not on their actual strengths or the real job market.

PathwayZA changes that.

---

## What it does

A learner signs up, enters their subjects and marks, and the platform:

- Calculates their APS score automatically
- Matches them to careers based on their subjects and interests
- Shows a skill gap analysis — what's missing for their target career
- Returns real SA job market demand data per career
- (Roadmap) Matches them to university programmes they qualify for
- (Roadmap) Surfaces bursary opportunities like NSFAS, Sasol, and MTN

---

## Architecture

```
┌─────────────────────────────────────────┐
│           React Frontend                │
│        (Swagger UI / Postman)           │
└──────────────────┬──────────────────────┘
                   │ HTTP + JWT
                   ▼
┌─────────────────────────────────────────┐
│     Java Spring Boot — Core API         │
│              Port 8080                  │
│                                         │
│  POST /api/auth/register                │
│  POST /api/auth/login                   │
│  POST /api/learner/subjects  (secured)  │
│  GET  /api/learner/profile   (secured)  │
│  GET  /api/learner/careers   (secured)  │
└────────────┬────────────────────────────┘
             │ REST call (RestTemplate)
             ▼
┌─────────────────────────────────────────┐
│     C# ASP.NET — AI Service             │
│              Port 5000                  │
│                                         │
│  POST /api/score    (career matching)   │
│  POST /api/skillgap (gap analysis)      │
└────────────┬────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│           PostgreSQL 16                 │
│                                         │
│  learners · learner_subjects            │
│  saved_careers                          │
└─────────────────────────────────────────┘
```

**Flow:** Learner registers and logs in via the Java service → submits subjects and marks → Java computes APS → Java calls the C# service → C# scores and ranks matched careers → learner sees results.

---

## Tech stack

| Layer | Technology |
|---|---|
| Core API | Java 21, Spring Boot 3.5, Spring Security, JWT (jjwt 0.12) |
| AI Service | C# .NET 8, ASP.NET Core Web API |
| Database | PostgreSQL 16 |
| ORM | Hibernate / Spring Data JPA |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Auth | JWT (stateless, BCrypt password hashing) |

---

## Project structure

```
pathwayza/
├── core-api/                  # Java Spring Boot service
│   └── src/main/java/com/pathwayza/core_api/
│       ├── controller/        # AuthController, LearnerController
│       ├── model/             # Learner, LearnerSubject
│       ├── repository/        # LearnerRepository, LearnerSubjectRepository
│       ├── security/          # JwtUtil, JwtAuthFilter, SecurityConfig
│       └── service/           # ApsCalculator
│
└── ai-service/                # C# ASP.NET service (coming Sunday)
    └── PathwayZA.AIService/
        └── Controllers/       # ScoreController, SkillGapController
```

---

## Run locally

### Prerequisites

- Java 21+
- .NET 8 SDK
- PostgreSQL 16
- Maven

### 1. Database setup

Open pgAdmin and run:

```sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE learners (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255),
  grade INT,
  province VARCHAR(50),
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE learner_subjects (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  learner_id UUID REFERENCES learners(id),
  subject VARCHAR(100),
  level VARCHAR(20),
  mark INT
);

CREATE TABLE saved_careers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  learner_id UUID REFERENCES learners(id),
  career_name VARCHAR(150),
  saved_at TIMESTAMP DEFAULT NOW()
);
```

### 2. Java service

```bash
cd core-api
# Update src/main/resources/application.properties:
# spring.datasource.url=jdbc:postgresql://localhost:5432/pathwayzadb
# spring.datasource.username=postgres
# spring.datasource.password=yourpassword

mvn spring-boot:run
# Runs on http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

### 3. C# AI service

```bash
cd ai-service/PathwayZA.AIService
dotnet run
# Runs on http://localhost:5000
```

---

## API reference

### Auth

```
POST /api/auth/register
Body: { "name": "...", "email": "...", "passwordHash": "...", "grade": 11, "province": "Western Cape" }
Response: { "email": "...", "token": "..." }

POST /api/auth/login
Body: { "email": "...", "password": "..." }
Response: { "token": "..." }
```

### Learner (all require Authorization: Bearer <token>)

```
POST /api/learner/subjects
Body: [{ "subject": "Mathematics", "level": "HG", "mark": 72 }, ...]
Response: { "aps": 28, "subjects": [...] }

GET /api/learner/profile
Response: { "learner": {...}, "subjects": [...], "aps": 28 }

GET /api/learner/careers
Response: { "careers": [{ "name": "Software Engineer", "matchScore": 91, "demandSA": "High" }] }
```

### AI service

```
POST /api/score
Body: { "aps": 28, "subjects": ["Mathematics", "Science"], "interests": ["technology"] }
Response: { "careers": [{ "name": "...", "matchScore": 91, "demandSA": "High" }] }

POST /api/skillgap
Body: { "learnerSubjects": ["English", "History"], "targetCareer": "Software Engineer" }
Response: { "missingSubjects": [...], "missingSkills": [...], "recommendedCourses": [...] }
```

---

## APS scoring

PathwayZA uses the standard South African APS conversion:

| Mark | APS Points |
|------|------------|
| 80%+ | 7 |
| 70–79% | 6 |
| 60–69% | 5 |
| 50–59% | 4 |
| 40–49% | 3 |
| 30–39% | 2 |
| 0–29% | 1 |

---

## Roadmap

- [ ] University programme matcher — UCT, Wits, TUT, UJ, Stellenbosch with APS thresholds
- [ ] TVET college pathways for learners not university-bound
- [ ] Bursary finder — NSFAS, Sasol, MTN, Anglo American matched to learner profile
- [ ] NLP career chat — "What does a data engineer actually do in SA?" via LLM API
- [ ] React frontend — onboarding quiz, career matches dashboard, skill gap detail view
- [ ] Job market demand data — live SA job posting ingestion per career category

---

## Why PathwayZA

Most career tools in South Africa are built for professionals already in the workforce. PathwayZA is built for the learner who doesn't yet know what they want to be — and gives them a data-driven answer grounded in their actual academic profile and the South African job market, not generic global career lists.

---



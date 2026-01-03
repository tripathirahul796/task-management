# Task Management Backend
Spring Boot backend implementing a **Task Management System**.  
Supports creating, reading, updating, deleting, and listing tasks. The project is structured following **Domain-Driven Design (DDD)** principles.

---

## Overview

- **Language:** Java 21  
- **Build Tool:** Gradle (wrapper included)  
- **Frameworks:** Spring Boot (Web MVC), Jakarta Validation  
- **Persistence:** In-memory repository (no database required)  

---

## Prerequisites

Ensure the following are installed on your machine:

- **Java 21 JDK**  
  - Verify: `java -version` should print `21.x`  
- **Git** (optional, for cloning the repo)

## Clone & Build

1. Clone the repo:

```
git clone https://github.com/tripathirahul796/task-management.git
cd taskManagement
```

2. Run tests:

```
./gradlew.bat test --no-daemon
```

3. Build the project (jar):

```
./gradlew.bat build --no-daemon
```

4. Run the application locally:

```
./gradlew.bat bootRun
```

The app will start on port 8080 by default: `http://localhost:8080`.


##  Testing

Run the full test suite locally:

```powershell
./gradlew.bat test
```

Unit tests and integration-style tests are included under `src/test/java`.

---

## 🧭 Project Structure (high level)

- `com.taskmanagement.domain` — domain entities (`Task`, `TaskStatus`)
- `com.taskmanagement.dto` — request/response DTOs
- `com.taskmanagement.repositories` — repository interfaces + in-memory implementation
- `com.taskmanagement.services` — service layer and business logic
- `com.taskmanagement.controllers` — REST controllers and global exception handler

---
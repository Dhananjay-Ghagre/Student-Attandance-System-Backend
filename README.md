# Backend - Student Attendance System

Spring Boot backend with JWT authentication and role-based access control.

## Setup

1. Create MySQL database:
```sql
CREATE DATABASE attendance_db;
```

2. Update database credentials in `src/main/resources/application.yml`

3. Run the application:
```bash
mvn spring-boot:run
```

Backend runs on http://localhost:8080

## Default Login Credentials
- Admin: admin/admin123
- Teacher: teacher1/teacher123  
- Student: student1/student123
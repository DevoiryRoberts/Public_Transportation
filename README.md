# Public Transportation Management System

**Backend API** לניהול מערכת תחבורה ציבורית — אוטובוסים, נהגים, תחנות, קווים ונסיעות, בארכיטקטורת שכבות מבוססת Spring Boot.

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-Auth-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-0F80CC?style=for-the-badge&logo=h2&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![JWT](https://img.shields.io/badge/JWT-jjwt-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-Boilerplate_Reduction-red?style=for-the-badge)

> ריפו זה מכיל את שכבת השרת (Backend) בלבד. צד הלקוח (Frontend) לא נכלל בריפו זה.

---

## Overview

שרת REST API לניהול תפעול תחבורה ציבורית: ניהול צי אוטובוסים כולל מעקב טסטים, ניהול נהגים (המשמשים גם כמשתמשי המערכת המחוברים), ניהול תחנות וקווים עם סדר תחנות דינמי, ותזמון נסיעות הכולל בדיקת התנגשויות (נהג/אוטובוס תפוסים) וחישוב זמני הגעה. תיעוד API מלא ואינטראקטיבי זמין דרך Swagger UI.

## Key Features

- ניהול צי אוטובוסים: CRUD, מעקב תאריך טסט אחרון, איתור אוטובוסים הזקוקים לטסט
- נהגים המשמשים גם כמשתמשי מערכת מחוברים (Spring Security `UserDetails`), עם הפרדת שדות ציבוריים/מנהליים באמצעות Jackson `@JsonView`
- ניהול קווים ותחנות: הוספה/הסרה של תחנה במיקום ספציפי בקו, עם סידור מחדש אוטומטי של סדר התחנות שאחריה
- תזמון נסיעות עם בדיקת זמינות נהג ואוטובוס, חישוב זמן הגעה משוער, וסימולציית מיקום בזמן אמת על ציר הקו
- מחיקה רכה ברמת בסיס הנתונים (Hibernate `@SQLDelete` + `@SQLRestriction`) לכל ישות מרכזית, עם שחזור ומחיקה סופית נפרדת
- טיפול גלובלי בשגיאות (ולידציה, Not Found, שגיאות שלמות נתונים) עם מבנה תגובה אחיד

## Architecture

```
Config        → Spring Security, JSON Views (Public / Admin)
Controllers   → REST Endpoints + תיעוד Swagger
Services      → לוגיקה עסקית
Repositories  → Spring Data JPA, כולל שאילתות Native לניהול ארכיון
Mappers       → המרת Entity <-> DTO
Models        → ישויות JPA ו-Enums
DTOs          → אובייקטי בקשה ותגובה
Exceptions    → GlobalExceptionHandler
```

## Roles

| Role | Permissions |
|---|---|
| Driver | משתמש מחובר במערכת; פעולות על נתוניו האישיים (לפי `@PreAuthorize`) |
| Admin | שליטה מלאה: ניהול נהגים, אוטובוסים, תחנות, קווים ונסיעות, כולל ארכוב ומחיקה סופית |

## Getting Started

```bash
git clone <repository-url>
cd public_transportation_project
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

השרת עולה כברירת מחדל בכתובת `http://localhost:8080`, ותיעוד ה-API זמין ב-`/swagger-ui.html`. בסיס הנתונים הוא H2 (In-Memory כברירת מחדל), כך שאין צורך בהתקנת שרת DB חיצוני להרצה מקומית.

## Technical Notes

- קיימת תלות ל-JWT (`io.jsonwebtoken:jjwt`) ב-`pom.xml`, אך היא אינה מחוברת עדיין ל-Filter Chain או לקונטרולר Login בפועל — נראה כי מדובר בתשתית מוכנה לאימות מבוסס Token שטרם מומשה במלואה.
- אנוטציות `@PreAuthorize` קיימות ברמת המתודה, אך יש לוודא הפעלת `@EnableMethodSecurity` כדי שייאכפו בפועל; כרגע `SecurityFilterChain` מוגדר עם `permitAll()` לכל בקשה.
- בסיס הנתונים המוגדר הוא H2 (`scope: runtime`) — מתאים לפיתוח ובדיקות; יש לשקול מעבר ל-DB פרודקשן (PostgreSQL/MySQL) לפני עלייה לסביבה חיה בסיום הפיתוח.

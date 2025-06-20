# Book Share – Backend

**Book Share** to platforma do wypożyczania książek między użytkownikami. Aplikacja backendowa została zbudowana jako wielomodułowy monolit w oparciu o Spring Boot 3.4 i Java 21. Udostępnia REST API do obsługi operacji związanych z użytkownikami, książkami, polubieniami oraz wypożyczeniami. Autoryzacja użytkowników oparta jest na mechanizmie JWT.

---

## Spis treści

- [Technologie](#technologie)
- [Struktura projektu](#struktura-projektu)
- [Uruchamianie aplikacji](#uruchamianie-aplikacji)
- [Konfiguracja JWT](#konfiguracja-jwt)
- [Baza danych i migracje](#baza-danych-i-migracje)
- [Testowanie](#testowanie)
- [API](#api)

---

## Technologie

- **Java 21**
- **Spring Boot 3.4.x**
- **Maven**
- **PostgreSQL**
- **Liquibase** – zarządzanie migracjami bazy danych
- **Spring Security + JWT** – uwierzytelnianie i autoryzacja
- **JUnit 5** – testy jednostkowe
- **RestAssured + Testcontainers** – testy E2E

---

## Struktura projektu

Projekt jest zorganizowany jako wielomodułowa aplikacja typu monolit:

```
book-share-be/
├── app/          # Moduł uruchamiający Spring Boot
├── core/         # Logika biznesowa (serwisy, model domenowy)
├── db/           # Dostęp do bazy danych, Liquibase
├── security/     # Konfiguracja JWT, filtry, zabezpieczenia
├── web/          # Kontrolery REST, DTO, konfiguracja webowa
├── dependencies/ # pom.xml odpowiedzialny za wersje bibliotek wykorzystywane w proejkcie
└── pom.xml       # Główny plik Maven
```

---

## Uruchamianie aplikacji

### Wymagania

- Java 21+
- Maven 3.8+
- Uruchomiona instancja PostgreSQL z odpowiednio skonfigurowaną bazą danych

### Krok po kroku

1. **Sklonuj repozytorium:**
   ```bash
   git clone https://github.com/BartekCode/book-share-be.git
   cd book-share-be
   ```

2. **Skonfiguruj plik `application.yml` (np. w module `app/src/main/resources/`)**:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/bookshare
       username: your_user
       password: your_password
     mail:
       host: localhost
       port: 1025
       username: bartek
       password: bartek
   security:
     activationCodeExpireTime: 15 # po jakim czasie w min kod aktywacyjny przestanie dzialac
     jwt:
       secretKey: your_jwt_secret_key
       expiration: 3600000 # w milisekundach (np. 1 godzina)
   ```

3. **Zbuduj projekt:**
   ```bash
   mvn clean install
   ```

4. **Uruchom aplikację:**
   ```bash
   mvn spring-boot:run -pl app
   ```

---

## Konfiguracja JWT

JWT (JSON Web Token) jest używane do autoryzacji. Tokeny są podpisywane przy użyciu sekretu określonego w pliku `application.yml`.

- **Endpoint logowania:** `POST /login`  
  W odpowiedzi zwracany jest token JWT.

- **Nagłówek autoryzacyjny dla zabezpieczonych endpointów:**
  ```
  Authorization: Bearer <token>
  ```

---

## Baza danych i migracje

Projekt używa Liquibase do zarządzania schematem bazy danych. Pliki migracyjne znajdują się w module `db`:

```
db/src/main/resources/db/changelog/
├── changelog-master.yaml
├── 01-init-schema.yaml
├── ...
```

Migracje są automatycznie uruchamiane przy starcie aplikacji – nie wymagają ręcznego wykonania.

---

## Testowanie

### Testy jednostkowe

Testy jednostkowe napisane w JUnit 5:

```bash
mvn test
```

### Testy integracyjne / E2E

Projekt zawiera testy E2E z użyciem RestAssured i Testcontainers. Aby je uruchomić:

```bash
mvn verify -P e2e
```

---

## API

SWAGGER: [openapi.yml](openapi.yml)

GET http://localhost:8080/v3/api-docs.yaml

---

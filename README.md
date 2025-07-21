# 🔐 Домашнее задание Т1 по аутентификации

## Запуск

 Для запуска нужно выполнить только одну команду:
 ``` docker compose up ```

Все необходимые таблицы в базе данных Postgres будут созданы автоматически


## 📄 Эндпоинты

---

### 🔸 POST `/auth/register`

Регистрация нового пользователя.

#### Request
role (опционально): ADMIN, PREMIUM_USER, GUEST (по умолчанию — GUEST)
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "securepassword",
  "role": "PREMIUM_USER"
}

```
### 🔸 POST /auth/login
Аутентификация и получение токенов.

```json
{
  "username": "testuser",
  "password": "securepassword"
}
```

### 🔸 POST /auth/refresh-token
Обновление access-токена с помощью refresh-токена.

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 🔸 POST /auth/logout
Отзыв (деактивация) refresh-токена. Требует Authorization заголовок с Bearer accessToken.

Authorization: Bearer ACCESS_TOKEN 

### 🔸 GET /premium/features
Доступен только пользователям с ролью PREMIUM_USER.


Authorization: Bearer ACCESS_TOKEN

### 🔸 GET /admin/dashboard
Доступен только пользователям с ролью ADMIN.

Authorization: Bearer ACCESS_TOKEN


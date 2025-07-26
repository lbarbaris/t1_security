# 🔐 Дополнительное домашнее задание по аутентификации

## Отличие и обоснование

Ключевым отличием от предыдущей реализации является замена JWS на JWE.
Я пересмотрел запись воркшопа, примерно на 54 минуте было сказано, что токен JWE позволяет передавать чувствительную информацию.
При этом компрометация в таком случае будет невозможна, так как дешифровать можно только с помощью закрытого ключа

Для реализации JWE я применил библиотеку com.nimbusds:nimbus-jose-jwt с гитхаба и переписал JwtService

## Запуск

Если вы запускали проект из ветки main (4 домашнее задание), то для запуска необходимо сначала удалить контейнер myapp_container из docker'a
 ``` docker rm myapp_container ```

Затем необходимо выполнить команду
 ``` docker compose up ```

### !Первый запуск может происходить долго! это нормально

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


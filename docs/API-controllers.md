# API Documentation (JSON-focused)

Base URL: `${spring.api.prefix}` (e.g. `/api/v1`). Responses use `ApiResponse<T>`:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "...",
  "data": { /* payload */ },
  "errors": null,
  "timestamp": "2024-01-01T00:00:00"
}
```
Auth: Public endpoints are POST `/auth/login`, `/auth/register`, `/auth/refresh-token`, `/auth/logout`, and GET `/topics/find-all`. Others need `Authorization: Bearer <token>`.

---

## AuthenticationController (`${spring.api.prefix}/auth`)

### POST /login (public)
Request:
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd"
}
```
Response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Đăng nhập thành công!",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<refresh-jwt>",
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "fullName": "User Name",
      "role": "USER",
      "ankiDeckName": "Default",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  },
  "errors": null,
  "timestamp": "..."
}
```

### POST /register (public)
Request:
```json
{
  "email": "new@example.com",
  "password": "P@ssw0rd",
  "fullName": "New User"
}
```
Response: `ApiResponse<UserResponse>`

### POST /refresh-token (public)
Request:
```json
{ "refreshToken": "<refresh-jwt>" }
```
Response: `ApiResponse<?>` (service returns refreshed tokens)

### POST /logout (public)
Request:
```json
{ "refreshToken": "<refresh-jwt>" }
```
Response: `ApiResponse<?>` with logout message

---

## UserController (`${spring.api.prefix}/user`)
### GET /  (Bearer + ADMIN)
Query params: `pageNo`, `pageSize`, `sort`, `keyword`
Response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Lấy danh sách thành công",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 100,
    "totalPages": 5,
    "data": [ { "id": "uuid", "email": "...", "fullName": "...", "role": "USER", "ankiDeckName": "Default", "isActive": true, "createdAt": "...", "updatedAt": "..." } ]
  },
  "errors": null,
  "timestamp": "..."
}
```

---

## TopicController (`${spring.api.prefix}/topics`)
### GET /find-all (public)
Query: `pageNo`, `pageSize`, `sort`, `keyword`
Response (topics list):
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "tải danh sách topic trong database thành công!",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 10,
    "totalPages": 1,
    "data": [ { "id": 1, "name": "Travel", "description": "...", "createdAt": "..." } ]
  },
  "errors": null,
  "timestamp": "..."
}
```

---

## DictionaryWordController (`${spring.api.prefix}/vocabularies`)

### GET /lookup/basic (Bearer)
Query: `word`
Response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Tra cứu cơ bản thành công",
  "data": {
    "dictionaryWordId": 12,
    "word": "apple",
    "partOfSpeech": "noun",
    "phonetic": "ˈæp.əl",
    "meaningVi": "quả táo",
    "explanationVi": "...",
    "exampleSentence": "I ate an apple.",
    "audioUrl": "https://..."
  },
  "errors": null,
  "timestamp": "..."
}
```

### POST /lookup/ai (Bearer)
Request:
```json
{
  "word": "run",
  "contextSentence": "He can run very fast."
}
```
Response: same structure as lookup/basic

### POST /translate (Bearer)
Request:
```json
{ "text": "Hello world" }
```
Response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Dịch đoạn văn thành công",
  "data": { "originalText": "Hello world", "translatedText": "Xin chào thế giới" },
  "errors": null,
  "timestamp": "..."
}
```

### GET /find-all (Bearer + ADMIN)
Response (paged dictionary words):
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "tải danh sách từ trong database thành công!",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 200,
    "totalPages": 10,
    "data": [ { "id": 1, "word": "apple", "partOfSpeech": "noun", "pronunciation": "ˈæp.əl", "meaningVi": "quả táo" } ]
  },
  "errors": null,
  "timestamp": "..."
}
```

---

## UserSavedWordController (`${spring.api.prefix}/user-saved-words`)

### POST / (Bearer)
Request:
```json
{
  "dictionaryWordId": 12,
  "vocabularyGroupId": 3,
  "sourceSentence": "I ate an apple.",
  "sourceUrl": "https://news.com/article"
}
```
Response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Lưu từ vựng vào sổ tay của bạn thành công!",
  "data": { "userSavedWordId": 99 },
  "errors": null,
  "timestamp": "..."
}
```

### GET /find-all/{vocabularyGroupId} (Bearer)
Response (paged saved words):
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "tải danh sách từ trong database thành công!",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1,
    "data": [
      {
        "id": 99,
        "userId": "uuid",
        "dictionaryWordResponse": {
          "id": 12,
          "word": "apple",
          "partOfSpeech": "noun",
          "pronunciation": "ˈæp.əl",
          "meaningVi": "quả táo"
        },
        "sourceUrl": "https://news.com/article",
        "ankiStatus": "PENDING",
        "ankiNoteId": 123456
      }
    ]
  },
  "errors": null,
  "timestamp": "..."
}
```

### DELETE /{id} (Bearer)
Response:
```json
{ "success": true, "code": "SUCCESS", "message": "Xóa từ vựng khỏi sổ tay của bạn thành công!", "data": null, "errors": null, "timestamp": "..." }
```

### POST /sync-anki (Bearer)
Success response:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "Đồng bộ thành công 3 từ vựng sang Anki!",
  "data": { "syncedWords": 3 },
  "errors": null,
  "timestamp": "..."
}
```
On Anki connection error:
```json
{ "success": false, "code": "ERROR", "message": "Lỗi: Anki chưa được mở hoặc AnkiConnect chưa cấu hình đúng port 8765!" }
```

---

## VocabularyGroupController (`${spring.api.prefix}/vocabulary-groups`)

### GET /find-all (Bearer)
Response (paged groups):
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "tải danh sách nhóm từ vựng bạn tạo thành công!",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 3,
    "totalPages": 1,
    "data": [ { "id": 3, "userId": "uuid", "name": "TOEIC", "isDefault": false, "createdAt": "...", "updatedAt": "..." } ]
  },
  "errors": null,
  "timestamp": "..."
}
```

### POST / (Bearer)
Request:
```json
{ "name": "TOEIC" }
```
Response: `ApiResponse<VocabularyGroupResponse>`

### PUT /{id} (Bearer)
Request:
```json
{ "name": "IELTS" }
```
Response: `ApiResponse<VocabularyGroupResponse>`

### DELETE /{id} (Bearer)
Response:
```json
{ "success": true, "code": "SUCCESS", "message": "xóa bộ từ vựng thành công", "data": null, "errors": null, "timestamp": "..." }
```

---

Request validation hints:
- `@NotBlank`: non-empty text
- `@NotNull`: required field
- `@Size(min=8)`: password length >= 8

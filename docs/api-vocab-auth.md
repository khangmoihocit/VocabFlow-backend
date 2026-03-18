# Tài liệu API - Authentication & Vocabulary

> Tất cả response đều bọc trong `ApiResponse` với các trường: `success` (boolean), `code` (string), `message` (string), `data` (object), `errors` (object|null), `timestamp` (ISO-8601). Các response trả về dưới đây chỉ liệt kê cấu trúc `data`.

> Biến tiền tố `${spring.api.prefix}` cần được thay bằng cấu hình runtime (ví dụ: `/api/v1`).

## Authentication Controller (`${spring.api.prefix}/auth`)

### 1) POST `/login`
- **Dùng để**: Đăng nhập, nhận access token/refresh token.
- **Request body** (`application/json`):
```json
{
  "email": "user@example.com",
  "password": "your_password"
}
```
- **Response `data`**:
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "fullName": "User Name",
    "role": "ROLE_USER",
    "ankiDeckName": "<deckName>",
    "isActive": true,
    "createdAt": "2024-03-01T12:00:00",
    "updatedAt": "2024-03-02T15:00:00"
  }
}
```

### 2) POST `/register`
- **Dùng để**: Tạo tài khoản mới.
- **Request body**:
```json
{
  "email": "user@example.com",
  "password": "your_password",
  "fullName": "User Name"
}
```
- **Response `data`**: giống `user` ở trên.

### 3) POST `/refresh-token`
- **Dùng để**: Lấy cặp token mới từ refresh token hợp lệ.
- **Request body**:
```json
{
  "refreshToken": "<jwt>"
}
```
- **Response `data`**:
```json
{
  "accessToken": "<new_access_token>",
  "refreshToken": "<new_refresh_token>"
}
```

### 4) POST `/logout`
- **Dùng để**: Đăng xuất, vô hiệu refresh token.
- **Request body**: giống `/refresh-token`.
- **Response `data`**: `null` (message: "Đăng xuất thành công!").

## Vocabulary Controller (`${spring.api.prefix}/vocabularies`)

### 1) POST `/save-word-user`
- **Dùng để**: Lưu một từ/cụm từ vào sổ tay của người dùng.
- **Request body**:
```json
{
  "dictionaryWordId": 123,
  "sourceSentence": "The cat sits on the mat.",
  "sourceUrl": "https://example.com/article"
}
```
- **Response `data`**:
```json
{
  "userSavedWordId": 456
}
```

### 2) GET `/lookup/basic`
- **Dùng để**: Tra cứu nhanh một từ có sẵn trong từ điển nội bộ.
- **Query params**: `word` (string, bắt buộc) — từ/cụm từ cần tra.
- **Response `data`**:
```json
{
  "dictionaryWordId": 123,
  "word": "cat",
  "partOfSpeech": "noun",
  "phonetic": "/kæt/",
  "meaningVi": "con mèo",
  "explanationVi": "Một loài động vật nuôi trong nhà",
  "exampleSentence": "The cat sits on the mat.",
  "audioUrl": "https://.../cat.mp3"
}
```

### 3) POST `/lookup/ai`
- **Dùng để**: Tra cứu từ bằng AI (có ngữ cảnh câu nếu cần).
- **Request body**:
```json
{
  "word": "run",
  "contextSentence": "I run every morning to stay fit."
}
```
- **Response `data`**: cùng cấu trúc như `/lookup/basic`.

### 4) POST `/translate`
- **Dùng để**: Dịch một đoạn văn bản.
- **Request body**:
```json
{
  "text": "Hello, how are you?"
}
```
- **Response `data`**:
```json
{
  "originalText": "Hello, how are you?",
  "translatedText": "Xin chào, bạn khỏe không?"
}
```

### 5) GET `/find-all` *(yêu cầu quyền ADMIN)*
- **Dùng để**: Phân trang danh sách từ trong cơ sở dữ liệu.
- **Query params** (tùy chọn):
  - `pageNo` (int, mặc định 1)
  - `pageSize` (int, mặc định 20)
  - `sort` (string, ví dụ `word,asc`)
  - `keyword` (string, tìm kiếm theo từ)
- **Response `data`** (dạng `PageResponse<DictionaryWordResponse>`):
```json
{
  "pageNo": 1,
  "pageSize": 20,
  "totalElements": 200,
  "totalPages": 10,
  "data": [
    {
      "id": 123,
      "word": "cat",
      "partOfSpeech": "noun",
      "pronunciation": "kæt",
      "meaningVi": "con mèo"
    }
  ]
}
```

### 6) GET `/find-user-saved-word`
- **Dùng để**: Phân trang danh sách từ đã lưu của người dùng hiện tại.
- **Query params** (tùy chọn): `pageNo`, `pageSize`, `sort` (mặc định `id,asc`), `keyword`.
- **Response `data`** (`PageResponse<WordSavedFindResponse>`):
```json
{
  "pageNo": 1,
  "pageSize": 20,
  "totalElements": 40,
  "totalPages": 2,
  "data": [
    {
      "id": 456,
      "userId": "uuid",
      "dictionaryWordResponse": {
        "id": 123,
        "word": "cat",
        "partOfSpeech": "noun",
        "pronunciation": "kæt",
        "meaningVi": "con mèo"
      },
      "sourceUrl": "https://example.com/article",
      "ankiStatus": "NEW",
      "ankiNoteId": 999
    }
  ]
}
```


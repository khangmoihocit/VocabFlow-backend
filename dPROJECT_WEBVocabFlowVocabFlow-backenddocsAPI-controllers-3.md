# Tài liệu API - Vocabulary & Dictionary Management

Tài liệu này tiếp tục mô tả 3 nhóm API bao gồm `DictionaryWordController`, `VocabularyGroupController` và `UserSavedWordController`.

Tất cả các API đều có chung định dạng Response (gói trong `ApiResponse<T>`):
```json
{
  "success": true,               
  "code": "SUCCESS",             
  "message": "Thông báo",        
  "data": { ... },               
  "errors": null,                
  "timestamp": "2024-03-24T12:00:00"
}
```

---

## 3. Vocabulary Group (`/vocabulary-groups`)
**Prefix base:** `/vocabulary-groups` (ví dụ: `http://localhost:8085/api/v1/vocabulary-groups`)
**Quyền truy cập:** User đã đăng nhập (Cần Access Token)
**Mô tả:** Các API hỗ trợ người dùng tạo và quản lý các "Nhóm từ vựng" (sổ tay tự tạo của mỗi cá nhân).

### 3.1. Lấy danh sách nhóm từ vựng của User
- **URL**: `/find-all`
- **Method**: `GET`
- **Query Parameters**:
  - `pageNo` (int): Trang hiện tại (Mặc định: 1)
  - `pageSize` (int): Kích thước trang (Mặc định: 20)
  - `sort` (string): Trường sắp xếp (Mặc định: "id,asc")
  - `keyword` (string): Tên nhóm để tìm kiếm (Mặc định: rỗng)
- **Response Data** (`PageResponse<VocabularyGroupResponse>`):
  ```json
  {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1,
    "data": [
      {
        "id": 1,
        "userId": "uuid...",
        "name": "IELTS Target 7.0",
        "isDefault": false,
        "createdAt": "2024-03-24...",
        "updatedAt": "2024-03-24..."
      }
    ]
  }
  ```

### 3.2. Tạo nhóm từ vựng mới
- **URL**: `/`
- **Method**: `POST`
- **Request Body** (`VocabularyGroupRequest` - JSON):
  ```json
  {
    "name": "Từ vựng IT"  // Bắt buộc (không được rỗng)
  }
  ```
- **Response Data** (`VocabularyGroupResponse`): Thông tin nhóm từ vựng vừa sinh ra.

### 3.3. Chỉnh sửa tên nhóm từ vựng
- **URL**: `/{id}`
- **Method**: `PUT`
- **Path Parameter**: `id` (Long - ID bộ từ vựng cần update)
- **Request Body** (`VocabularyGroupRequest` - JSON):
  ```json
  {
    "name": "Từ vựng Giao Tiếp Update"
  }
  ```
- **Response Data** (`VocabularyGroupResponse`): Nhóm từ với data mới cập nhật.

### 3.4. Xóa bộ từ vựng
- **URL**: `/{id}`
- **Method**: `DELETE`
- **Path Parameter**: `id` (Long - ID bộ từ vựng cần xóa)
- **Response Data**: Trả message rỗng (Không có data), báo xóa thành công.

---

## 4. User Saved Words (`/user-saved-words`)
**Prefix base:** `/user-saved-words` 
**Quyền truy cập:** User đã đăng nhập (Cần Access Token)
**Mô tả:** Các API thao tác đưa từ vựng thực tế vào trong sổ tay của User đã tạo ở bước trên, cũng như đồng bộ nó với Anki Desktop.

### 4.1. Lưu từ vựng vào Sổ tay
- **URL**: `/`
- **Method**: `POST`
- **Request Body** (`UserSaveWordRequest` - JSON):
  ```json
  {
    "dictionaryWordId": 12,       // Bắt buộc (ID của từ vựng hệ thống)
    "vocabularyGroupId": 1,       // Bắt buộc (ID của nhóm lưu / sổ tay của bạn)
    "sourceSentence": "I am a programmer", // Ngữ cảnh câu chứa từ vựng
    "sourceUrl": "https://..."    // Link bài viết hoặc ngữ cảnh nguồn
  }
  ```
- **Response Data** (`UserSavedWordResponse`):
  ```json
  {
    "userSavedWordId": 1205 // ID Mapping nối giữa Từ vựng - Nhóm
  }
  ```

### 4.2. Xem danh sách toàn bộ từ đã lưu trong 1 Sổ tay
- **URL**: `/find-all/{vocabularyGroupId}`
- **Method**: `GET`
- **Path Parameter**: `vocabularyGroupId` (Long - ID của sổ tay nhóm)
- **Query Parameters**: `pageNo` (1), `pageSize` (20), `sort` ("id,asc"), `keyword` (rỗng)
- **Response Data** (`PageResponse<WordSavedFindResponse>`):
  ```json
  {
    "pageNo": 1,
    "pageSize": 20,
    "totalElements": 2,
    "totalPages": 1,
    "data": [
      {
        "id": 1205,
        "userId": "uuid...",
        "dictionaryWordResponse": { /* thông tin full từ vựng */ },
        "sourceUrl": "...",
        "ankiStatus": "NOT_SYNCED",
        "ankiNoteId": null
      }
    ]
  }
  ```

### 4.3. Xóa một từ khỏi Sổ tay
- **URL**: `/{id}`
- **Method**: `DELETE`
- **Path Parameter**: `id` (Long - ID Mapping - `userSavedWordId`)
- **Response Data**: Message báo xóa thành công. Cuốn sổ tay vẫn còn, chỉ từ vựng đó ở trong đó sẽ mất đi.

### 4.4. Đồng bộ (Sync) sang Anki
- **URL**: `/sync-anki`
- **Method**: `POST`
- **Request Body**: Không. Yêu cầu AnkiConnect Desktop app (port `8765`) đã được bật.
- **Response Data**:
  ```json
  {
    "syncedWords": 15 // Trả về con số các từ đã đồng bộ
  }
  ```

---

## 5. Dictionary Words (`/vocabularies`)
**Prefix base:** `/vocabularies`
**Quyền truy cập:** Thường là **PUBLIC** cho công cụ tra cứu, và quyền **ADMIN** cho tool xem backend.
**Mô tả:** Các API truy xuất dữ liệu từ vựng hệ thống và Tích hợp công cụ AI/Translate dịch thuật.

### 5.1. Tra từ Cơ Bản (Database local)
- **URL**: `/lookup/basic`
- **Method**: `GET`
- **Quyền:** Public.
- **Query Parameter**: `word` (String) - từ tiếng anh cần tra cứu.
- **Response Data** (`LookupResponse`):
  ```json
  {
    "dictionaryWordId": 12,
    "word": "hello",
    "partOfSpeech": "exclamation",
    "phonetic": "/həˈləʊ/",
    "meaningVi": "xin chào",
    "explanationVi": "lời dùng để chào hỏi",
    "exampleSentence": "Hello, how are you?",
    "audioUrl": "https://...mp3"
  }
  ```

### 5.2. Tra từ Nâng Cao (Qua AI Context)
- **URL**: `/lookup/ai`
- **Method**: `POST`
- **Quyền:** Public.
- **Request Body** (`LookupRequest` - JSON):
  ```json
  {
    "word": "bank",             // Bắt buộc (từ cần tra)
    "contextSentence": "River bank" // Ngữ cảnh (giúp AI dịch mượt hơn vào ý nghĩa 'Bờ sông' thay vì 'Ngân hàng')
  }
  ```
- **Response Data** (`LookupResponse`): Giống với cấu trúc của `/lookup/basic`, nhưng dữ liệu do LLM generate động.

### 5.3. Dịch văn bản dài
- **URL**: `/translate`
- **Method**: `POST`
- **Quyền:** Public.
- **Request Body** (`TranslateRequest` - JSON):
  ```json
  {
    "text": "This is a beautiful day to learn programming." // Bắt buộc
  }
  ```
- **Response Data** (`TranslateResponse`):
  ```json
  {
    "originalText": "This is a beautiful day to learn programming.",
    "translatedText": "Đây là một ngày đẹp trời để học lập trình."
  }
  ```

### 5.4. Lấy tất cả từ điển trong Hệ thống
- **URL**: `/find-all`
- **Method**: `GET`
- **Quyền:** **ADMIN** (`hasRole('ADMIN')`).
- **Query Parameters**: `pageNo` (1), `pageSize` (20), `sort` ("word,asc"), `keyword` (rỗng)
- **Response Data** (`PageResponse<DictionaryWordResponse>`):
  Danh sách phân trang toàn bộ từ điển chuẩn đang có trong DataBase Backend.

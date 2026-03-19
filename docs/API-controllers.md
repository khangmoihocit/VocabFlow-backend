# API Documentation - Controllers

This document describes the public API endpoints implemented by the six controllers requested: `AuthenticationController`, `UserController`, `TopicController`, `DictionaryWordController`, `UserSavedWordController`, and `VocabularyGroupController`.

Notes:
- Base path is `${spring.api.prefix}` (literal) as used in the code; replace with the real prefix (commonly `/api/v1`) when calling the API.
- All responses are wrapped in `ApiResponse<T>` unless otherwise noted.
- Authentication: endpoints that require a Bearer token are noted. Security rules are defined in `core.config.SecurityConfig`.

---

## AuthenticationController
Base: `${spring.api.prefix}/auth`

### POST /login
- Auth required: No (public)
- Purpose: Authenticate user and return access + refresh tokens and user info.
- Request body (JSON):
  - AuthenticationRequest
    - email: string (required) - `@NotBlank`
    - password: string (required) - `@NotBlank`, `@Size(min = 8)`
- Response: `ApiResponse<AuthenticationResponse>`
  - AuthenticationResponse
    - accessToken: string
    - refreshToken: string
    - user: UserResponse
      - id: UUID
      - email: string
      - fullName: string
      - role: string
      - ankiDeckName: string
      - isActive: boolean
      - createdAt: LocalDateTime
      - updatedAt: LocalDateTime

### POST /register
- Auth required: No (public)
- Purpose: Create a new user account.
- Request body (JSON):
  - UserCreationRequest
    - email: string (required) - `@NotBlank`
    - password: string (required) - `@NotBlank`, `@Size(min = 8)`
    - fullName: string (required) - `@NotEmpty`
- Response: `ApiResponse<UserResponse>` (see `UserResponse` above)

### POST /refresh-token
- Auth required: No (public)
- Purpose: Exchange a refresh token for a new access token (service-specific return).
- Request body (JSON):
  - RefreshTokenRequest
    - refreshToken: string (required) - `@NotBlank`
- Response: `ApiResponse<?>` (service returns refreshed token object)

### POST /logout
- Auth required: No (public)
- Purpose: Invalidate a refresh token / logout user.
- Request body (JSON): RefreshTokenRequest
- Response: `ApiResponse<?>` (message)

---

## UserController
Base: `${spring.api.prefix}/user`

### GET /
- Auth required: Yes (Bearer) and Role: ADMIN (`@PreAuthorize("hasRole('ADMIN')")`)
- Purpose: Get paginated list of users.
- Query params: controller reads request parameter map; expected common params:
  - pageNo (default 1)
  - pageSize (default 20)
  - sort (e.g. `field,asc`)
  - keyword
- Response: `ApiResponse<PageResponse<UserResponse>>`
  - PageResponse fields: `pageNo`, `pageSize`, `totalElements`, `totalPages`, `data: UserResponse[]`

---

## TopicController
Base: `${spring.api.prefix}/topics`

### GET /find-all
- Auth required: No (public GET)
- Purpose: Get paginated list of topics.
- Query params:
  - pageNo (default 1)
  - pageSize (default 20)
  - sort (default `id,asc`)
  - keyword (default empty)
- Response: `ApiResponse<PageResponse<TopicResponse>>`
  - TopicResponse fields:
    - id: Long
    - name: string
    - description: string
    - createdAt: LocalDateTime

---

## DictionaryWordController
Base: `${spring.api.prefix}/vocabularies`

### GET /lookup/basic?word={word}
- Auth required: Yes (Bearer)
- Purpose: Quickly lookup a word from DB/dictionary by query param.
- Query params:
  - word (required)
- Response: `ApiResponse<LookupResponse>`
  - LookupResponse fields:
    - dictionaryWordId: Long
    - word: string
    - partOfSpeech: string
    - phonetic: string
    - meaningVi: string
    - explanationVi: string
    - exampleSentence: string
    - audioUrl: string

### POST /lookup/ai
- Auth required: Yes (Bearer)
- Purpose: Lookup using AI (richer explanation/context).
- Request body: LookupRequest
  - word: string (required) - `@NotBlank`
  - contextSentence: string (optional)
- Response: `ApiResponse<LookupResponse>` (same structure as above)

### POST /translate
- Auth required: Yes (Bearer)
- Purpose: Translate a text snippet.
- Request body: TranslateRequest
  - text: string (required) - `@NotBlank`
- Response: `ApiResponse<TranslateResponse>`
  - TranslateResponse fields:
    - originalText: string
    - translatedText: string

### GET /find-all
- Auth required: Yes and Role: ADMIN (`@PreAuthorize("hasRole('ADMIN')")`)
- Purpose: Admin-only listing of dictionary words.
- Query params: pageNo, pageSize, sort, keyword
- Response: `ApiResponse<PageResponse<DictionaryWordResponse>>`
  - DictionaryWordResponse fields:
    - id: Long
    - word: string
    - partOfSpeech: string
    - pronunciation: string
    - meaningVi: string

---

## UserSavedWordController
Base: `${spring.api.prefix}/user-saved-words`

### POST /
- Auth required: Yes (Bearer)
- Purpose: Save a dictionary word to the current user's vocabulary group.
- Request body: UserSaveWordRequest
  - dictionaryWordId: Long (required) - `@NotNull`
  - vocabularyGroupId: Long (required) - `@NotNull`
  - sourceSentence: string (optional)
  - sourceUrl: string (optional)
- Response: `ApiResponse<UserSavedWordResponse>`
  - UserSavedWordResponse fields: `userSavedWordId: Long`

### GET /find-all/{vocabularyGroupId}
- Auth required: Yes
- Purpose: Get paginated saved words for the current user in a vocabulary group.
- Path params: vocabularyGroupId: Long
- Query params: pageNo,pageSize,sort,keyword
- Response: `ApiResponse<PageResponse<WordSavedFindResponse>>`
  - WordSavedFindResponse fields:
    - id: Long
    - userId: UUID
    - dictionaryWordResponse: DictionaryWordResponse
    - sourceUrl: string
    - ankiStatus: string
    - ankiNoteId: Long

### DELETE /{id}
- Auth required: Yes
- Purpose: Delete saved word by id.
- Path params: id: Long
- Response: `ApiResponse<?>` (message)

### POST /sync-anki
- Auth required: Yes
- Purpose: Sync saved words to local Anki via AnkiConnect (local TCP 127.0.0.1:8765)
- Request: none
- Response:
  - On success: `ApiResponse<Map<String,Integer>>` containing `syncedWords` count
  - On Anki connection error: HTTP 400 with `ApiResponse.error` message explaining AnkiConnect not reachable

---

## VocabularyGroupController
Base: `${spring.api.prefix}/vocabulary-groups`

### GET /find-all
- Auth required: Yes
- Purpose: List vocabulary groups for the user (paginated)
- Query params: pageNo,pageSize,sort,keyword
- Response: `ApiResponse<PageResponse<VocabularyGroupResponse>>`
  - VocabularyGroupResponse fields:
    - id: Long
    - userId: UUID
    - name: string
    - isDefault: boolean
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime

### POST /
- Auth required: Yes
- Purpose: Create vocabulary group
- Request body: VocabularyGroupRequest
  - name: string (required) - `@NotBlank`
- Response: `ApiResponse<VocabularyGroupResponse>`

### PUT /{id}
- Auth required: Yes
- Purpose: Update vocabulary group
- Path param: id: Long
- Request body: VocabularyGroupRequest
- Response: `ApiResponse<VocabularyGroupResponse>`

### DELETE /{id}
- Auth required: Yes
- Purpose: Delete vocabulary group
- Path param: id: Long
- Response: `ApiResponse<?>` (message)

---

If you want example request/response JSONs added for each endpoint, I can append them (small examples only).

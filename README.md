## 개요

 #### Yahoo Finance 웹사이트에서 회사별 배당금 데이터를 스크래핑하여 제공하는 RESTful API 서버

## 기술 스택

- Spring Boot
- Spring Security + JWT
- Spring Data JPA (H2 DB)
- Redis
- Jsoup: HTML 파싱을 통한 웹 스크래핑
- Spring Scheduler
  
## 회원

### 공통
 - [x] 회원가입 (POST /auth/signup)
 - [x] JWT 토큰 발행 (POST /auth/signin)
 - [x] 필터 기반 JWT 검증

### 회사 관리
 - [x] 티커 입력으로 회사 등록 (POST /company)
 - [x] 회사명 자동완성 (GET /company/autocomplete?keyword={keyword})
 - [x] 페이징된 회사 목록 조회 (GET /company) [ROLE_READ]
 - [x] 회사 삭제 (DELETE /company/{ticker}) [ROLE_WRITE]

### 배당금 조회
 - [x] 배당금 조회 API (GET /finance/dividend/{companyName})
 - [x] Redis 캐싱 적용
 - [x] 회사 삭제 시 캐시 무효화

### 스케줄링
 - [x] 주기적 배당금 스크래핑 (@Scheduled)
 - [x] 스크래핑 간 3초 지연 처리

# ORE Backend

> “우리는 소비를 줄이자고 말하지 않는다. 대신, 제품을 더 오래, 더 제대로 사용할 수 있도록 돕는다.”

## 프로젝트 소개

**오래(ORE, Own Repair Extend)**는 전자·가전제품을 구매한 뒤 필요한 정보를 반복해서 찾아야 하는 불편함과, 그 과정에서 제품이 너무 이르게 교체되는 문제를 해결하는 제품 생애주기 관리 서비스입니다.

제품을 구매한 순간의 정보와 이미지를 저장하고, 보증 기간·공식 매뉴얼·수리 이력·수리 예정 알림을 한곳에서 관리합니다. 사용자는 필요할 때마다 정보를 다시 검색하는 대신 자신의 제품에 필요한 정보를 바로 확인하고, AI 상담을 통해 관리와 수리에 도움을 받을 수 있습니다.

ORE는 제품의 구매부터 관리, 수리, 재사용까지 이어지는 흐름을 서비스 안에 연결합니다. 단순히 제품 정보를 보관하는 데 그치지 않고, 관리 시점에 필요한 정보를 먼저 꺼내고 수리 이력을 축적해 다음 선택에 활용할 수 있도록 설계합니다. 이를 통해 제품을 더 오래, 더 제대로 사용할 수 있는 환경을 만듭니다.

이 프로젝트는 실제 서비스 출시와 공모전 출품을 목표로 하는 실전형 학습 과정이기도 합니다. 팀원들은 기획부터 설계, 구현, 개선까지의 전 과정을 경험하며 역할별 책임을 바탕으로 개발합니다. 스프린트 운영, 코드 리뷰, 일정 관리 등 협업 방식을 직접 실천해 문제를 스스로 판단하고 해결하는 역량을 키웁니다.

나아가 ORE는 제품 수명 연장을 통해 환경 지속 가능성에 기여합니다. 소비를 제한하자는 메시지보다, 이미 가진 제품을 오래 쓰기 쉽게 만드는 기술적 경험을 제공하며 ESG 가치가 서비스로 이어질 수 있음을 보여주고자 합니다.

## 주요 기능

- Google ID 토큰 기반 로그인과 JWT 액세스/리프레시 토큰 인증
- 제품 등록, 즐겨찾기, 보증 기간 조회 및 이미지 관리
- 제품 사진 업로드를 통한 정보 추출과 공식 매뉴얼 검색
- 수리 이력 및 수리 예정 알림 관리
- 읽음 상태를 포함한 알림 관리
- 제품별 AI 채팅 세션 및 메시지 이력 관리
- Amazon S3를 이용한 이미지 저장

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.0.5, Spring MVC, Spring Security |
| Database | PostgreSQL, Spring Data JPA / Hibernate |
| Build | Gradle Wrapper |
| Authentication | Google OAuth ID Token, JJWT |
| Storage | AWS S3 SDK |
| External service | AI server (기본 `http://localhost:8000`) |

## 로컬 실행 방법

### 1. 사전 준비

- JDK 17
- PostgreSQL 데이터베이스
- Google OAuth 클라이언트 ID
- AWS S3 버킷 및 액세스 키
- 제품 인식·채팅 기능을 사용할 경우 AI 서버 (기본 포트 `8000`)

### 2. 환경 변수 설정

루트에 `.env` 파일을 만들고 아래 값을 설정합니다. `.env`는 Git에서 제외되어 있으므로 실제 비밀 값은 커밋하지 마세요.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/ore
DB_USERNAME=postgres
DB_PASSWORD=your_database_password

GOOGLE_CLIENT_ID=your_google_oauth_client_id
JWT_SECRET=use_a_long_random_secret_key

AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_S3_BUCKET=your_s3_bucket_name
```

`application.yaml`은 위 환경 변수를 사용합니다. AI 서버 주소를 바꾸려면 `ai.server.url`을 원하는 주소로 설정하세요.

### 3. 애플리케이션 실행

Windows에서는 다음 명령으로 실행합니다.

```powershell
.\\gradlew.bat bootRun
```

macOS/Linux에서는 다음 명령을 사용합니다.

```bash
./gradlew bootRun
```

기본 서버 주소는 `http://localhost:8080`입니다.

## 테스트와 빌드

```powershell
.\\gradlew.bat test
.\\gradlew.bat build
```

## 인증

`POST /auth/**`를 제외한 API는 JWT 인증이 필요합니다. 요청 헤더에 액세스 토큰을 포함하세요.

```http
Authorization: Bearer {accessToken}
```

Google 로그인은 리프레시 토큰을 `HttpOnly`, `Secure`, `SameSite=None` 쿠키로 설정합니다. 따라서 로컬 HTTP 환경에서 쿠키 기반 토큰 갱신을 시험할 때는 브라우저의 Secure 쿠키 정책을 고려해야 합니다.

## API 개요

| 도메인 | 엔드포인트 | 설명 |
| --- | --- | --- |
| Auth | `POST /auth/google` | Google ID 토큰으로 로그인 |
| Auth | `POST /auth/refresh` | 리프레시 쿠키로 액세스 토큰 갱신 |
| Auth | `POST /auth/logout` | 로그아웃 및 리프레시 쿠키 제거 |
| User | `GET`, `PATCH /users/me` | 내 정보 조회·수정 |
| Product | `POST`, `GET /products` | 제품 등록·목록 조회 |
| Product | `GET`, `PATCH`, `DELETE /products/{productId}` | 제품 상세 조회·수정·삭제 |
| Product | `PATCH /products/{productId}/favorite` | 즐겨찾기 설정 |
| Product | `GET /products/{productId}/warranty` | 보증 기간 조회 |
| Product image | `POST`, `PATCH`, `DELETE /products/{productId}/image` | 제품 이미지 관리 |
| Product source | `POST /product-sources` | 제품 사진 및 출처 등록·정보 추출 |
| Product source | `PATCH /product-sources/{sourceId}` | 추출된 제품 정보 수정 |
| Manual | `GET /products/{productId}/manual` | 저장된 제품 매뉴얼 조회 |
| Manual | `POST /products/manual/search` | 출처 정보를 기반으로 공식 매뉴얼 검색·저장 |
| Repair history | `POST`, `GET /products/{productId}/repairs` | 수리 이력 등록·조회 |
| Repair history | `GET`, `PATCH`, `DELETE /products/{productId}/repairs/{repairId}` | 수리 이력 상세 조회·수정·삭제 |
| Repair reminder | `POST`, `GET /products/{productId}/repair-reminders` | 제품별 수리 알림 등록·조회 |
| Repair reminder | `GET /repair-reminders` | 전체 수리 알림 조회 |
| Repair reminder | `PATCH`, `DELETE /products/{productId}/repair-reminders/{reminderId}` | 수리 알림 수정·삭제 |
| Repair reminder | `PATCH /products/{productId}/repair-reminders/{reminderId}/done` | 수리 알림 완료 처리 |
| Notification | `GET /notifications`, `GET /notifications/unread-count` | 알림·안 읽은 알림 수 조회 |
| Notification | `PATCH /notifications/{notificationId}/read`, `PATCH /notifications/read-all` | 알림 읽음 처리 |
| Chat | `POST`, `GET /chat/sessions` | AI 채팅 세션 생성·조회 |
| Chat | `POST`, `GET /chat/sessions/{sessionId}/messages` | 채팅 메시지 전송·조회 |

이미지를 전송하는 API는 `multipart/form-data`를 사용하며, 업로드 크기는 파일당 최대 10 MB, 요청당 최대 10 MB입니다.

## 프로젝트 구조

```text
src/main/java/com/ore/backend
├── user/             # 로그인, 토큰, 사용자 정보
├── product/          # 제품 및 보증 기간
├── productImage/     # 제품 이미지
├── productSource/    # 제품 사진 분석용 출처 정보
├── productManual/    # 제품 매뉴얼
├── repairHistory/    # 수리 이력
├── repairReminder/   # 수리 알림
├── notification/     # 서비스 알림
├── chatSession/      # AI 채팅 세션
├── chatMessage/      # AI 채팅 메시지
├── security/         # JWT 필터 및 Spring Security 설정
├── external/         # Google 토큰 검증
└── global/           # 예외 처리, S3, AI 클라이언트, 공통 설정
```

## CORS

현재 아래 Origin의 자격 증명 포함 요청을 허용합니다.

- `http://localhost:5173`
- `http://localhost:3000`
- `https://ore-konkuk.vercel.app`

배포 환경의 프론트엔드 주소가 달라지면 `SecurityConfig`의 허용 Origin도 함께 갱신하세요.

## 배포

GitHub Actions를 통해 Amazon EC2에 배포합니다. [deploy.yml](.github/workflows/deploy.yml)은 `main` 브랜치 푸시 또는 수동 실행 시 애플리케이션을 빌드한 뒤 EC2에 JAR 파일과 환경 변수를 전달하고 `ore` 서비스를 재시작합니다.

배포 시 아래 GitHub Actions Secrets가 필요합니다.

- `EC2_HOST`, `EC2_USERNAME`, `EC2_SSH_KEY`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `GOOGLE_CLIENT_ID`, `JWT_SECRET`
- `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AWS_S3_BUCKET`

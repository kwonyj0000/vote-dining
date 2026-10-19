# 작업 프롬프트 및 결과 정리

이 프로젝트를 만들면서 사용한 프롬프트와 결과를 순서대로 정리한 문서입니다.

---

## 1. CLAUDE.md 초기화

**프롬프트**
> `/init` — 코드베이스를 분석해서 CLAUDE.md 파일 생성

**결과**
- 빈 저장소임을 확인, 최소한의 `CLAUDE.md` 생성

---

## 2. CLAUDE.md 업데이트

**프롬프트**
> CLAUDE.md를 아래 내용으로 업데이트해줘.
> 프로젝트: 팀 메뉴 투표 앱 / 스택: Java JDK 내장 HttpServer, HTML + 바닐라 JavaScript, 인메모리 Map / 빌드 도구 없음. javac/java 명령어로 직접 실행 / 포트: 8080 / 코딩 규칙: 한국어 주석, 영문 변수명

**결과**
- `CLAUDE.md`에 프로젝트 개요, 빌드 명령어, 아키텍처, 코딩 규칙 추가

---

## 3. 기능 명세 문서 작성

**프롬프트**
> `docs/투표기능.md` 파일을 만들어줘. (메뉴 등록·투표·실시간 순위 요구사항, 완료 조건 포함)

**결과**
- `docs/투표기능.md` 생성 (목표, 요구사항, 범위 제외, 완료 조건 체크박스 포함)

---

## 4. 구현 계획 수립

**프롬프트**
> docs/투표기능.md 스펙대로 구현 계획을 세워줘. Server.java 하나와 web/ 폴더 아래 index.html, app.js로 구성해줘.

**결과**
- API 설계 (`GET /api/menus`, `POST /api/menus`, `POST /api/vote/{name}`)
- 핸들러 구성, 데이터 구조(`LinkedHashMap`), 프론트 함수 구성 계획 확정

---

## 5. 초기 구현

**프롬프트**
> (계획 승인 후 구현 실행)

**결과**
- `Server.java` — HttpServer, 정적 파일 서빙, 메뉴 CRUD + 투표 API
- `web/index.html` — 메뉴 입력폼 + 목록 UI
- `web/app.js` — `addMenu()`, `vote()`, `loadMenus()`, 3초 폴링

---

## 6. 메뉴 삭제 기능 추가

**프롬프트**
> 기존에 등록되어있는 메뉴 삭제하는 기능도 추가해줘.

**결과**
- `Server.java` — `DELETE /api/delete/{name}` 핸들러 추가
- `web/app.js` — `deleteMenu()` 함수 추가
- `web/index.html` — 삭제 버튼(빨간 계열) 스타일 추가

---

## 7. 맛집 검색 기능 계획 (1차 — 카카오 API)

**프롬프트**
> 투표결과를 가지고 회사 근처 맛집을 검색하는 기능도 추가하고 싶은데, 구현방법 알려줘. 연동할 서비스는 카카오관련 서비스가 쉬울것 같아.

**결과**
- 카카오 Local API 기반 계획 수립 (주소 → 좌표 → 키워드 검색)
- 사용자 요청으로 방향 변경 → 네이버 딥링크 방식으로 재계획

---

## 8. 맛집 검색 기능 계획 (2차 — 네이버 딥링크)

**프롬프트**
> 네이버 딥링크라는게 있는데 이걸 이용하는 방법으로 다시 계획세워줘.

**결과**
- API 키 불필요, URL 조합만으로 구현하는 방식 채택
- `https://map.naver.com/v5/search/{검색어}` 딥링크 방식 확정

---

## 9. 맛집 검색 기능 구현

**프롬프트**
> 투표 완료할때 검색할 지역을 입력받는걸로 바꿔서 구현해줘.

**결과**
- `web/index.html` — 지역 입력 필드 + "맛집 검색" 버튼 섹션 추가
- `web/app.js` — `openNaverMap()` 추가: 1위 메뉴 + 입력 지역 → 네이버 지도 새 탭
- `Server.java` 수정 없음 (순수 클라이언트 사이드 처리)

---

## 10. README.md 작성

**프롬프트**
> 지금까지 구현한 내용을 정리해서 README.md 파일로 정리해줘.

**결과**
- `README.md` 생성 (실행 방법, 기능 목록, 파일 구성, API 명세, 기술 스택, 제외 범위)

---

## 11. Git 커밋 및 GitHub 저장소 생성

**프롬프트**
> 지금까지 수정사항을 git에 올리고 싶은데 해야하는 순서를 알려줘.
> GitHub에 지금까지 만든 프로젝트를 올릴 새 저장소를 만들어줘.
> 저장소 public으로 바꿔줘

**결과**
- `.gitignore` (`*.class`) 생성
- `gh repo create vote-dining --private` → public으로 변경
- 초기 커밋 & 푸시: https://github.com/kwonyj0000/vote-dining

---

## 12. Railway 배포

**프롬프트**
> GitHub를 통해서 배포하는 방법을 알고싶어.
> 소스 수정 필요한 부분 소스 수정해줘.

**결과**
- `Dockerfile` 생성 (eclipse-temurin:21-jdk-alpine)
- `Server.java` — `PORT` 환경변수로 포트 동적 설정
- 커밋 & 푸시 → Railway 자동 배포 트리거

---

## 13. 배포 오류 해결

**프롬프트**
> Deployment failed during the build process — Railpack failed to prepare the build.

**결과**
- `railway.toml` 추가 (`builder = "dockerfile"`)로 Railpack 대신 Dockerfile 사용 명시
- 커밋 & 푸시 → 빌드 성공

---

## 14. 배포 문서 작성

**프롬프트**
> 지금 배포한 내용을 deployee.md 파일로 정리해줘.

**결과**
- `deployee.md` 생성 (플랫폼, 구성 파일, 환경변수, 배포 순서, 자동 배포 흐름)

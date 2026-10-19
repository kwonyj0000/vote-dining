# 팀 메뉴 투표 앱

팀원이 점심 메뉴를 등록하고 투표한 뒤, 득표 1위 메뉴로 근처 맛집을 검색할 수 있는 PoC 앱입니다.

## 실행 방법

```bash
# 컴파일 (처음 한 번, 또는 Server.java 수정 후)
javac Server.java

# 서버 시작 (포트 8080)
java Server
```

브라우저에서 `http://localhost:8080` 접속

> 서버를 종료하면 투표 데이터가 초기화됩니다 (인메모리 저장).

## 기능

| 기능 | 설명 |
|------|------|
| 메뉴 등록 | 이름을 입력해 투표 목록에 추가 |
| 투표 | 메뉴마다 투표 버튼, 누를 때마다 득표수 +1 |
| 순위 표시 | 득표 높은 순으로 자동 정렬, 3초마다 갱신 |
| 메뉴 삭제 | 등록된 메뉴 삭제 |
| 맛집 검색 | 투표 1위 메뉴 + 입력한 지역으로 네이버 지도 검색 (새 탭) |

## 파일 구성

```
261020_vote_dining/
├── Server.java       # HTTP 서버 + API 핸들러
└── web/
    ├── index.html    # UI
    └── app.js        # API 호출 및 렌더링 로직
```

## API

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/menus` | 전체 메뉴 목록 (득표순 JSON) |
| POST | `/api/menus` | 메뉴 등록 `{"name": "짜장면"}` |
| POST | `/api/vote/{name}` | 득표수 +1 |
| DELETE | `/api/delete/{name}` | 메뉴 삭제 |

## 기술 스택

- **백엔드**: Java JDK 내장 `com.sun.net.httpserver.HttpServer`
- **프론트엔드**: HTML + 바닐라 JavaScript
- **저장소**: 인메모리 `LinkedHashMap`
- **맛집 검색**: 네이버 지도 딥링크 (`https://map.naver.com/v5/search/…`)
- **빌드 도구**: 없음 (`javac` / `java` 직접 실행)

## 제외 범위 (PoC)

- 로그인 및 사용자 인증
- 중복 투표 방지
- 데이터 영속성 (DB 저장)

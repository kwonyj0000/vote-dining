# 구현 계획

---

## 1단계 — 투표 앱 초기 구현

### 파일 구성

```
261020_vote_dining/
├── Server.java        ← HTTP 서버 + API 핸들러
└── web/
    ├── index.html     ← UI 마크업
    └── app.js         ← API 호출 + 렌더링 로직
```

### Server.java

**데이터 구조**
```java
// 메뉴 이름 → 득표수 (등록 순서 보존)
Map<String, Integer> votes = new LinkedHashMap<>();
```

**API 설계**

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/menus` | 전체 메뉴 목록 반환 (votes 내림차순 JSON) |
| POST | `/api/menus` | 메뉴 등록. body: `{"name":"짜장면"}` |
| POST | `/api/vote/{name}` | 해당 메뉴 득표수 +1 |
| DELETE | `/api/delete/{name}` | 메뉴 삭제 |

**응답 형식 (GET /api/menus)**
```json
[{"name":"짜장면","votes":3},{"name":"삼겹살","votes":1}]
```

**핸들러 구성**
- `StaticHandler` — `web/` 디렉토리에서 파일 읽어 Content-Type 포함 응답
- `MenuHandler` — `/api/menus` 경로, GET/POST 분기
- `VoteHandler` — `/api/vote/{name}` 경로, POST 처리
- `DeleteHandler` — `/api/delete/{name}` 경로, DELETE 처리

**주의사항**
- 한글 메뉴명 URL 파라미터: `URLDecoder.decode(name, "UTF-8")` 처리
- 응답 헤더에 `Content-Type: application/json; charset=utf-8` 설정

### web/app.js 함수 구성

| 함수 | 설명 |
|------|------|
| `loadMenus()` | GET /api/menus → 목록 렌더링 (득표순 정렬) |
| `addMenu()` | 입력값 읽어 POST /api/menus → 성공 시 loadMenus() 호출 |
| `vote(name)` | POST /api/vote/{name} → 성공 시 loadMenus() 호출 |
| `deleteMenu(name)` | DELETE /api/delete/{name} → 성공 시 loadMenus() 호출 |

**실시간 갱신**
- 페이지 로드 시 `loadMenus()` 1회 호출
- `setInterval(loadMenus, 3000)` — 3초마다 폴링

### 빌드 & 실행

```bash
javac Server.java
java Server
# 브라우저에서 http://localhost:8080 접속
```

---

## 2단계 — 맛집 검색 기능 (네이버 지도 딥링크)

### 배경

투표 1위 메뉴명 + 사용자가 입력한 지역으로 네이버 지도 검색 URL을 만들어 새 탭으로 여는 방식.
API 키 불필요, 서버 로직 없음 (순수 클라이언트 처리).

### 네이버 지도 딥링크 URL 형식

```
https://map.naver.com/v5/search/{인코딩된_검색어}
```

**검색어 구성:** `{1위 메뉴명} {입력한 지역} 맛집`
- 지역 미입력 시: `{1위 메뉴명} 맛집`

### 구현

**web/index.html 추가**
- 투표 목록 아래 지역 입력 필드 + "맛집 검색" 버튼 섹션

**web/app.js 추가**
```javascript
async function openNaverMap() {
  const menus = await fetch('/api/menus').then(r => r.json());
  if (menus.length === 0) { alert('먼저 메뉴를 등록하고 투표해주세요.'); return; }
  const topMenu = menus[0].name;
  const location = document.getElementById('location-input').value.trim();
  const query = location ? `${topMenu} ${location} 맛집` : `${topMenu} 맛집`;
  window.open(`https://map.naver.com/v5/search/${encodeURIComponent(query)}`, '_blank');
}
```

**Server.java** — 수정 없음

### 검증

1. 메뉴 등록·투표 후 지역 입력 → "맛집 검색" 버튼 클릭
2. 새 탭에서 네이버 지도 검색 결과 확인
3. 메뉴 없을 때 버튼 클릭 → alert 확인
4. 지역 미입력 시 `{메뉴명} 맛집` 으로 검색되는지 확인

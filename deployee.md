# 배포 가이드

## 플랫폼

[Railway](https://railway.app) — GitHub 저장소 연동, push 시 자동 재배포

## 배포 구성 파일

| 파일 | 역할 |
|------|------|
| `Dockerfile` | 빌드 이미지 정의 (eclipse-temurin:21-jdk-alpine) |
| `railway.toml` | Railway 빌더를 Dockerfile로 명시 |

## 환경변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `PORT` | 서버 포트 (Railway가 자동 주입) | `8080` |

## 최초 배포 순서

1. [railway.app](https://railway.app) → GitHub 계정으로 로그인
2. **New Project → Deploy from GitHub repo** → `vote-dining` 선택
3. **Settings → Source → Check for update** 클릭 (GitHub 자동 배포 활성화)
4. **Settings → Networking → Generate Domain** 클릭 → 공개 URL 발급

## 이후 배포 (코드 수정 시)

```bash
git add .
git commit -m "변경 내용"
git push
```

`git push` 후 Railway가 자동으로 빌드 & 재배포합니다. 완료까지 약 1~2분 소요.

## 배포 상태 확인

Railway 대시보드 → **Deployments** 탭 → 빌드 로그 확인

| 상태 | 의미 |
|------|------|
| 🟡 Building | Dockerfile로 빌드 중 |
| 🟢 Active | 배포 성공, 앱 실행 중 |
| 🔴 Failed | 오류 발생, 로그 확인 필요 |

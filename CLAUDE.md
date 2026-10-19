# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

팀 메뉴 투표 앱. Java JDK 내장 `HttpServer`로 백엔드를 구성하고, HTML + 바닐라 JavaScript로 프론트엔드를 제공하며, 데이터는 인메모리 `Map`으로 관리한다.

## Commands

```bash
# 컴파일 (프로젝트 루트에서)
javac Server.java

# 실행 (포트 8080)
java Server
```

빌드 도구 없음. `javac` / `java` 명령어로 직접 실행한다.

## Architecture

- 백엔드: JDK 내장 `com.sun.net.httpserver.HttpServer` (포트 8080)
- 프론트엔드: 정적 HTML + 바닐라 JavaScript (서버가 직접 서빙)
- 저장소: 인메모리 `Map` (서버 재시작 시 초기화)

## Coding Conventions

- 주석은 한국어로 작성
- 변수명·메서드명은 영문(camelCase) 사용

## Commit Rules

- 푸시 전 반드시 `javac Server.java` 컴파일 성공 확인
- 커밋 메시지 형식: `{type}: 한 줄 설명`
  - `feat` — 새 기능
  - `fix` — 버그 수정
  - `docs` — 문서 변경
  - `refactor` — 기능 변경 없는 코드 정리

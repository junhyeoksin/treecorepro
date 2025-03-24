# Tree Core Pro

Java 기반의 트리 구조 데이터를 관리하는 스프링 부트 애플리케이션입니다.

## 개요

Tree Core Pro는 계층적 데이터 구조를 효율적으로 관리하고 조작할 수 있는 API를 제공합니다. 이 프로젝트는 하이버네이트와 JPA를 사용하여 데이터베이스 지속성을 구현하며, RESTful API를 통해 트리 노드를 생성, 읽기, 업데이트 및 삭제하는 기능을 제공합니다.

## 주요 기능

- 계층적 트리 구조 데이터 관리
- 트리 노드 CRUD 작업
- 효율적인 트리 탐색 및 조작
- 계층적 데이터 쿼리

## 기술 스택

- Java
- Spring Boot
- Hibernate/JPA
- H2 데이터베이스 (개발용)
- Gradle

## 시작하기

### 요구사항

- JDK 11 이상
- Gradle 7.x 이상

### 설치 및 실행

1. 저장소 클론하기
   ```bash
   git clone https://github.com/junhyeoksin/treecorepro.git
   cd treecorepro
   ```

2. 애플리케이션 빌드
   ```bash
   ./gradlew build
   ```

3. 애플리케이션 실행
   ```bash
   ./gradlew bootRun
   ```

4. 애플리케이션 접속
   - API 엔드포인트: http://localhost:8080
   - H2 콘솔: http://localhost:8080/h2-console

## API 문서

주요 API 엔드포인트:

- `GET /api/treeNode`: 모든 트리 노드 조회
- `GET /api/treeNode/{id}`: 특정 ID의 트리 노드 조회
- `POST /api/treeNode`: 새 트리 노드 생성
- `PUT /api/treeNode/{id}`: 특정 ID의 트리 노드 업데이트
- `DELETE /api/treeNode/{id}`: 특정 ID의 트리 노드 삭제

## 개발 및 디버깅

프로젝트 실행 중에는 다음과 같은 도구를 사용할 수 있습니다:

- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:keystrom)
- 로그는 기본적으로 콘솔에 출력됩니다.

## 기여

버그 리포트, 기능 요청, 풀 리퀘스트는 환영합니다.

## 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다. 
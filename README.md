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

## Nested Set Model 활용

이 프로젝트는 계층적 데이터를 효율적으로 저장하고 조회하기 위해 Nested Set Model을 사용합니다:

### Nested Set Model이란?

Nested Set Model은 계층 구조 데이터를 관계형 데이터베이스에 효율적으로 저장하기 위한 기법입니다. 각 노드는 `left`와 `right` 값을 가지며, 이 값들로 노드 간의 계층 관계를 표현합니다.

### 구현 방식

`TreeNode` 엔티티에서 Nested Set Model을 구현하는 주요 필드들:

```java
@Column(name = "c_left")
private Long c_left;

@Column(name = "c_right")
private Long c_right;

@Column(name = "c_level")
private Integer c_level;

@Column(name = "c_parentid")
private Long c_parentid;
```

### Nested Set Model의 장점

1. **효율적인 하위 트리 검색**: 특정 노드의 모든 하위 노드를 단일 쿼리로 검색 가능
   ```sql
   SELECT * FROM tree_node WHERE c_left > node.c_left AND c_right < node.c_right
   ```

2. **계층 구조 보존**: left와 right 값으로 트리의 구조를 정확히 표현

3. **경로 탐색 최적화**: 특정 노드에서 루트까지의 경로를 효율적으로 찾기 가능
   ```sql
   SELECT * FROM tree_node WHERE c_left < node.c_left AND c_right > node.c_right
   ```

4. **순서 유지**: 트리 내 노드의 순서가 자연스럽게 유지됨

### 구현 특징

- **부모 ID 추가 사용**: 직접적인 부모 접근을 위한 `c_parentid` 필드 활용
- **레벨 정보 저장**: 노드 깊이를 직접 저장하여 트리 구조 파악 용이
- **트랜잭션 관리**: 노드 조작 시 데이터 일관성 유지를 위한 트랜잭션 처리
- **캐싱 전략**: 자주 접근하는 트리 구조에 대한 캐싱으로 성능 최적화

## Hibernate 및 JPA 활용

이 프로젝트는 다양한 방식으로 Hibernate와 JPA를 활용합니다:

### 1. 엔티티 정의 (JPA 애노테이션 사용)
`TreeNode.java` 클래스는 JPA 애노테이션을 사용하여 데이터베이스 테이블과 매핑됩니다:
```java
@Entity
@Table(name = "tree_node")
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TreeNode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TreeNodeSequence")
    @Column(name = "c_id")
    private Long c_id;
    
    // 다른 필드들...
}
```

### 2. Hibernate 고유 기능 활용
프로젝트는 Hibernate 고유 애노테이션도 사용합니다:
- `@DynamicUpdate`: 변경된 필드만 업데이트
- `@DynamicInsert`: NULL이 아닌 필드만 삽입
- `@Cache`: 두 번째 레벨 캐시 활성화

### 3. Hibernate 설정
`HibernateConfig` 클래스는 다음과 같은 설정을 담당합니다:
- SessionFactory 설정
- 트랜잭션 관리
- Hibernate 속성 구성
- 캐시 설정
- 쿼리 인터셉터 구성

### 4. DAO 레이어 (데이터 접근 계층)
`AbstractHibernateDao` 클래스는 Hibernate 기반 데이터 접근 패턴을 구현합니다:
- Hibernate의 `Session`을 사용한 CRUD 작업
- JPA의 Criteria API를 활용한 타입 안전 쿼리
- 대량 업데이트, 일괄 삽입 등의 고급 기능 지원

### 5. 캐싱 전략
성능 향상을 위해 Hibernate의 캐싱 기능을 활용합니다:
- EhCache를 캐시 제공자로 사용
- 2차 캐시 및 쿼리 캐시 활성화
- 자주 액세스하는 엔티티와 쿼리 결과 캐싱

### 6. 계층적 데이터 관리
트리 구조 데이터를 효율적으로 관리하기 위한 필드 설계:
```java
@Column(name = "c_parentid")
private Long c_parentid;

@Column(name = "c_left")
private Long c_left;

@Column(name = "c_right")
private Long c_right;

@Column(name = "c_level")
private Integer c_level;
```

### 7. application.yml 설정
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region:
            factory_class: org.hibernate.cache.ehcache.EhCacheRegionFactory
```

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

## 사용 방법

Tree Core Pro는 RESTful API를 통해 트리 구조 데이터를 관리합니다. 다음은 주요 기능별 사용 방법입니다.

### 1. 트리 노드 기본 작업

#### 루트 노드 생성
```bash
# 루트 노드 생성
curl -X POST "http://localhost:8080/api/nodes/root?title=루트노드" -H "Content-Type: application/json"
```

#### 노드 조회
```bash
# 모든 노드 조회
curl -X GET "http://localhost:8080/api/nodes"

# 특정 ID의 노드 조회
curl -X GET "http://localhost:8080/api/nodes/1"
```

#### 노드 생성 및 수정
```bash
# 새 노드 생성
curl -X POST "http://localhost:8080/api/nodes" -H "Content-Type: application/json" -d '{"c_title":"새 노드", "c_type":"default"}'

# 특정 노드 업데이트
curl -X PUT "http://localhost:8080/api/nodes/2" -H "Content-Type: application/json" -d '{"c_title":"업데이트된 노드"}'
```

#### 노드 삭제
```bash
# 노드 삭제
curl -X DELETE "http://localhost:8080/api/nodes/3"
```

### 2. 계층 구조 관리

#### 자식 노드 작업
```bash
# 자식 노드 조회
curl -X GET "http://localhost:8080/api/nodes/children/1"

# 자식 노드 추가
curl -X POST "http://localhost:8080/api/nodes/children/1" -H "Content-Type: application/json" -d '{"c_title":"자식 노드", "c_type":"default"}'
```

#### 트리 구조 조작
```bash
# 노드 이동
curl -X PUT "http://localhost:8080/api/nodes/2/move?newParentId=4&position=last"

# 노드 복사
curl -X POST "http://localhost:8080/api/nodes/2/copy?targetParentId=1&position=first"
```

### 3. 트리 탐색

#### 자손 및 조상 조회
```bash
# 노드의 모든 자손 조회
curl -X GET "http://localhost:8080/api/nodes/1/descendants"

# 노드의 모든 조상 조회
curl -X GET "http://localhost:8080/api/nodes/5/ancestors"
```

#### 검색 기능
```bash
# 제목으로 노드 검색
curl -X GET "http://localhost:8080/api/nodes/search?title=검색어"
```

### 4. 응답 형식

모든 API 응답은 JSON 형식으로 제공됩니다. 노드 객체의 기본 구조는 다음과 같습니다:

```json
{
  "c_id": 1,
  "c_parentid": 0,
  "c_position": "0",
  "c_left": 1,
  "c_right": 2,
  "c_level": 0,
  "c_title": "루트 노드",
  "c_type": "default",
  "c_insdate": "2023-01-01T00:00:00"
}
```

### 5. H2 콘솔 사용

개발 환경에서는 H2 인메모리 데이터베이스를 사용합니다. 다음 단계를 통해 데이터베이스를 직접 확인할 수 있습니다:

1. 브라우저에서 http://localhost:8080/h2-console 접속
2. JDBC URL: `jdbc:h2:mem:keystrom` 입력
3. 사용자명: `sa` (비밀번호는 비워둠)
4. '연결' 버튼 클릭

### 6. 오류 처리

API 응답은 표준 HTTP 상태 코드를 사용합니다:
- 200: 성공
- 201: 리소스 생성 성공
- 400: 잘못된 요청
- 404: 리소스를 찾을 수 없음
- 500: 서버 오류

## API 문서

주요 API 엔드포인트:

- `GET /api/nodes`: 모든 트리 노드 조회
- `GET /api/nodes/{id}`: 특정 ID의 트리 노드 조회
- `POST /api/nodes`: 새 트리 노드 생성
- `PUT /api/nodes/{id}`: 특정 ID의 트리 노드 업데이트
- `DELETE /api/nodes/{id}`: 특정 ID의 트리 노드 삭제
- `GET /api/nodes/children/{parentId}`: 특정 부모 노드의 자식 노드 조회
- `POST /api/nodes/children/{parentId}`: 특정 부모 노드에 자식 노드 추가
- `GET /api/nodes/search`: 제목으로 노드 검색
- `POST /api/nodes/root`: 루트 노드 생성
- `PUT /api/nodes/{id}/move`: 노드 이동
- `POST /api/nodes/{id}/copy`: 노드 복사
- `GET /api/nodes/{id}/descendants`: 특정 노드의 모든 자손 조회
- `GET /api/nodes/{id}/ancestors`: 특정 노드의 모든 조상 조회

## 개발 및 디버깅

프로젝트 실행 중에는 다음과 같은 도구를 사용할 수 있습니다:

- H2 콘솔: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:keystrom)
- 로그는 기본적으로 콘솔에 출력됩니다.

## 기여

버그 리포트, 기능 요청, 풀 리퀘스트는 환영합니다.

## 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다. 
# OpenTelemetry 로깅 시스템 예제

이 프로젝트는 OpenTelemetry를 활용한 통합 로깅 시스템의 예제입니다. Kotlin + Spring Boot 기반의 마이크로서비스에서 생성되는 로그를 수집하고, OpenSearch를 통해 저장 및 분석할 수 있습니다.

## 🏗️ 아키텍처

```
[Spring Boot App] → [OpenTelemetry Java Agent] → [OpenTelemetry Collector] → [OpenSearch] ← [OpenSearch Dashboards]
                                                          ↗
[Docker Containers] → [Fluent Bit] ----------------------
```

## 🛠️ 기술 스택

### 최신 기술 스택 (2025년 8월 기준)

- **OpenTelemetry Java Agent 2.6.0**: 자동 계측 (코드 변경 최소화)
- **OpenTelemetry Collector 0.105.0**: OTLP 프로토콜 기반 데이터 수집
- **OpenSearch 2.15.0**: Elasticsearch의 Apache 2.0 라이센스 대안
- **OpenSearch Dashboards**: Kibana의 오픈소스 대안
- **Fluent Bit 3.1.4**: 경량 로그 수집기 (Cloud Native 트렌드)

### 애플리케이션 스택

- **Kotlin 2.1.0**: 최신 Kotlin 버전
- **Spring Boot 3.4.1**: 최신 Spring Boot 버전
- **Java 21 LTS + Virtual Threads**: Long Term Support 버전 + 최신 성능 최적화
- **Gradle 8.5**: 빌드 도구

## 🚀 빠른 시작

### 1. 저장소 클론 및 시작

```bash
# 1. 애플리케이션 빌드
./gradlew clean build -x test

# 2. Docker 컨테이너 시작
docker-compose up -d

# 3. 서비스 상태 확인
docker-compose ps
```

## 📊 서비스 접속 정보

| 서비스                | URL                                    | 설명                  |
| --------------------- | -------------------------------------- | --------------------- |
| User Service API      | http://localhost:28080/api/v1/users    | REST API 엔드포인트   |
| OpenSearch            | http://localhost:29200                 | 검색 엔진             |
| OpenSearch Dashboards | http://localhost:25601                 | 로그 분석 대시보드    |
| Actuator Health       | http://localhost:28080/actuator/health | 애플리케이션 헬스체크 |
| OTel Collector Health | http://localhost:23133/health          | 컬렉터 상태           |

> **📌 포트 정보**: 다른 프로젝트와의 충돌을 방지하기 위해 모든 포트를 2xxxx 범위로 설정했습니다.

## 🧪 API 테스트

### 1. 사용자 목록 조회

```bash
curl http://localhost:28080/api/v1/users
```

### 2. 사용자 생성

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","fullName":"John Doe"}' \
  http://localhost:28080/api/v1/users
```

### 3. 특정 사용자 조회

```bash
curl http://localhost:28080/api/v1/users/1
```

### 4. 에러 시뮬레이션 (로그 생성 테스트)

```bash
curl http://localhost:28080/api/v1/users/error
```

## 📝 로그 모니터링

### 실시간 로그 확인

```bash
# 애플리케이션 로그
docker-compose logs -f user-service

# OpenTelemetry Collector 로그
docker-compose logs -f otel-collector

# Fluent Bit 로그
docker-compose logs -f fluent-bit

# 모든 서비스 로그
docker-compose logs -f
```

### OpenSearch Dashboards에서 로그 분석

1. **http://localhost:25601** 접속
2. **Management > Dev Tools** 이동
3. 다음 쿼리로 로그 검색:

```json
GET logs/_search
{
  "query": {
    "match": {
      "message": "사용자"
    }
  },
  "sort": [
    {
      "@timestamp": {
        "order": "desc"
      }
    }
  ]
}
```

## 📋 로깅 특징

### 1. 구조화된 로깅

- **JSON 형식**: 파싱이 쉬운 구조화된 로그
- **MDC (Mapped Diagnostic Context)**: 요청별 컨텍스트 정보
- **트레이스/스팬 ID**: 분산 트레이싱 연관 정보

### 2. OpenTelemetry 통합

- **자동 계측**: Java Agent를 통한 무침입 계측
- **수동 계측**: `@WithSpan` 어노테이션을 통한 세밀한 제어
- **OTLP 프로토콜**: 표준 텔레메트리 프로토콜 사용

### 3. 로그 레벨별 분류

- **INFO**: 정상적인 비즈니스 플로우
- **WARN**: 주의가 필요한 상황
- **ERROR**: 오류 상황 및 예외
- **DEBUG**: 상세한 디버깅 정보

## 🛠️ 설정 파일 설명

### Docker Compose (`docker-compose.yml`)

- OpenSearch, OpenSearch Dashboards, OpenTelemetry Collector, Fluent Bit, User Service 정의
- 네트워크 및 볼륨 설정
- 서비스 간 의존성 관리

### OpenTelemetry Collector (`otel-collector/config.yml`)

- OTLP 프로토콜 리시버 설정
- OpenSearch 익스포터 설정
- 배치 처리 및 메모리 최적화

### Fluent Bit (`fluent-bit/fluent-bit.conf`)

- 컨테이너 로그 수집 설정
- OpenSearch로 로그 전송 설정
- 로그 파싱 및 필터링 규칙

### Spring Boot (`application.yml`, `logback-spring.xml`)

- 프로파일별 로깅 설정
- JSON 형식 로그 출력
- OpenTelemetry MDC 통합

## 🔧 트러블슈팅

### OpenSearch 메모리 부족

```bash
# Docker Desktop 메모리 할당량 증가 (최소 4GB 권장)
# 또는 OpenSearch 메모리 설정 조정
```

### 포트 충돌

```bash
# 사용 중인 포트 확인
lsof -i :29200,25601,28080,24317

# 충돌하는 프로세스 종료 후 재시작
```

### 컨테이너 상태 확인

```bash
# 모든 컨테이너 상태 확인
docker-compose ps

# 특정 서비스 로그 확인
docker-compose logs [service-name]
```

## 📚 추가 학습 자료

- [OpenTelemetry 공식 문서](https://opentelemetry.io/)
- [OpenSearch 가이드](https://opensearch.org/docs/)
- [Spring Boot 3.x + OpenTelemetry](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)
- [Fluent Bit 설정 가이드](https://docs.fluentbit.io/)

## 🆕 2025년 최신 기능

### Java 21 Virtual Threads

- **향상된 성능**: 경량 스레드를 통한 동시성 처리 개선
- **메모리 효율성**: 기존 Thread Pool 대비 메모리 사용량 감소
- **자동 활성화**: `spring.threads.virtual.enabled=true` 설정

### 최신 버전 업그레이드

```yaml
# 2025년 8월 기준 최신 버전
OpenTelemetry: 1.40.0
OpenSearch: 2.15.0
Fluent Bit: 3.1.4
OTel Collector: 0.105.0
```

## 🚀 다음 단계

이 로깅 시스템을 기반으로 다음을 확장할 수 있습니다:

1. **메트릭 수집**: Prometheus + Grafana 통합
2. **알림 시스템**: OpenSearch Watcher 또는 AlertManager 연동
3. **마이크로서비스 확장**: 추가 서비스 모듈 생성 및 연동
4. **보안 강화**: 인증/인가 및 로그 암호화 적용
5. **AI 기반 분석**: 이상 탐지 및 예측 모니터링

## 🤝 기여하기

이 프로젝트는 학습 목적으로 만들어졌습니다. 개선사항이나 버그를 발견하시면 이슈를 등록해주세요!

## 🏷️ 버전 정보

- **버전**: 1.0.0
- **업데이트**: 2025년 8월
- **호환성**: Java 21, Spring Boot 3.4.1, Kotlin 2.1.0

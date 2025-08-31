# OpenTelemetry Kotlin Multi-Module 예제 프로젝트

> **2025년 최신 트렌드를 반영한 OpenTelemetry 기반 로깅 시스템 학습 프로젝트**

## 🎯 프로젝트 목적

이 프로젝트는 **OpenTelemetry를 활용한 통합 옵저버빌리티 시스템**을 학습하기 위한 예제입니다.
Kotlin + Spring Boot 기반의 마이크로서비스에서 생성되는 **로그, 메트릭, 트레이스를 수집하고 분석**하는
현대적인 모니터링 시스템을 실습할 수 있습니다.

## 🏗️ 핵심 기술 스택

- **OpenTelemetry** - 벤더 중립적 옵저버빌리티 표준
- **OpenSearch** - 로그 저장 및 검색 엔진
- **Spring Boot 3.4.1** + **Kotlin 2.1.0** + **Java 21 Virtual Threads**
- **Docker Compose** - 통합 개발 환경

## 🚀 빠른 시작

```bash
# 서비스 접속
# - 애플리케이션: http://localhost:28080/api/v1/users
# - 로그 대시보드: http://localhost:25601
```

## 📚 상세 문서

| 문서                                                 | 설명                                  |
| ---------------------------------------------------- | ------------------------------------- |
| **[📊 observability.md](docs/observability.md)**     | OpenTelemetry 로깅 시스템 구축 가이드 |
| **[📦 version-catalog.md](docs/version-catalog.md)** | Gradle Version Catalog 활용법         |

## 🎓 학습 포인트

- ✅ **자동 계측**: OpenTelemetry Java Agent를 통한 무침입 모니터링
- ✅ **구조화된 로깅**: JSON 형식 + MDC 컨텍스트 + 분산 트레이싱
- ✅ **최신 기술**: 2025년 트렌드를 반영한 기술 스택
- ✅ **컨테이너 환경**: Docker Compose를 통한 완전한 개발 환경
- ✅ **실습 중심**: 실제 API 호출과 로그 분석 체험

## 🔧 개발 환경

- **Java 21 LTS** (Virtual Threads 지원)
- **Kotlin 2.1.0**
- **Gradle 8.5** with Version Catalog
- **Docker & Docker Compose**

---

> **💡 참고**: 포트 충돌 방지를 위해 모든 서비스는 2xxxx 포트 범위를 사용합니다.  
> 자세한 내용은 [observability.md](docs/observability.md)를 참고하세요.

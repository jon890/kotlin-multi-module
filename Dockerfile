# Build stage
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY --chown=gradle:gradle . .

# Build the application
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# OpenTelemetry Java Agent 다운로드
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.1.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 애플리케이션 JAR 복사
COPY --from=builder /app/user-service/build/libs/user-service.jar /app/app.jar

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

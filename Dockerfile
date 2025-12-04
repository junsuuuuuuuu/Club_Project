# 1단계: 빌드 환경 (Build Stage)
FROM gradle:jdk17-alpine AS builder
WORKDIR /app
# 빌드에 필요한 모든 파일을 컨테이너로 복사
COPY . /app
# Gradle을 사용하여 Spring Boot 앱의 JAR 파일을 빌드합니다.
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 환경 (Run Stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 빌드된 JAR 파일을 실행 환경으로 복사합니다.
# 주의: Spring Boot 버전이나 빌드 설정에 따라 jar 파일명이 다를 수 있습니다.
COPY --from=builder /app/build/libs/*.jar app.jar
# Spring Boot 기본 포트 노출
EXPOSE 8080
# 앱 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM gradle:8.7-jdk21-alpine AS build

WORKDIR /app

# 1) Gradle 빌드 파일만 먼저 복사
COPY build.gradle settings.gradle gradle.properties /app/
COPY gradle /app/gradle

# 2) 의존성 다운로드 / 캐시 가능
RUN gradle clean build -x test --no-daemon --parallel || true

# 3) 소스 전체 복사
COPY src /app/src

# 4) 실제 빌드
RUN gradle clean build -x test --no-daemon --parallel

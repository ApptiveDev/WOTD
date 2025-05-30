## 1단계: 빌드 스테이지
#FROM gradle:8.7-jdk21-alpine AS build
#WORKDIR /app
#
## Gradle 캐시 최적화를 위해 먼저 build 파일 복사
#COPY build.gradle settings.gradle /app/
#COPY gradle /app/gradle
#
## 의존성 다운로드
#RUN gradle build -x test
#
## 전체 복사
#COPY . /app
#
## 빌드 실행
#RUN gradle build -x test --no-daemon --parallel
#
## 2단계: 런타임 스테이지 (더 작고 가벼움)
#FROM openjdk:21-slim
#WORKDIR /app
#
## 빌드된 jar 복사
#COPY --from=build /app/build/libs/apptive_3team-0.0.1-SNAPSHOT.jar app.jar
#
## 실행
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY app.jar app.jar

CMD ["java", "-jar", "app.jar"]

# 🔨 1단계: 빌드 스테이지 (Gradle 포함)
FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test

# ▶️ 2단계: 실행 스테이지 (슬림한 런타임 이미지)
FROM amazoncorretto:17

# 실행 환경 변수
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

WORKDIR /app

# 빌드된 JAR 파일만 복사
COPY --from=builder /build/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

CMD java $JVM_OPTS -jar app.jar
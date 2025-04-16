FROM gradle:8.4.0-jdk17 AS builder

WORKDIR /build
COPY --chown=gradle:gradle . .
RUN gradle clean build -x test

FROM amazoncorretto:17

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

WORKDIR /app

COPY --from=builder /build/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

CMD java $JVM_OPTS -jar app.jar
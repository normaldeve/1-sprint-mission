# Amazon Corretto 17 이미지 사용
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사
COPY . .

# 실행에 필요한 환경 변수 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

RUN ./gradlew clean build -x test

# 80 포트 노출
EXPOSE 80

# 애플리케이션 실행
CMD java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar
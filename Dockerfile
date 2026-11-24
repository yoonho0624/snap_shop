# ========== 1단계: 빌드용 ==========
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 설정 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 줄바꿈 문자 정리 + 실행 권한 부여 + 의존성 미리 다운로드
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ========== 2단계: 실행용 ==========
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV TZ=Asia/Seoul

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# 1) Build Stage
FROM gradle:8.8-jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# 2) Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 산출물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 컨테이너 타임존 (대한민국)
ENV TZ=Asia/Seoul

# 애플리케이션 포트
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

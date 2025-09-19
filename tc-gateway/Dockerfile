# 使用官方的 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将 Maven 构建的 JAR 文件复制到容器中
COPY target/tc-gateway.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]

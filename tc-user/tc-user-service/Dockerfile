# Dockerfile for TC User Service
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY tc-user/tc-user-service/pom.xml tc-user/tc-user-service/

# 复制源代码
COPY tc-user/tc-user-service/src tc-user/tc-user-service/src

# 安装Maven并构建项目
RUN apt-get update && \
    apt-get install -y maven && \
    cd tc-user/tc-user-service && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=docker"

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar tc-user/tc-user-service/target/tc-user-service.jar"]

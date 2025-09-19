@echo off
echo ========================================
echo TC用户服务系统启动脚本
echo ========================================

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请安装JDK 17+
    pause
    exit /b 1
)

echo.
echo 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请安装Maven 3.6+
    pause
    exit /b 1
)

echo.
echo 开始编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo 编译成功！开始启动服务...
echo.
echo 服务将在以下地址启动:
echo - 服务地址: http://localhost:8080/tc-user-service
echo - API文档: http://localhost:8080/tc-user-service/swagger-ui.html
echo - 健康检查: http://localhost:8080/tc-user-service/actuator/health
echo.
echo 按 Ctrl+C 停止服务
echo ========================================

mvn spring-boot:run

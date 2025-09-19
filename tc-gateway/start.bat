@echo off
echo Building tc-gateway...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo Build failed.
    pause
    exit /b %errorlevel%
)

echo Running tc-gateway...
java -jar target/tc-gateway.jar
pause

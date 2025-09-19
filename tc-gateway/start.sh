#!/bin/bash
echo "Building tc-gateway..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed."
    exit 1
fi

echo "Running tc-gateway..."
java -jar target/tc-gateway.jar

#!/bin/bash

# Check Java version
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "${JAVA_VERSION}" < "17" ]]; then
    echo "Java version must be 17 or higher. Current version: ${JAVA_VERSION}"
    exit 1
fi

# Check Maven
if ! command -v ./mvnw &> /dev/null && ! command -v mvn &> /dev/null; then
    echo "Maven is not installed and Maven wrapper is not available."
    exit 1
fi

echo "Building eBay SOAP Service..."
if command -v ./mvnw &> /dev/null; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the error messages above."
    exit 1
fi

echo "Starting eBay SOAP Service..."
java -jar target/ebay-clone-soap-1.0.0.jar

if [ $? -ne 0 ]; then
    echo "Failed to start the service. Please check the error messages above."
    exit 1
fi

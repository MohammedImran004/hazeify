#!/bin/bash

echo "========================================"
echo "Hazeify Hospital Management System"
echo "========================================"
echo

echo "Checking prerequisites..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo "✓ Java and Maven are installed"
echo

echo "========================================"
echo "Database Setup"
echo "========================================"
echo
echo "Please ensure you have MySQL running and create a database named 'hazeify'"
echo
echo "SQL Command:"
echo "CREATE DATABASE hazeify;"
echo

echo "========================================"
echo "Configuration"
echo "========================================"
echo
echo "Please update the database configuration in:"
echo "src/main/resources/application.properties"
echo
echo "Update these lines with your MySQL credentials:"
echo "spring.datasource.username=your_username"
echo "spring.datasource.password=your_password"
echo

echo "========================================"
echo "Building Project"
echo "========================================"
echo
echo "Building the project with Maven..."
mvn clean install

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo "✓ Build successful"
echo

echo "========================================"
echo "Starting Application"
echo "========================================"
echo
echo "Starting Hazeify Hospital Management System..."
echo
echo "The application will be available at: http://localhost:8080"
echo
echo "Test Credentials:"
echo "Admin: admin / admin123"
echo "Patient: john.doe / patient123"
echo "Patient: jane.smith / patient123"
echo
echo "Press Ctrl+C to stop the application"
echo

mvn spring-boot:run 
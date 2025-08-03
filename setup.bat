@echo off
echo ========================================
echo Hazeify Hospital Management System
echo ========================================
echo.

echo Checking prerequisites...

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    pause
    exit /b 1
)

echo ✓ Java and Maven are installed
echo.

echo ========================================
echo Database Setup
echo ========================================
echo.
echo Please ensure you have MySQL running and create a database named 'hazeify'
echo.
echo SQL Command:
echo CREATE DATABASE hazeify;
echo.

echo ========================================
echo Configuration
echo ========================================
echo.
echo Please update the database configuration in:
echo src/main/resources/application.properties
echo.
echo Update these lines with your MySQL credentials:
echo spring.datasource.username=your_username
echo spring.datasource.password=your_password
echo.

echo ========================================
echo Building Project
echo ========================================
echo.
echo Building the project with Maven...
mvn clean install

if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo ✓ Build successful
echo.

echo ========================================
echo Starting Application
echo ========================================
echo.
echo Starting Hazeify Hospital Management System...
echo.
echo The application will be available at: http://localhost:8080
echo.
echo Test Credentials:
echo Admin: admin / admin123
echo Patient: john.doe / patient123
echo Patient: jane.smith / patient123
echo.
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run

pause 
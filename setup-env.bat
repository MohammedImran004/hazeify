@echo off
echo ========================================
echo Hazeify Environment Setup Script
echo ========================================
echo.

echo Please enter your MySQL database credentials:
echo.

set /p DB_USERNAME="MySQL Username (default: root): "
if "%DB_USERNAME%"=="" set DB_USERNAME=root

set /p DB_PASSWORD="MySQL Password: "
if "%DB_PASSWORD%"=="" (
    echo Error: MySQL password is required!
    pause
    exit /b 1
)

echo.
echo Please enter your JWT secret key (or press Enter for default):
set /p JWT_SECRET="JWT Secret Key: "
if "%JWT_SECRET%"=="" set JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

echo.
echo Setting environment variables...

set DB_URL=jdbc:mysql://localhost:3306/hazeify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
set JWT_EXPIRATION=86400000
set JWT_REFRESH_EXPIRATION=604800000
set APP_NAME=hazeify
set APP_PORT=8080

echo.
echo Environment variables set successfully!
echo.
echo Database URL: %DB_URL%
echo Database Username: %DB_USERNAME%
echo Database Password: [HIDDEN]
echo JWT Secret: [HIDDEN]
echo JWT Expiration: %JWT_EXPIRATION%
echo JWT Refresh Expiration: %JWT_REFRESH_EXPIRATION%
echo App Name: %APP_NAME%
echo App Port: %APP_PORT%
echo.
echo You can now run the application with: mvn spring-boot:run
echo.
pause 
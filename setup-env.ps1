Write-Host "========================================" -ForegroundColor Green
Write-Host "Hazeify Environment Setup Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Please enter your MySQL database credentials:" -ForegroundColor Yellow
Write-Host ""

$DB_USERNAME = Read-Host "MySQL Username (default: root)"
if ([string]::IsNullOrEmpty($DB_USERNAME)) {
    $DB_USERNAME = "root"
}

$DB_PASSWORD = Read-Host "MySQL Password" -AsSecureString
$DB_PASSWORD_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($DB_PASSWORD))

if ([string]::IsNullOrEmpty($DB_PASSWORD_PLAIN)) {
    Write-Host "Error: MySQL password is required!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Please enter your JWT secret key (or press Enter for default):" -ForegroundColor Yellow
$JWT_SECRET = Read-Host "JWT Secret Key"
if ([string]::IsNullOrEmpty($JWT_SECRET)) {
    $JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
}

Write-Host ""
Write-Host "Setting environment variables..." -ForegroundColor Yellow

$env:DB_URL = "jdbc:mysql://localhost:3306/hazeify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME = $DB_USERNAME
$env:DB_PASSWORD = $DB_PASSWORD_PLAIN
$env:JWT_SECRET_KEY = $JWT_SECRET
$env:JWT_EXPIRATION = "86400000"
$env:JWT_REFRESH_EXPIRATION = "604800000"
$env:APP_NAME = "hazeify"
$env:APP_PORT = "8080"

Write-Host ""
Write-Host "Environment variables set successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Database URL: $env:DB_URL" -ForegroundColor Cyan
Write-Host "Database Username: $env:DB_USERNAME" -ForegroundColor Cyan
Write-Host "Database Password: [HIDDEN]" -ForegroundColor Cyan
Write-Host "JWT Secret: [HIDDEN]" -ForegroundColor Cyan
Write-Host "JWT Expiration: $env:JWT_EXPIRATION" -ForegroundColor Cyan
Write-Host "JWT Refresh Expiration: $env:JWT_REFRESH_EXPIRATION" -ForegroundColor Cyan
Write-Host "App Name: $env:APP_NAME" -ForegroundColor Cyan
Write-Host "App Port: $env:APP_PORT" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can now run the application with: mvn spring-boot:run" -ForegroundColor Green
Write-Host ""

$SAVE_ENV = Read-Host "Do you want to save these variables to a .env file? (y/n)"
if ($SAVE_ENV -eq "y" -or $SAVE_ENV -eq "Y") {
    $envContent = @"
# Database Configuration
DB_URL=$env:DB_URL
DB_USERNAME=$env:DB_USERNAME
DB_PASSWORD=$env:DB_PASSWORD

# JWT Configuration
JWT_SECRET_KEY=$env:JWT_SECRET_KEY
JWT_EXPIRATION=$env:JWT_EXPIRATION
JWT_REFRESH_EXPIRATION=$env:JWT_REFRESH_EXPIRATION

# Application Configuration
APP_NAME=$env:APP_NAME
APP_PORT=$env:APP_PORT
"@
    
    $envContent | Out-File -FilePath ".env" -Encoding UTF8
    Write-Host ".env file created successfully!" -ForegroundColor Green
    Write-Host "Note: Make sure .env is in your .gitignore to avoid committing sensitive data." -ForegroundColor Yellow
}

Read-Host "Press Enter to exit" 
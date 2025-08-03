#!/bin/bash

echo "========================================"
echo "Hazeify Environment Setup Script"
echo "========================================"
echo

echo "Please enter your MySQL database credentials:"
echo

read -p "MySQL Username (default: root): " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-root}

read -s -p "MySQL Password: " DB_PASSWORD
echo
if [ -z "$DB_PASSWORD" ]; then
    echo "Error: MySQL password is required!"
    exit 1
fi

echo
echo "Please enter your JWT secret key (or press Enter for default):"
read -p "JWT Secret Key: " JWT_SECRET
JWT_SECRET=${JWT_SECRET:-404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}

echo
echo "Setting environment variables..."

export DB_URL="jdbc:mysql://localhost:3306/hazeify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export JWT_EXPIRATION="86400000"
export JWT_REFRESH_EXPIRATION="604800000"
export APP_NAME="hazeify"
export APP_PORT="8080"

echo
echo "Environment variables set successfully!"
echo
echo "Database URL: $DB_URL"
echo "Database Username: $DB_USERNAME"
echo "Database Password: [HIDDEN]"
echo "JWT Secret: [HIDDEN]"
echo "JWT Expiration: $JWT_EXPIRATION"
echo "JWT Refresh Expiration: $JWT_REFRESH_EXPIRATION"
echo "App Name: $APP_NAME"
echo "App Port: $APP_PORT"
echo
echo "You can now run the application with: mvn spring-boot:run"
echo

# Optionally save to .env file
read -p "Do you want to save these variables to a .env file? (y/n): " SAVE_ENV
if [[ $SAVE_ENV =~ ^[Yy]$ ]]; then
    cat > .env << EOF
# Database Configuration
DB_URL=$DB_URL
DB_USERNAME=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD

# JWT Configuration
JWT_SECRET_KEY=$JWT_SECRET
JWT_EXPIRATION=$JWT_EXPIRATION
JWT_REFRESH_EXPIRATION=$JWT_REFRESH_EXPIRATION

# Application Configuration
APP_NAME=$APP_NAME
APP_PORT=$APP_PORT
EOF
    echo ".env file created successfully!"
    echo "Note: Make sure .env is in your .gitignore to avoid committing sensitive data."
fi 
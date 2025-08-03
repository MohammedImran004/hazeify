package com.hospitalManagement.hazeify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class EnvironmentConfig {

    // This class can be extended to add custom environment variable handling
    // For now, we're using Spring's built-in environment variable support

    public static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/hazeify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String DEFAULT_DB_USERNAME = "root";
    public static final String DEFAULT_DB_PASSWORD = "imran";
    public static final String DEFAULT_JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public static final long DEFAULT_JWT_EXPIRATION = 86400000L; // 24 hours
    public static final long DEFAULT_JWT_REFRESH_EXPIRATION = 604800000L; // 7 days
}
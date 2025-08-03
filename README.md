# Hazeify - Hospital Management System

A comprehensive hospital management system built with Spring Boot, featuring user management, doctor management, appointment booking, and role-based access control.

## Features

### 🔐 Authentication & Authorization

- JWT-based authentication
- Role-based access control (Admin, Doctor, Patient)
- Secure password encryption
- Session management

### 👥 User Management

- User registration and login
- Role-based dashboards
- Profile management
- Admin user management

### 👨‍⚕️ Doctor Management

- Doctor registration and profiles
- Specialization management
- Availability settings
- Consultation fee management

### 📅 Appointment System

- Book appointments with available doctors
- Appointment status tracking (Pending, Confirmed, Completed, Cancelled)
- Time slot validation
- Conflict detection

### 📊 Admin Dashboard

- System statistics
- User management
- Doctor management
- Appointment overview

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Quick Setup

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE hazeify;
```

### 2. Environment Configuration

Copy the environment file and configure your database:

```bash
cp env.example .env
```

Update the database configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hazeify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at: `http://localhost:8080`

## Test Credentials

### Admin Access

- **Username**: `admin`
- **Email**: `admin@hazeify.com`
- **Password**: `admin123`

### Sample Patients

- **Username**: `john.doe`
- **Email**: `john.doe@example.com`
- **Password**: `patient123`

- **Username**: `jane.smith`
- **Email**: `jane.smith@example.com`
- **Password**: `patient123`

### Sample Doctors (Created Automatically)

- **Dr. Sarah Johnson** - Cardiology
- **Dr. Michael Chen** - Neurology
- **Dr. Emily Rodriguez** - Pediatrics

## API Endpoints

### Public Endpoints

- `GET /` - Home page
- `GET /login` - Login page
- `GET /signup` - Registration page
- `GET /api/public/doctors` - Get available doctors

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Admin Endpoints

- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/doctors/manage` - Manage doctors
- `GET /admin/users` - Manage users
- `GET /admin/appointments` - View all appointments
- `GET /api/admin/doctors` - List all doctors
- `POST /api/admin/doctors` - Add new doctor
- `PUT /api/admin/doctors/{id}` - Update doctor information
- `DELETE /api/admin/doctors/{id}` - Delete doctor

### Patient Endpoints

- `GET /patient/dashboard` - Patient dashboard
- `GET /patient/book-appointment` - Book appointment
- `GET /patient/appointments` - View my appointments
- `GET /patient/profile` - Patient profile

### Doctor Endpoints

- `GET /doctor/dashboard` - Doctor dashboard
- `GET /doctor/appointments` - View appointments
- `GET /doctor/profile` - Doctor profile

## Database Schema

### Users Table

- `id` - Primary key
- `username` - Unique username
- `email` - Unique email
- `password` - Encrypted password
- `full_name` - User's full name
- `phone_number` - Contact number
- `role` - User role (ADMIN, DOCTOR, PATIENT)
- `enabled` - Account status
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

### Doctors Table

- `id` - Primary key
- `name` - Doctor's name
- `specialization` - Medical specialization
- `email` - Unique email
- `phone_number` - Contact number
- `description` - Doctor description
- `visiting_start_time` - Start of visiting hours
- `visiting_end_time` - End of visiting hours
- `consultation_fee` - Fee amount
- `is_available` - Availability status
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

### Appointments Table

- `id` - Primary key
- `patient_name` - Patient's name
- `email` - Patient's email
- `phone` - Patient's phone
- `patient_id` - Reference to user (if registered)
- `doctor_id` - Reference to doctor
- `appointment_date` - Appointment date
- `appointment_time` - Appointment time
- `status` - Appointment status
- `notes` - Additional notes
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

## Troubleshooting

### Common Issues

1. **Database Connection Error**

   - Ensure MySQL is running
   - Check database credentials in `application.properties`
   - Verify database exists

2. **Port Already in Use**

   - Change port in `application.properties`: `server.port=8081`

3. **JWT Token Issues**

   - Check JWT secret key configuration
   - Ensure proper token format

4. **Appointment Booking Issues**
   - Verify doctor availability
   - Check appointment time conflicts
   - Ensure time is within doctor's visiting hours

### Logs

Enable debug logging by adding to `application.properties`:

```properties
logging.level.com.hospitalManagement.hazeify=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/hospitalManagement/hazeify/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── entity/         # JPA entities
│   │       ├── repository/     # Data access layer
│   │       ├── security/       # Security configuration
│   │       └── service/        # Business logic
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       └── application.properties
└── test/                       # Test files
```

### Adding New Features

1. **New Entity**: Create entity class in `entity/` package
2. **Repository**: Create repository interface in `repository/` package
3. **Service**: Create service class in `service/` package
4. **Controller**: Create controller in `controller/` package
5. **DTO**: Create DTO class in `dto/` package if needed
6. **Template**: Create Thymeleaf template in `templates/` package

## Security Features

- Password encryption using BCrypt
- JWT token-based authentication
- Role-based access control
- CSRF protection (disabled for API endpoints)
- Session management
- Input validation and sanitization

## Performance Optimizations

- Lazy loading for entity relationships
- Database indexing on frequently queried fields
- Connection pooling
- Caching for static data

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

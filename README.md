# Hazeify - Hospital Management System

A modern, comprehensive hospital management system built with Spring Boot, featuring appointment booking, doctor management, and role-based access control.

## 🚀 Features

### 📅 Appointment Module

- **Public Appointment Booking**: Patients can book appointments without registration
- **Doctor Appointment Management**: Doctors can view and manage their appointments
- **Admin Dashboard**: Complete appointment oversight and management
- **Real-time Availability**: Check doctor availability and time slots
- **Appointment Status Tracking**: PENDING, CONFIRMED, COMPLETED, CANCELLED

### 👨‍⚕️ Doctor Management

- **Doctor Profiles**: Complete doctor information with specializations
- **Shift Management**: Configure doctor availability and shift timings
- **Specialization Tracking**: Organize doctors by medical specialties
- **Availability Control**: Enable/disable doctor availability

### 🔐 Role-Based Access Control

- **Patient Access**: Book appointments and view personal records
- **Doctor Access**: Manage appointments and patient information
- **Admin Access**: Full system management and oversight

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.x, Spring Security, JWT
- **Database**: JPA/Hibernate with H2 (development)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build Tool**: Maven

## 📁 Project Structure

```
src/main/java/com/hospitalManagement/hazeify/
├── config/                 # Configuration classes
├── controller/            # REST and MVC controllers
│   ├── AdminController.java
│   ├── AdminRestController.java
│   ├── AppointmentController.java
│   ├── DoctorController.java
│   ├── DoctorDashboardController.java
│   ├── HomeController.java
│   └── ...
├── dto/                   # Data Transfer Objects
│   ├── AppointmentDto.java
│   ├── DoctorDto.java
│   └── ...
├── entity/               # JPA entities
│   ├── Appointment.java
│   ├── Doctor.java
│   ├── User.java
│   └── ...
├── repository/           # Data access layer
│   ├── AppointmentRepository.java
│   ├── DoctorRepository.java
│   └── ...
├── security/            # Security configuration
│   ├── SecurityConfig.java
│   ├── JwtService.java
│   └── ...
└── service/             # Business logic
    ├── AppointmentService.java
    ├── DoctorService.java
    └── ...
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd hazeify
   ```

2. **Build the project**

   ```bash
   mvn clean install
   ```

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Main application: http://localhost:8080
   - Appointment booking: http://localhost:8080/appointments/book
   - Doctor appointments: http://localhost:8080/doctor/appointments
   - Admin dashboard: http://localhost:8080/admin/dashboard

## 📋 API Endpoints

### Appointment Management

- `POST /appointments` - Book a new appointment
- `GET /appointments/doctor/{doctorId}` - Get appointments for a doctor
- `GET /appointments/doctor/{doctorId}?date=YYYY-MM-DD` - Get appointments by date

### Doctor Management (Admin)

- `GET /api/admin/doctors` - List all doctors
- `POST /api/admin/doctors` - Add new doctor
- `PUT /api/admin/doctors/{id}` - Update doctor information
- `DELETE /api/admin/doctors/{id}` - Delete doctor

### Doctor Dashboard

- `GET /api/doctor/appointments` - Get logged-in doctor's appointments
- `GET /api/doctor/appointments?date=YYYY-MM-DD` - Filter appointments by date

## 🎨 User Interface

### Public Pages

- **Home Page** (`/`): Landing page with navigation to all features
- **Appointment Booking** (`/appointments/book`): Public form to book appointments

### Role-Specific Pages

- **Doctor Appointments** (`/doctor/appointments`): Doctor's appointment management
- **Admin Doctor Management** (`/admin/doctors/manage`): Admin's doctor management interface

## 🔐 Security & Access Control

### Role-Based Routes

- **Public**: `/`, `/appointments/book`, `/appointments` (POST)
- **Admin Only**: `/admin/**`, `/api/admin/**`
- **Doctor Only**: `/doctor/**`, `/api/doctor/**`
- **Patient Only**: `/patient/**`

### Authentication

- JWT-based authentication
- Form-based login
- Role-based authorization

## 📊 Database Schema

### Appointment Entity

```java
- id (Long)
- patientName (String)
- email (String)
- phone (String)
- patient (User) - ManyToOne
- doctor (Doctor) - ManyToOne
- date (LocalDate)
- time (LocalTime)
- status (AppointmentStatus)
- notes (String)
```

### Doctor Entity

```java
- id (Long)
- name (String)
- specialization (String)
- email (String)
- phoneNumber (String)
- visitingStartTime (LocalTime)
- visitingEndTime (LocalTime)
- consultationFee (Double)
- isAvailable (Boolean)
```

## 🎯 Key Features Implementation

### 1. Appointment Booking Flow

1. Patient accesses `/appointments/book`
2. Fills appointment form with patient details
3. Selects doctor from available list
4. Chooses date and time
5. System validates availability and creates appointment

### 2. Doctor Dashboard

1. Doctor logs in and accesses `/doctor/appointments`
2. Views all appointments with filtering options
3. Can filter by date and status
4. Real-time statistics display

### 3. Admin Management

1. Admin accesses `/admin/doctors/manage`
2. Views all doctors in a table format
3. Can add, edit, and delete doctors
4. Manages doctor availability and shift timings

## 🧪 Testing

### Manual Testing

1. **Appointment Booking**: Test the public appointment form
2. **Doctor Management**: Test admin CRUD operations
3. **Role Access**: Verify role-based access control
4. **Data Validation**: Test form validation and error handling

### API Testing

Use tools like Postman or curl to test REST endpoints:

```bash
# Book an appointment
curl -X POST http://localhost:8080/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientName": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "doctorId": 1,
    "date": "2024-01-15",
    "time": "10:00:00",
    "notes": "Regular checkup"
  }'
```

## 🚀 Deployment

### Development

```bash
mvn spring-boot:run
```

### Production

```bash
mvn clean package
java -jar target/hazeify-0.0.1-SNAPSHOT.jar
```

## 📝 Configuration

### Application Properties

Key configuration in `application.properties`:

- Database connection settings
- JWT secret key
- Server port configuration
- Logging levels

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:

- Create an issue in the repository
- Contact the development team

---

**Hazeify** - Modern Hospital Management System 🏥

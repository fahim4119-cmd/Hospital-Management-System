# Hospital Management System

Java Swing and MySQL desktop application for managing the daily records of a small hospital or clinic.

## Purpose

This project is built as an Object-Oriented Programming hospital management system. Its purpose is to keep common hospital data in one place and make it easy for an administrator to:

- Register and log in to the system.
- Manage doctors and their specializations.
- Manage patients and their basic medical information.
- Schedule and update appointments between patients and doctors.
- Manage medicine inventory, prices, quantities, and expiry dates.
- Search records quickly from each module.

The system is not meant to be a production medical records platform. It is a learning project that demonstrates Java OOP, Swing GUI development, JDBC, DAO classes, validation, and MySQL database operations.

## Main Features

- Login and registration screens.
- Dashboard with live totals for doctors, patients, appointments, and medicines.
- Doctors module with add, edit, delete, search, and refresh.
- Patients module with add, edit, delete, search, and refresh.
- Appointments module with doctor/patient selection, date, time, status, notes, search, and refresh.
- Medicines module with add, edit, delete, search, and refresh.
- Input validation for required fields, email, age, price, quantity, experience, date, and time.
- MySQL database script with demo records.
- Separated packages for models, database access, UI screens, dialogs, and utilities.

## Technologies Used

- Java 8 or higher
- Java Swing for the graphical user interface
- MySQL for persistent storage
- JDBC for database communication
- MySQL Connector/J included in `lib/`

## Project Structure

```text
HospitalManagementSystem/
|-- README.md
|-- hospital_db.sql
|-- lib/
|   `-- mysql-connector-j-9.7.0.jar
`-- src/
    `-- hospital/
        |-- Main.java
        |-- db/
        |   |-- DBConnection.java
        |   |-- UserDAO.java
        |   |-- DoctorDAO.java
        |   |-- PatientDAO.java
        |   |-- AppointmentDAO.java
        |   `-- MedicineDAO.java
        |-- models/
        |   |-- User.java
        |   |-- Doctor.java
        |   |-- Patient.java
        |   |-- Appointment.java
        |   `-- Medicine.java
        |-- ui/
        |   |-- LoginFrame.java
        |   |-- RegisterFrame.java
        |   |-- DashboardFrame.java
        |   |-- DoctorsPanel.java
        |   |-- PatientsPanel.java
        |   |-- AppointmentsPanel.java
        |   |-- MedicinesPanel.java
        |   `-- dialogs/
        |       |-- DoctorDialog.java
        |       |-- PatientDialog.java
        |       |-- AppointmentDialog.java
        |       `-- MedicineDialog.java
        `-- utils/
            |-- UITheme.java
            `-- Validator.java
```

## How The Application Works

### 1. Program Start

The application starts from `src/hospital/Main.java`.

`Main.main()` applies the shared Swing look and feel through `UITheme.applyLookAndFeel()`, then opens `LoginFrame` on the Swing event dispatch thread.

### 2. Login And Registration

`LoginFrame` asks for username and password.

When the user clicks `Sign In`, the form validates that both fields are filled. Then `UserDAO.login()` runs a SQL query against the `users` table:

```sql
SELECT * FROM users WHERE username = ? AND password = ?
```

If a matching record exists, the login window closes and `DashboardFrame` opens.

`RegisterFrame` creates a new account. It validates:

- Full name is required.
- Username is required.
- Email must use a valid email format.
- Password must be at least 6 characters.
- Confirm password must match password.

If validation passes, `UserDAO.register()` checks whether the username already exists, then inserts the new user into the `users` table.

### 3. Dashboard

`DashboardFrame` is the main application window after login.

It contains:

- A left sidebar for navigation.
- A top bar showing the current page name.
- A main content area that changes when a module is selected.
- Dashboard cards showing live totals from the database.

The totals come from DAO methods:

- `DoctorDAO.getTotalDoctors()`
- `PatientDAO.getTotalPatients()`
- `AppointmentDAO.getTotalAppointments()`
- `MedicineDAO.getTotalMedicines()`

### 4. Modules

Each module is a Swing `JPanel` shown inside the dashboard content area.

The modules follow the same pattern:

- Load records from the matching DAO.
- Display records in a non-editable `JTable`.
- Open a dialog for adding or editing records.
- Delete selected records after confirmation.
- Search records using a keyword.
- Refresh the table to show all records again.

### 5. Dialogs

Dialogs are used for add and edit operations:

- `DoctorDialog`
- `PatientDialog`
- `AppointmentDialog`
- `MedicineDialog`

Each dialog collects form data, validates it with `Validator`, and returns a model object to the panel. The panel then passes that model object to the correct DAO method.

### 6. Database Layer

The `db` package contains DAO classes. DAO means Data Access Object.

The DAO classes hide SQL from the UI. For example, `DoctorsPanel` does not directly write SQL. It calls methods like:

- `doctorDAO.getAllDoctors()`
- `doctorDAO.addDoctor(doctor)`
- `doctorDAO.updateDoctor(doctor)`
- `doctorDAO.deleteDoctor(id)`
- `doctorDAO.searchDoctors(keyword)`

This makes the project easier to understand because the UI code and database code are separated.

### 7. Models

The `models` package contains simple Java classes that represent database records:

- `User`
- `Doctor`
- `Patient`
- `Appointment`
- `Medicine`

These classes use private fields with getters and setters, which demonstrates encapsulation.

### 8. Utilities

The `utils` package contains:

- `UITheme.java`: shared colors, fonts, buttons, cards, fields, table styling, and look-and-feel setup.
- `Validator.java`: reusable validation methods for input fields.

## Database Tables

The database script `hospital_db.sql` creates these tables:

- `users`: login and registration accounts.
- `doctors`: doctor profiles.
- `patients`: patient records.
- `appointments`: appointments connected to patients and doctors.
- `medicines`: medicine inventory.

The appointments table uses foreign keys:

- `patient_id` references `patients.id`
- `doctor_id` references `doctors.id`

If a patient or doctor is deleted, related appointments are also deleted because the SQL script uses `ON DELETE CASCADE`.

## Setup Instructions

### 1. Install Requirements

- Install Java JDK 8 or higher.
- Install MySQL Server.
- Use the included MySQL connector file: `lib/mysql-connector-j-9.7.0.jar`.

### 2. Create The Database

Open MySQL Workbench, phpMyAdmin, or the MySQL command line and run:

```sql
SOURCE path/to/hospital_db.sql;
```

Or open `hospital_db.sql` and execute the full script manually.

The script creates:

- The `hospital_db` database.
- All required tables.
- A default admin account.
- Demo doctors, patients, appointments, and medicines.

### 3. Configure Database Password

Open `src/hospital/db/DBConnection.java` and update this line if your MySQL password is different:

```java
private static final String PASSWORD = "fahair";
```

Also check:

```java
private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
private static final String USER = "root";
```

### 4. Compile From Terminal

From the project root on Windows PowerShell:

```powershell
javac -cp "lib\mysql-connector-j-9.7.0.jar" -d out src\hospital\Main.java src\hospital\db\*.java src\hospital\models\*.java src\hospital\ui\*.java src\hospital\ui\dialogs\*.java src\hospital\utils\*.java
```

### 5. Run From Terminal

```powershell
java -cp "out;lib\mysql-connector-j-9.7.0.jar" hospital.Main
```

### 6. Run From An IDE

You can also open the project in NetBeans, IntelliJ IDEA, or Eclipse.

Make sure to:

- Add `lib/mysql-connector-j-9.7.0.jar` to the project libraries.
- Set the main class to `hospital.Main`.
- Run `hospital_db.sql` before logging in.
- Check the MySQL password in `DBConnection.java`.

## Default Login

| Username | Password |
| --- | --- |
| admin | admin123 |

## Example User Flow

1. Start the program.
2. Log in using `admin` and `admin123`.
3. The dashboard opens and shows record totals.
4. Click `Doctors` to add, edit, delete, search, or refresh doctors.
5. Click `Patients` to manage patient records.
6. Click `Appointments` to schedule a patient with a doctor.
7. Click `Medicines` to manage medicine inventory.
8. Click `Logout` to return to the login screen.

## OOP Concepts Demonstrated

- Encapsulation: model fields are private and accessed through getters/setters.
- Abstraction: DAO classes hide SQL details from the UI.
- Inheritance: UI classes extend Swing classes such as `JFrame`, `JPanel`, and `JDialog`.
- Composition: frames and panels are built from reusable Swing components.
- Separation of concerns: models, DAO classes, UI classes, dialogs, validation, and theme code are separated.

## Important Notes

- Passwords are stored as plain text for learning purposes. A real system should hash passwords.
- Date fields use `YYYY-MM-DD`.
- Time fields use `HH:MM`.
- The application expects MySQL to be running before login.
- If a table appears empty, first confirm the database script has been executed and `DBConnection.java` has the correct credentials.

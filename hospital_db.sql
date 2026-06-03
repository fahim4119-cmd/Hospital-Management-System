-- ============================================================
-- Hospital Management System - Database Setup
-- Run this script in MySQL Workbench or phpMyAdmin
-- ============================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- ============================================================
-- Table: users (for login/authentication)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'Admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'Admin';

-- ============================================================
-- Table: doctors
-- ============================================================
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    qualification VARCHAR(100),
    gender VARCHAR(10),
    experience INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Table: patients
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10),
    blood_group VARCHAR(10),
    phone VARCHAR(20),
    address VARCHAR(200),
    disease VARCHAR(200),
    photo_path VARCHAR(300),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE patients ADD COLUMN IF NOT EXISTS photo_path VARCHAR(300);

-- ============================================================
-- Table: appointments
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- ============================================================
-- Table: medicines
-- ============================================================
CREATE TABLE IF NOT EXISTS medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    manufacturer VARCHAR(100),
    price DECIMAL(10, 2) DEFAULT 0.00,
    quantity INT DEFAULT 0,
    expiry_date DATE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Table: billing
-- ============================================================
CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE
);

-- ============================================================
-- Table: rooms and admissions
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) UNIQUE NOT NULL,
    room_type VARCHAR(20) NOT NULL,
    floor INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS admissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    room_id INT NOT NULL,
    admission_date DATE NOT NULL,
    discharge_date DATE,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- ============================================================
-- Table: staff
-- ============================================================
CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(40),
    department VARCHAR(80),
    phone VARCHAR(20),
    shift VARCHAR(20),
    salary DECIMAL(10, 2) DEFAULT 0.00
);

-- ============================================================
-- Demo Data
-- ============================================================

-- Default admin user (username: admin, password: admin123)
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@hospital.com', 'Admin'),
('doctor', 'doctor123', 'Dr. Portal User', 'doctor@hospital.com', 'Doctor'),
('reception', 'reception123', 'Front Desk User', 'reception@hospital.com', 'Receptionist');

-- Sample doctors
INSERT INTO doctors (name, specialization, phone, email, qualification, gender, experience) VALUES
('Dr. Ahmed Khan', 'General Physician', '0300-1234567', 'ahmed.khan@hospital.com', 'MBBS, MD', 'Male', 12),
('Dr. Sara Ali', 'Gynecologist', '0301-2345678', 'sara.ali@hospital.com', 'MBBS, FCPS', 'Female', 8),
('Dr. Bilal Hassan', 'Cardiologist', '0302-3456789', 'bilal.hassan@hospital.com', 'MBBS, MRCP', 'Male', 15),
('Dr. Fatima Malik', 'Pediatrician', '0303-4567890', 'fatima.malik@hospital.com', 'MBBS, DCH', 'Female', 6),
('Dr. Omar Sheikh', 'Orthopedic Surgeon', '0304-5678901', 'omar.sheikh@hospital.com', 'MBBS, FRCS', 'Male', 20),
('Dr. Ayesha Noor', 'Dermatologist', '0305-6789012', 'ayesha.noor@hospital.com', 'MBBS, MCPS', 'Female', 5),
('Dr. Zafar Iqbal', 'Neurologist', '0306-7890123', 'zafar.iqbal@hospital.com', 'MBBS, FCPS', 'Male', 18);

-- Sample patients
INSERT INTO patients (name, age, gender, blood_group, phone, address, disease) VALUES
('Muhammad Ali', 35, 'Male', 'O+', '0310-1111111', 'Street 4, Gulshan, Karachi', 'Hypertension'),
('Zainab Hussain', 28, 'Female', 'A+', '0311-2222222', 'Block B, Model Town, Lahore', 'Anemia'),
('Usman Tariq', 52, 'Male', 'B+', '0312-3333333', 'F-10, Islamabad', 'Diabetes Type 2'),
('Amna Raza', 19, 'Female', 'AB-', '0313-4444444', 'Hayatabad, Peshawar', 'Asthma'),
('Kashif Mehmood', 45, 'Male', 'O-', '0314-5555555', 'Satellite Town, Rawalpindi', 'Arthritis'),
('Sana Baig', 33, 'Female', 'A-', '0315-6666666', 'Johar Town, Lahore', 'Migraine'),
('Tariq Jameel', 60, 'Male', 'B-', '0316-7777777', 'Clifton, Karachi', 'Heart Disease');

-- Sample appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES
(1, 1, CURDATE(), '09:00', 'Confirmed', 'Regular checkup'),
(2, 2, CURDATE(), '10:30', 'Pending', 'Follow-up visit'),
(3, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00', 'Confirmed', 'ECG test required'),
(4, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00', 'Pending', 'Vaccination'),
(5, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:30', 'Confirmed', 'X-Ray result review'),
(6, 6, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '15:00', 'Pending', 'Skin allergy treatment'),
(7, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '10:00', 'Completed', 'Post-op checkup');

-- Sample medicines
INSERT INTO medicines (name, category, manufacturer, price, quantity, expiry_date, description) VALUES
('Panadol 500mg', 'Analgesic', 'GlaxoSmithKline', 25.00, 500, '2026-12-31', 'Pain reliever and fever reducer'),
('Augmentin 625mg', 'Antibiotic', 'GlaxoSmithKline', 180.00, 200, '2026-06-30', 'Broad-spectrum antibiotic'),
('Metformin 500mg', 'Antidiabetic', 'Getz Pharma', 45.00, 300, '2027-01-31', 'For Type 2 Diabetes management'),
('Omeprazole 20mg', 'Antacid', 'AGP', 60.00, 150, '2026-09-30', 'Proton pump inhibitor for acid reflux'),
('Amoxicillin 250mg', 'Antibiotic', 'Highnoon Pharma', 120.00, 400, '2026-03-31', 'For bacterial infections'),
('Brufen 400mg', 'Analgesic', 'Abbott', 35.00, 600, '2027-06-30', 'Anti-inflammatory and pain relief'),
('Vitamin C 500mg', 'Supplement/Vitamin', 'Pharmatec', 80.00, 1000, '2027-12-31', 'Immune system support'),
('Atorvastatin 10mg', 'Cardiovascular', 'ICI Pakistan', 95.00, 250, '2026-11-30', 'Cholesterol lowering medicine');

INSERT INTO rooms (room_number, room_type, floor, status) VALUES
('101', 'General', 1, 'Available'),
('102', 'General', 1, 'Available'),
('201', 'Private', 2, 'Available'),
('ICU-1', 'ICU', 1, 'Available');

INSERT INTO staff (name, role, department, phone, shift, salary) VALUES
('Nadia Khan', 'Nurse', 'Emergency', '0320-1111111', 'Morning', 65000),
('Hassan Rauf', 'Receptionist', 'Front Desk', '0321-2222222', 'Evening', 52000),
('Mariam Shah', 'Lab Technician', 'Laboratory', '0322-3333333', 'Night', 70000);

-- Verification queries
SELECT 'Users' AS TableName, COUNT(*) AS Records FROM users
UNION ALL
SELECT 'Doctors', COUNT(*) FROM doctors
UNION ALL
SELECT 'Patients', COUNT(*) FROM patients
UNION ALL
SELECT 'Appointments', COUNT(*) FROM appointments
UNION ALL
SELECT 'Medicines', COUNT(*) FROM medicines;

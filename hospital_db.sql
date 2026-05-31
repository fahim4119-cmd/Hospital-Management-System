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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
-- Demo Data
-- ============================================================

-- Default admin user (username: admin, password: admin123)
INSERT INTO users (username, password, full_name, email) VALUES
('admin', 'admin123', 'System Administrator', 'admin@hospital.com');

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

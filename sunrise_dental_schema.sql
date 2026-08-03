-- =====================================================================
-- SUNRISE DENTAL CLINIC - Appointment & Patient Management System
-- Initial Database Schema (Step 1)
-- Based on Section 7.1 of the Task B Design Document
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sunrise_dental;

-- ---------------------------------------------------------------------
-- USERS  (login accounts for Admin / Reception / Dentist / Patient)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    salt           VARCHAR(255) NOT NULL,
    role           ENUM('ADMIN','RECEPTION','DENTIST','PATIENT') NOT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- PATIENTS
-- ---------------------------------------------------------------------
CREATE TABLE patients (
    patient_id        INT AUTO_INCREMENT PRIMARY KEY,
    patient_name      VARCHAR(100) NOT NULL,
    address           VARCHAR(255),
    contact_number    VARCHAR(20)  NOT NULL,
    date_of_birth     DATE         NOT NULL,
    emergency_contact VARCHAR(100),
    patient_login_id  INT NULL,                      -- FK to users, nullable (not all patients self-register)
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_user FOREIGN KEY (patient_login_id)
        REFERENCES users(user_id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- DENTISTS
-- ---------------------------------------------------------------------
CREATE TABLE dentists (
    dentist_id     INT AUTO_INCREMENT PRIMARY KEY,
    dentist_name   VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    working_hours  VARCHAR(100),                     -- e.g. "Mon-Fri 09:00-17:00"
    user_id        INT NOT NULL,
    CONSTRAINT fk_dentist_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- TREATMENT TYPES
-- ---------------------------------------------------------------------
CREATE TABLE treatment_types (
    treatment_id       INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name     VARCHAR(100) NOT NULL,
    consultation_fee   DECIMAL(10,2) NOT NULL CHECK (consultation_fee >= 0),
    estimated_duration INT NOT NULL COMMENT 'minutes'
);

-- ---------------------------------------------------------------------
-- APPOINTMENTS
-- ---------------------------------------------------------------------
CREATE TABLE appointments (
    appointment_no   VARCHAR(20) PRIMARY KEY,         -- e.g. APT-2026-0143
    patient_id       INT NOT NULL,
    dentist_id       INT NOT NULL,
    treatment_id     INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status           ENUM('BOOKED','COMPLETED','CANCELLED','NO_SHOW') NOT NULL DEFAULT 'BOOKED',
    created_by       INT NOT NULL,                    -- FK to users
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_appt_dentist FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appt_treatment FOREIGN KEY (treatment_id) REFERENCES treatment_types(treatment_id),
    CONSTRAINT fk_appt_created_by FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------
-- BILLS
-- ---------------------------------------------------------------------
CREATE TABLE bills (
    bill_id        INT AUTO_INCREMENT PRIMARY KEY,
    appointment_no VARCHAR(20) NOT NULL UNIQUE,
    total_amount   DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    payment_status ENUM('UNPAID','PAID','PARTIAL') NOT NULL DEFAULT 'UNPAID',
    generated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    generated_by   INT NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_no) REFERENCES appointments(appointment_no),
    CONSTRAINT fk_bill_generated_by FOREIGN KEY (generated_by) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------
-- CLINICAL NOTES
-- ---------------------------------------------------------------------
CREATE TABLE clinical_notes (
    note_id            INT AUTO_INCREMENT PRIMARY KEY,
    appointment_no     VARCHAR(20) NOT NULL UNIQUE,
    diagnosis          TEXT,
    procedure_notes    TEXT,
    follow_up_required BOOLEAN NOT NULL DEFAULT FALSE,
    recorded_by        INT NOT NULL,
    CONSTRAINT fk_note_appointment FOREIGN KEY (appointment_no) REFERENCES appointments(appointment_no),
    CONSTRAINT fk_note_recorded_by FOREIGN KEY (recorded_by) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------
-- AUDIT LOG
-- ---------------------------------------------------------------------
CREATE TABLE audit_log (
    log_id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL,
    action         VARCHAR(100) NOT NULL,
    table_affected VARCHAR(50)  NOT NULL,
    timestamp      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ---------------------------------------------------------------------
-- Sample lookup data to get started (optional but useful for testing)
-- ---------------------------------------------------------------------
INSERT INTO treatment_types (treatment_name, consultation_fee, estimated_duration) VALUES
    ('General Checkup', 25.00, 30),
    ('Tooth Extraction', 60.00, 45),
    ('Root Canal', 150.00, 90),
    ('Teeth Cleaning', 40.00, 30);

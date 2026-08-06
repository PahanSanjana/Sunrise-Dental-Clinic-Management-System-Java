package model;

import java.sql.Date;

public class Patient {
    private int patientId;
    private String patientName;
    private String gender;
    private String address;
    private String contactNumber;
    private String email;
    private Date dateOfBirth;
    private String emergencyContact;
    private String emergencyPhone;
    private int patientLoginId; // -1 means NULL
    private String createdAt;
    private String updatedAt;
    private String medicalHistory;
    private String allergies;

    public Patient() {}

    // Constructor for new patient (without ID)
    public Patient(String patientName, String gender, String address, String contactNumber,
                   String email, Date dateOfBirth, String emergencyContact, String emergencyPhone,
                   int patientLoginId, String medicalHistory, String allergies) {
        this.patientName = patientName;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.patientLoginId = patientLoginId;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
    }

    // Constructor with all fields
    public Patient(int patientId, String patientName, String gender, String address,
                   String contactNumber, String email, Date dateOfBirth, String emergencyContact,
                   String emergencyPhone, int patientLoginId, String createdAt, 
                   String updatedAt, String medicalHistory, String allergies) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.patientLoginId = patientLoginId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
    }

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public int getPatientLoginId() { return patientLoginId; }
    public void setPatientLoginId(int patientLoginId) { this.patientLoginId = patientLoginId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    @Override
    public String toString() {
        return patientName;
    }
}
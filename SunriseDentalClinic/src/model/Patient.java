package model;

import java.sql.Date;

public class Patient {
    private int patientId;
    private int userId;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String medicalHistory;
    private String allergies;
    private String createdAt;
    private String updatedAt;

    public Patient() {}

    public Patient(int patientId, int userId, String firstName, String lastName, 
                   Date dateOfBirth, String gender, String phone, String email, 
                   String address, String emergencyContact, String emergencyPhone, 
                   String medicalHistory, String allergies, String createdAt, String updatedAt) {
        this.patientId = patientId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for new patient (without ID)
    public Patient(String firstName, String lastName, Date dateOfBirth, String gender,
                   String phone, String email, String address, String emergencyContact,
                   String emergencyPhone, String medicalHistory, String allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
    }

    // Getters and Setters
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
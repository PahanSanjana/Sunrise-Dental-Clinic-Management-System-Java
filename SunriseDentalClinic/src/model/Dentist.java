package model;

public class Dentist {
    private int dentistId;
    private String dentistName;
    private String specialization;
    private String licenseNumber;
    private String workingHours;
    private String phone;
    private String email;
    private int yearsOfExperience;
    private double consultationFee;
    private boolean isAvailable;
    private int userId;
    private String createdAt;
    private String updatedAt;

    // Default Constructor
    public Dentist() {}

    // Constructor for new dentist (without ID)
    public Dentist(String dentistName, String specialization, String licenseNumber,
                   String workingHours, String phone, String email,
                   int yearsOfExperience, double consultationFee, boolean isAvailable) {
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.workingHours = workingHours;
        this.phone = phone;
        this.email = email;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.isAvailable = isAvailable;
        this.userId = -1; // -1 means NULL
    }

    // Constructor with all fields (for retrieving from database)
    public Dentist(int dentistId, String dentistName, String specialization,
                   String licenseNumber, String workingHours, String phone, String email,
                   int yearsOfExperience, double consultationFee, boolean isAvailable,
                   int userId, String createdAt, String updatedAt) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.workingHours = workingHours;
        this.phone = phone;
        this.email = email;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.isAvailable = isAvailable;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Check if the dentist is linked to a user account
     * @return true if linked to a user, false otherwise
     */
    public boolean isLinkedToUser() {
        return userId > 0;
    }

    /**
     * Get the dentist name as a string
     * @return Dentist name
     */
    public String getFullName() {
        return dentistName;
    }

    @Override
    public String toString() {
        return dentistName + " (" + specialization + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dentist dentist = (Dentist) obj;
        return dentistId == dentist.dentistId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(dentistId);
    }
}
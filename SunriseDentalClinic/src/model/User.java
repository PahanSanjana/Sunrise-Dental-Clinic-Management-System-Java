package model;

public class User {
    
    public enum UserRole {
        ADMIN,
        RECEPTION,
        DENTIST,
        PATIENT
    }
    
    private int userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String email;
    private UserRole role;
    private boolean isActive;
    private int createdBy;
    private String createdAt;
    private String updatedAt;
    
    // =====================================================
    // ADDITIONAL FIELDS FOR ROLE-BASED ACCESS
    // =====================================================
    private Integer patientId;  // For PATIENT role - links to patients table
    private Integer dentistId;  // For DENTIST role - links to dentists table
    private Integer staffId;    // For STAFF role - links to staff table
    private String fullName;    // Display name for the user

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    // Default constructor
    public User() {}

    // Constructor for creating new user (without ID)
    public User(String username, String passwordHash, String salt, String email, 
                UserRole role, boolean isActive, int createdBy) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdBy = createdBy;
    }

    // Full constructor with all fields
    public User(int userId, String username, String passwordHash, String salt,
                String email, UserRole role, boolean isActive, int createdBy,
                String createdAt, String updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Complete constructor with all fields including role-specific IDs
    public User(int userId, String username, String passwordHash, String salt,
                String email, UserRole role, boolean isActive, int createdBy,
                String createdAt, String updatedAt, Integer patientId, 
                Integer dentistId, Integer staffId, String fullName) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.staffId = staffId;
        this.fullName = fullName;
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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
    // ROLE-SPECIFIC GETTERS AND SETTERS
    // =====================================================

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDentistId() {
        return dentistId;
    }

    public void setDentistId(Integer dentistId) {
        this.dentistId = dentistId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // =====================================================
    // ROLE CHECK METHODS
    // =====================================================

    /**
     * Check if user has Admin role
     * @return true if Admin, false otherwise
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Check if user has Reception role
     * @return true if Reception, false otherwise
     */
    public boolean isReception() {
        return role == UserRole.RECEPTION;
    }

    /**
     * Check if user has Dentist role
     * @return true if Dentist, false otherwise
     */
    public boolean isDentist() {
        return role == UserRole.DENTIST;
    }

    /**
     * Check if user has Patient role
     * @return true if Patient, false otherwise
     */
    public boolean isPatient() {
        return role == UserRole.PATIENT;
    }

    /**
     * Check if user has Staff role (Reception or Dentist)
     * @return true if Staff, false otherwise
     */
    public boolean isStaff() {
        return role == UserRole.RECEPTION || role == UserRole.DENTIST;
    }

    /**
     * Check if user has any of the specified roles
     * @param roles Array of roles to check
     * @return true if user has any of the roles, false otherwise
     */
    public boolean hasRole(UserRole... roles) {
        if (roles == null) return false;
        for (UserRole r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user account is active and can login
     * @return true if active, false otherwise
     */
    public boolean canLogin() {
        return isActive;
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view patient data
     */
    public boolean canViewPatients() {
        return isAdmin() || isReception() || isDentist();
    }

    /**
     * Check if user can add/edit patients
     */
    public boolean canManagePatients() {
        return isAdmin() || isReception();
    }

    /**
     * Check if user can view appointments
     */
    public boolean canViewAppointments() {
        return true; // All roles can view appointments
    }

    /**
     * Check if user can manage appointments
     */
    public boolean canManageAppointments() {
        return isAdmin() || isReception();
    }

    /**
     * Check if user can book appointments
     */
    public boolean canBookAppointments() {
        return true; // All roles can book appointments
    }

    /**
     * Check if user can view billing data
     */
    public boolean canViewBills() {
        return true; // All roles can view bills
    }

    /**
     * Check if user can generate bills
     */
    public boolean canGenerateBills() {
        return isAdmin() || isReception() || isDentist();
    }

    /**
     * Check if user can manage bills (edit/delete)
     */
    public boolean canManageBills() {
        return isAdmin();
    }

    /**
     * Check if user can view reports
     */
    public boolean canViewReports() {
        return true; // All roles can view reports
    }

    /**
     * Check if user can manage staff
     */
    public boolean canManageStaff() {
        return isAdmin();
    }

    /**
     * Check if user can manage dentists
     */
    public boolean canManageDentists() {
        return isAdmin() || isReception();
    }

    /**
     * Check if user can manage treatments
     */
    public boolean canManageTreatments() {
        return isAdmin() || isReception();
    }

    /**
     * Check if user can view user management
     */
    public boolean canManageUsers() {
        return isAdmin();
    }

    // =====================================================
    // DATA FILTERING HELPERS
    // =====================================================

    /**
     * Get the ID to filter appointments by based on user role
     * - ADMIN/RECEPTION: null (view all)
     * - DENTIST: dentistId (view only their appointments)
     * - PATIENT: patientId (view only their appointments)
     */
    public Integer getAppointmentFilterId() {
        if (isAdmin() || isReception()) {
            return null; // View all
        } else if (isDentist()) {
            return dentistId;
        } else if (isPatient()) {
            return patientId;
        }
        return null;
    }

    /**
     * Get the column name to filter appointments by based on user role
     */
    public String getAppointmentFilterColumn() {
        if (isDentist()) {
            return "dentist_id";
        } else if (isPatient()) {
            return "patient_id";
        }
        return null;
    }

    /**
     * Get the ID to filter bills by based on user role
     * - ADMIN/RECEPTION: null (view all)
     * - DENTIST: dentistId (view bills from their appointments)
     * - PATIENT: patientId (view only their bills)
     */
    public Integer getBillFilterId() {
        if (isAdmin() || isReception()) {
            return null; // View all
        } else if (isDentist()) {
            return dentistId;
        } else if (isPatient()) {
            return patientId;
        }
        return null;
    }

    // =====================================================
    // DISPLAY HELPERS
    // =====================================================

    /**
     * Get display name for the user
     */
    public String getDisplayName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return username;
    }

    /**
     * Get the dashboard card name for this user role
     */
    public String getDashboardCard() {
        switch (role) {
            case ADMIN:
                return "DASHBOARD";
            case RECEPTION:
                return "DASHBOARD";
            case DENTIST:
                return "DASHBOARD";
            case PATIENT:
                return "DASHBOARD";
            default:
                return "DASHBOARD";
        }
    }

    /**
     * Get the role display name (capitalized)
     */
    public String getRoleDisplayName() {
        return role.name().substring(0, 1).toUpperCase() + role.name().substring(1).toLowerCase();
    }

    // =====================================================
    // OVERRIDE METHODS
    // =====================================================

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}
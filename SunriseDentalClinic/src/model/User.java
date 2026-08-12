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
    // HELPER METHODS
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
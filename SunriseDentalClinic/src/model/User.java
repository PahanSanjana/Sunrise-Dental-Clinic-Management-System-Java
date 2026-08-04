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
    private UserRole role;
    private boolean isActive;
    private String createdAt;

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(int userId, String username, String passwordHash, String salt, 
                UserRole role, boolean isActive, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Constructor without userId (for creating new users)
    public User(String username, String passwordHash, String salt, 
                UserRole role, boolean isActive) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.isActive = isActive;
    }

    // Getters
    public int getUserId() { 
        return userId; 
    }

    public String getUsername() { 
        return username; 
    }

    public String getPasswordHash() { 
        return passwordHash; 
    }

    public String getSalt() { 
        return salt; 
    }

    public UserRole getRole() { 
        return role; 
    }

    public boolean isActive() { 
        return isActive; 
    }

    public String getCreatedAt() { 
        return createdAt; 
    }

    // Setters
    public void setUserId(int userId) { 
        this.userId = userId; 
    }

    public void setUsername(String username) { 
        this.username = username; 
    }

    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }

    public void setSalt(String salt) { 
        this.salt = salt; 
    }

    public void setRole(UserRole role) { 
        this.role = role; 
    }

    public void setActive(boolean active) { 
        isActive = active; 
    }

    public void setCreatedAt(String createdAt) { 
        this.createdAt = createdAt; 
    }

    // Helper methods
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isReception() {
        return role == UserRole.RECEPTION;
    }

    public boolean isDentist() {
        return role == UserRole.DENTIST;
    }

    public boolean isPatient() {
        return role == UserRole.PATIENT;
    }

    public boolean hasRole(UserRole... roles) {
        if (roles == null) return false;
        for (UserRole r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
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
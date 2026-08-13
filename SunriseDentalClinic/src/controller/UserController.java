package controller;

import dao.UserDAO;
import model.User;
import model.User.UserRole;
import view.UserManagementPanel;

import java.util.List;
import java.util.Map;

public class UserController {
    private UserManagementPanel view;
    private UserDAO userDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for UserManagementPanel
     * @param view The UserManagementPanel instance
     */
    public UserController(UserManagementPanel view) {
        this.view = view;
        this.userDAO = new UserDAO();
    }

    // =====================================================
    // CREATE USER WITH PROFILE (FOR ADMIN)
    // =====================================================

    /**
     * Create a user with profile based on role
     * @param username Username
     * @param password Plain password
     * @param email Email address
     * @param role User role
     * @param createdBy User ID of creator
     * @param profileData Map of profile data (role-specific)
     * @return Created User object, null if failed
     */
    public User createUserWithProfile(String username, String password, String email,
                                      UserRole role, int createdBy, Map<String, Object> profileData) {
        return userDAO.createUserWithProfile(username, password, email, role, createdBy, profileData);
    }

    // =====================================================
    // CREATE PATIENT USER (FOR SIGNUP)
    // =====================================================

    /**
     * Create a new patient user with default PATIENT role (for signup)
     * @param username The username
     * @param password The plain text password
     * @param salt The salt
     * @param fullName The full name
     * @param email The email
     * @param phone The phone number
     * @return The created User object if successful, null otherwise
     */
    public User createPatientUser(String username, String password, String salt, 
                                  String fullName, String email, String phone) {
        return userDAO.createPatientUser(username, password, salt, fullName, email, phone);
    }

    /**
     * Create a new user (without profile)
     * @param user The user to create
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user) {
        return userDAO.createUser(user);
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Get user by ID
     * @param userId The user ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    /**
     * Get user by username
     * @param username The username
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    /**
     * Get user by email
     * @param email The email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    /**
     * Get users by role
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        return userDAO.getUsersByRole(role);
    }

    /**
     * Get active users
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        return userDAO.getActiveUsers();
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update user information
     * @param user The user to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    /**
     * Change user password
     * @param userId The user ID
     * @param newPassword The new password (plain text, will be hashed)
     * @return true if successful, false otherwise
     */
    public boolean changePassword(int userId, String newPassword) {
        String salt = "salt_" + System.currentTimeMillis();
        return userDAO.changePassword(userId, newPassword, salt);
    }

    /**
     * Deactivate a user (soft delete)
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateUser(int userId) {
        return userDAO.deactivateUser(userId);
    }

    /**
     * Activate a user
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean activateUser(int userId) {
        return userDAO.activateUser(userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a user permanently (hard delete)
     * @param userId The user ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if username already exists
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    /**
     * Check if email already exists
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total user count
     * @return Total number of users
     */
    public int getTotalUserCount() {
        return userDAO.getTotalUserCount();
    }

    /**
     * Get active user count
     * @return Number of active users
     */
    public int getActiveUserCount() {
        return userDAO.getActiveUserCount();
    }

    /**
     * Get user count by role
     * @param role The role to count
     * @return Number of users with the specified role
     */
    public int getUserCountByRole(UserRole role) {
        return userDAO.getUserCountByRole(role);
    }

    // =====================================================
    // HELPER METHODS FOR VIEW
    // =====================================================

    /**
     * Load users for the list view
     */
    public void loadUsers() {
        if (view != null) {
            view.loadUsers();
        }
    }

    /**
     * Refresh the user list
     */
    public void refreshUserList() {
        if (view != null) {
            view.loadUsers();
        }
    }

    /**
     * Validate user data before creating/updating
     * @param username The username
     * @param email The email
     * @param password The password (optional, for new users)
     * @param role The role
     * @return Error message if invalid, null if valid
     */
    public String validateUserData(String username, String email, String password, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters.";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Username can only contain letters, numbers, and underscores.";
        }
        if (usernameExists(username)) {
            return "Username already exists. Please choose another.";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Please enter a valid email address.";
        }
        if (emailExists(email)) {
            return "Email already registered. Please use another.";
        }
        
        if (password != null && !password.isEmpty()) {
            if (password.length() < 6) {
                return "Password must be at least 6 characters.";
            }
        }
        
        if (role == null) {
            return "Role is required.";
        }
        
        return null;
    }

    /**
     * Get role display name
     * @param role The role
     * @return Display name of the role
     */
    public String getRoleDisplayName(UserRole role) {
        if (role == null) return "Unknown";
        switch (role) {
            case ADMIN: return "Administrator";
            case RECEPTION: return "Receptionist";
            case DENTIST: return "Dentist";
            case PATIENT: return "Patient";
            default: return "Unknown";
        }
    }

    /**
     * Get all role names as string array
     * @return Array of role names
     */
    public String[] getAllRoleNames() {
        UserRole[] roles = UserRole.values();
        String[] roleNames = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleNames[i] = roles[i].name();
        }
        return roleNames;
    }

    /**
     * Get role from string
     * @param roleName The role name as string
     * @return UserRole enum value
     */
    public UserRole getRoleFromString(String roleName) {
        try {
            return UserRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // =====================================================
    // PROFILE LINKING METHODS
    // =====================================================

    /**
     * Get the profile type for a user role
     * @param role The user role
     * @return Profile type name
     */
    public String getProfileTypeForRole(UserRole role) {
        if (role == null) return "None";
        switch (role) {
            case ADMIN: return "System User";
            case RECEPTION: return "Staff";
            case DENTIST: return "Dentist";
            case PATIENT: return "Patient";
            default: return "Unknown";
        }
    }

    /**
     * Check if a user has a profile
     * @param userId The user ID
     * @return true if user has a profile, false otherwise
     */
    public boolean hasProfile(int userId) {
        User user = getUserById(userId);
        if (user == null) return false;
        
        // Admin doesn't need a profile
        if (user.isAdmin()) return true;
        
        // Check based on role
        switch (user.getRole()) {
            case RECEPTION:
                return checkStaffProfile(userId);
            case DENTIST:
                return checkDentistProfile(userId);
            case PATIENT:
                return checkPatientProfile(userId);
            default:
                return false;
        }
    }

    private boolean checkStaffProfile(int userId) {
        // This would need a StaffDAO method
        // For now, return true if user exists
        return getUserById(userId) != null;
    }

    private boolean checkDentistProfile(int userId) {
        // This would need a DentistDAO method
        return getUserById(userId) != null;
    }

    private boolean checkPatientProfile(int userId) {
        // This would need a PatientDAO method
        return getUserById(userId) != null;
    }
}
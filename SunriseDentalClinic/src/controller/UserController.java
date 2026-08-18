package controller;

import dao.UserDAO;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.UserManagementPanel;

import java.util.ArrayList;
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
     * Get all users - ADMIN only
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
     * Delete a user permanently (hard delete) - ADMIN only
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

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get users based on user role
     * @param user The current logged-in user
     * @return List of users filtered by role
     */
    public List<User> getUsersForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return userDAO.getUsersForUser(user);
    }

    /**
     * Get users for the current logged-in user
     * @return List of users filtered by current user's role
     */
    public List<User> getUsersForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getUsersForUser(currentUser);
    }

    /**
     * Get user by ID with permission check
     * @param userId The user ID
     * @param currentUser The current logged-in user
     * @return User object if authorized, null otherwise
     */
    public User getUserByIdForUser(int userId, User currentUser) {
        if (currentUser == null) {
            return null;
        }
        return userDAO.getUserByIdForUser(userId, currentUser);
    }

    /**
     * Get user by ID for the current logged-in user
     * @param userId The user ID
     * @return User object if authorized, null otherwise
     */
    public User getUserByIdForCurrentUser(int userId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getUserByIdForUser(userId, currentUser);
    }

    /**
     * Search users with role-based filtering
     * @param searchTerm The search term
     * @param currentUser The current logged-in user
     * @return List of matching users
     */
    public List<User> searchUsersForUser(String searchTerm, User currentUser) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return userDAO.searchUsers(searchTerm, currentUser);
    }

    /**
     * Search users for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching users
     */
    public List<User> searchUsersForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchUsersForUser(searchTerm, currentUser);
    }

    /**
     * Get users with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param currentUser The current logged-in user
     * @return List of users for the page
     */
    public List<User> getUsersForUserPaginated(int page, int pageSize, User currentUser) {
        List<User> allUsers = getUsersForUser(currentUser);
        if (allUsers == null || allUsers.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allUsers.size());
        
        if (start >= allUsers.size()) {
            return new ArrayList<>();
        }
        
        return allUsers.subList(start, end);
    }

    /**
     * Get users by role with role-based filtering
     * @param role The role to filter by
     * @param currentUser The current logged-in user
     * @return List of users with the specified role
     */
    public List<User> getUsersByRoleForUser(UserRole role, User currentUser) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<User> allUsers = getUsersForUser(currentUser);
        if (allUsers == null || allUsers.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getRole() == role) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    /**
     * Get active users with role-based filtering
     * @param currentUser The current logged-in user
     * @return List of active users
     */
    public List<User> getActiveUsersForUser(User currentUser) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<User> allUsers = getUsersForUser(currentUser);
        if (allUsers == null || allUsers.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<User> active = new ArrayList<>();
        for (User user : allUsers) {
            if (user.isActive()) {
                active.add(user);
            }
        }
        return active;
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view users
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewUsers(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_USERS");
    }

    /**
     * Check if user can manage users (view, add, edit, delete)
     * @param user The current user
     * @return true if can manage, false otherwise
     */
    public boolean canManageUsers(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_USERS") ||
               RolePermissions.hasActionPermission(user.getRole(), "ADD_USERS") ||
               RolePermissions.hasActionPermission(user.getRole(), "EDIT_USERS") ||
               RolePermissions.hasActionPermission(user.getRole(), "DELETE_USERS");
    }

    /**
     * Check if user can add a user
     * @param user The current user
     * @return true if can add, false otherwise
     */
    public boolean canAddUser(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_USERS");
    }

    /**
     * Check if user can edit a user
     * @param targetUser The user to edit
     * @param currentUser The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditUser(User targetUser, User currentUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }
        
        // Admin can edit any user
        if (currentUser.isAdmin()) {
            return true;
        }
        
        // Users can only edit themselves
        return targetUser.getUserId() == currentUser.getUserId();
    }

    /**
     * Check if user can delete a user
     * @param targetUser The user to delete
     * @param currentUser The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteUser(User targetUser, User currentUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }
        
        // Only Admin can delete users
        return currentUser.isAdmin();
    }

    /**
     * Check if user can activate/deactivate a user
     * @param targetUser The user to activate/deactivate
     * @param currentUser The current user
     * @return true if can toggle, false otherwise
     */
    public boolean canToggleUserStatus(User targetUser, User currentUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }
        
        // Only Admin can toggle user status
        return currentUser.isAdmin();
    }

    // =====================================================
    // UPDATE METHODS WITH PERMISSION CHECK
    // =====================================================

    /**
     * Update user with permission check
     * @param user The user to update
     * @param currentUser The current logged-in user
     * @return true if successful, false otherwise
     */
    public boolean updateUserForUser(User user, User currentUser) {
        if (!canEditUser(user, currentUser)) {
            return false;
        }
        return userDAO.updateUser(user);
    }

    /**
     * Delete user with permission check
     * @param userId The user ID to delete
     * @param currentUser The current logged-in user
     * @return true if successful, false otherwise
     */
    public boolean deleteUserForUser(int userId, User currentUser) {
        User targetUser = getUserById(userId);
        if (targetUser == null) {
            return false;
        }
        
        if (!canDeleteUser(targetUser, currentUser)) {
            return false;
        }
        
        return userDAO.deleteUser(userId);
    }

    /**
     * Activate user with permission check
     * @param userId The user ID to activate
     * @param currentUser The current logged-in user
     * @return true if successful, false otherwise
     */
    public boolean activateUserForUser(int userId, User currentUser) {
        User targetUser = getUserById(userId);
        if (targetUser == null) {
            return false;
        }
        
        if (!canToggleUserStatus(targetUser, currentUser)) {
            return false;
        }
        
        return userDAO.activateUser(userId);
    }

    /**
     * Deactivate user with permission check
     * @param userId The user ID to deactivate
     * @param currentUser The current logged-in user
     * @return true if successful, false otherwise
     */
    public boolean deactivateUserForUser(int userId, User currentUser) {
        User targetUser = getUserById(userId);
        if (targetUser == null) {
            return false;
        }
        
        if (!canToggleUserStatus(targetUser, currentUser)) {
            return false;
        }
        
        return userDAO.deactivateUser(userId);
    }

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get user count for a user
     * @param currentUser The current logged-in user
     * @return Total number of users for the user
     */
    public int getUserCountForUser(User currentUser) {
        List<User> users = getUsersForUser(currentUser);
        return users != null ? users.size() : 0;
    }

    /**
     * Get user count for the current logged-in user
     * @return Total number of users for the current user
     */
    public int getUserCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getUserCountForUser(currentUser);
    }

    /**
     * Get user count by role for a user
     * @param role The role to count
     * @param currentUser The current logged-in user
     * @return Number of users with the specified role
     */
    public int getUserCountByRoleForUser(UserRole role, User currentUser) {
        List<User> users = getUsersByRoleForUser(role, currentUser);
        return users != null ? users.size() : 0;
    }

    // =====================================================
    // HELPER METHODS FOR VIEW
    // =====================================================

    /**
     * Load users for the list view based on current user
     */
    public void loadUsersForCurrentUser() {
        if (view != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<User> users = getUsersForUser(currentUser);
            view.displayUsers(users);
        }
    }

    /**
     * Get filtered users with role-based access
     * @param searchText The search text
     * @param roleFilter The role filter
     * @param statusFilter The status filter (All, Active, Inactive)
     * @param currentUser The current logged-in user
     * @return List of filtered users
     */
    public List<User> getFilteredUsersForUser(String searchText, String roleFilter, 
                                               String statusFilter, User currentUser) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<User> users = getUsersForUser(currentUser);
        if (users == null || users.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Apply role filter
        if (roleFilter != null && !roleFilter.isEmpty() && !roleFilter.equals("All Roles")) {
            UserRole role = getRoleFromString(roleFilter);
            if (role != null) {
                users.removeIf(u -> u.getRole() != role);
            }
        }
        
        // Apply status filter
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("All")) {
            if ("Active".equals(statusFilter)) {
                users.removeIf(u -> !u.isActive());
            } else if ("Inactive".equals(statusFilter)) {
                users.removeIf(u -> u.isActive());
            }
        }
        
        // Apply search filter
        if (searchText != null && !searchText.isEmpty()) {
            String searchLower = searchText.toLowerCase().trim();
            users.removeIf(u -> {
                boolean usernameMatch = u.getUsername() != null && 
                                      u.getUsername().toLowerCase().contains(searchLower);
                boolean emailMatch = u.getEmail() != null && 
                                   u.getEmail().toLowerCase().contains(searchLower);
                boolean roleMatch = u.getRole() != null && 
                                  u.getRole().name().toLowerCase().contains(searchLower);
                return !usernameMatch && !emailMatch && !roleMatch;
            });
        }
        
        return users;
    }

    /**
     * Validate user data for update (checking username/email uniqueness excluding current user)
     * @param username The username
     * @param email The email
     * @param userId The user ID to exclude
     * @param password The password (optional)
     * @param role The role
     * @return Error message if invalid, null if valid
     */
    public String validateUserDataForUpdate(String username, String email, String password, 
                                            UserRole role, int userId) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters.";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "Username can only contain letters, numbers, and underscores.";
        }
        
        // Check username uniqueness excluding current user
        if (userDAO.usernameExists(username, userId)) {
            return "Username already exists. Please choose another.";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Please enter a valid email address.";
        }
        
        // Check email uniqueness excluding current user
        if (userDAO.emailExists(email)) {
            // Check if it's the same user's email
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null && existingUser.getUserId() != userId) {
                return "Email already registered. Please use another.";
            }
        }
        
        if (password != null && !password.isEmpty() && password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        
        if (role == null) {
            return "Role is required.";
        }
        
        return null;
    }
}
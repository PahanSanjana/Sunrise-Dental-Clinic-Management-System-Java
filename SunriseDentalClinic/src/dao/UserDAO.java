package dao;

import db.DBconnection;
import model.User;
import model.User.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDAO {

    // =====================================================
    // AUTHENTICATION METHODS
    // =====================================================
    
    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The plain text password
     * @return User object if authenticated, null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = 1";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                // For demo purposes, comparing plain text
                // In production, use proper password hashing with salt
                if (password.equals(user.getPasswordHash())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // =====================================================
    // CREATE USER WITH PROFILE (ADMIN METHOD)
    // =====================================================

    /**
     * Create a user with profile based on role
     * @param username Username
     * @param password Plain password (will be hashed in production)
     * @param email Email address
     * @param role User role
     * @param createdBy User ID of creator
     * @param profileData Map of profile data (role-specific)
     * @return Created User object, null if failed
     */
    public User createUserWithProfile(String username, String password, String email,
                                      UserRole role, int createdBy, Map<String, Object> profileData) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Create user
            String salt = "salt_" + System.currentTimeMillis();
            String userSql = "INSERT INTO users (username, password_hash, salt, email, role, is_active, created_by) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In production, hash this
            pstmt.setString(3, salt);
            pstmt.setString(4, email);
            pstmt.setString(5, role.name());
            pstmt.setBoolean(6, true);
            
            // Handle created_by - if 0 or negative, set to NULL
            if (createdBy > 0) {
                pstmt.setInt(7, createdBy);
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                conn.rollback();
                return null;
            }
            
            rs = pstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            } else {
                conn.rollback();
                return null;
            }
            
            // Close the statement
            pstmt.close();
            
            // If this is the first user (created_by was NULL), update to self
            if (createdBy <= 0) {
                updateCreatedBySelf(conn, userId);
            }
            
            // 2. Create/Update profile based on role
            boolean profileCreated = false;
            
            switch (role) {
                case ADMIN:
                    // No profile needed for admin
                    profileCreated = true;
                    break;
                    
                case RECEPTION:
                    profileCreated = createStaffProfile(conn, userId, profileData);
                    break;
                    
                case DENTIST:
                    // ✅ FIX: Use UPDATE instead of INSERT for dentist
                    profileCreated = updateDentistProfile(conn, userId, profileData);
                    break;
                    
                case PATIENT:
                    profileCreated = createPatientProfile(conn, userId, profileData);
                    break;
                    
                default:
                    profileCreated = false;
                    break;
            }
            
            if (!profileCreated) {
                conn.rollback();
                return null;
            }
            
            conn.commit();
            return getUserById(userId);
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error creating user with profile: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update created_by to self for the first user
     */
    private void updateCreatedBySelf(Connection conn, int userId) {
        String sql = "UPDATE users SET created_by = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating created_by: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // CREATE PATIENT USER (SIGNUP METHOD)
    // =====================================================

    /**
     * Create a new patient user with default PATIENT role (for signup)
     * @param username The username
     * @param passwordHash The hashed password (plain text for demo)
     * @param salt The salt
     * @param fullName The full name
     * @param email The email
     * @param phone The phone number
     * @return The created User object if successful, null otherwise
     */
    public User createPatientUser(String username, String passwordHash, String salt, 
                                  String fullName, String email, String phone) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            // First, create the user with PATIENT role
            String userSql = "INSERT INTO users (username, password_hash, salt, email, role, is_active, created_by) "
                           + "VALUES (?, ?, ?, ?, 'PATIENT', 1, NULL)";
            pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, salt);
            pstmt.setString(4, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                conn.rollback();
                return null;
            }
            
            rs = pstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            } else {
                conn.rollback();
                return null;
            }
            
            // Close the first statement
            pstmt.close();
            
            // Update created_by to self
            updateCreatedBySelf(conn, userId);
            
            // Then, create the patient record
            String patientSql = "INSERT INTO patients (user_id, patient_name, email, contact_number) "
                              + "VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(patientSql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            
            pstmt.executeUpdate();
            
            conn.commit();
            
            // Return the created user
            return getUserById(userId);
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error creating patient user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // PROFILE CREATION METHODS (For Admin)
    // =====================================================

    /**
     * Create staff profile for a user
     * @param conn Database connection
     * @param userId User ID to link
     * @param data Profile data map
     * @return true if successful, false otherwise
     */
    private boolean createStaffProfile(Connection conn, int userId, Map<String, Object> data) {
        try {
            String sql = "INSERT INTO staff (user_id, first_name, last_name, position, department, "
                       + "phone, email, hire_date, salary, is_active) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, (String) data.getOrDefault("firstName", ""));
            pstmt.setString(3, (String) data.getOrDefault("lastName", ""));
            pstmt.setString(4, (String) data.getOrDefault("position", ""));
            pstmt.setString(5, (String) data.getOrDefault("department", ""));
            pstmt.setString(6, (String) data.getOrDefault("phone", ""));
            pstmt.setString(7, (String) data.getOrDefault("email", ""));
            
            // Handle hire date
            Date hireDate = (Date) data.get("hireDate");
            if (hireDate == null) {
                hireDate = Date.valueOf(java.time.LocalDate.now());
            }
            pstmt.setDate(8, hireDate);
            
            pstmt.setDouble(9, (Double) data.getOrDefault("salary", 0.0));
            pstmt.setBoolean(10, true);
            
            boolean result = pstmt.executeUpdate() > 0;
            pstmt.close();
            return result;
        } catch (SQLException e) {
            System.err.println("Error creating staff profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ FIXED: UPDATE dentist profile with user_id (instead of INSERT)
     * @param conn Database connection
     * @param userId User ID to link
     * @param data Profile data map
     * @return true if successful, false otherwise
     */
    private boolean updateDentistProfile(Connection conn, int userId, Map<String, Object> data) {
        try {
            // ✅ UPDATE instead of INSERT - links user to existing dentist
            String sql = "UPDATE dentists SET user_id = ? WHERE license_number = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, (String) data.get("licenseNumber"));
            
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            
            if (rowsAffected == 0) {
                System.err.println("No dentist found with license number: " + data.get("licenseNumber"));
                return false;
            }
            
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating dentist profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create patient profile for a user
     * @param conn Database connection
     * @param userId User ID to link
     * @param data Profile data map
     * @return true if successful, false otherwise
     */
    private boolean createPatientProfile(Connection conn, int userId, Map<String, Object> data) {
        try {
            String sql = "INSERT INTO patients (user_id, patient_name, gender, address, contact_number, "
                       + "email, date_of_birth, emergency_contact, emergency_phone, "
                       + "medical_history, allergies) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, (String) data.getOrDefault("patientName", ""));
            
            // Validate gender - must be 'Male', 'Female', or 'Other'
            String gender = (String) data.getOrDefault("gender", "Other");
            if (!gender.equals("Male") && !gender.equals("Female") && !gender.equals("Other")) {
                gender = "Other";
            }
            pstmt.setString(3, gender);
            
            pstmt.setString(4, (String) data.getOrDefault("address", ""));
            pstmt.setString(5, (String) data.getOrDefault("contactNumber", ""));
            pstmt.setString(6, (String) data.getOrDefault("email", ""));
            
            // Handle date of birth
            Date dob = (Date) data.get("dateOfBirth");
            if (dob == null) {
                dob = Date.valueOf(java.time.LocalDate.now().minusYears(18));
            }
            pstmt.setDate(7, dob);
            
            pstmt.setString(8, (String) data.getOrDefault("emergencyContact", ""));
            pstmt.setString(9, (String) data.getOrDefault("emergencyPhone", ""));
            pstmt.setString(10, (String) data.getOrDefault("medicalHistory", ""));
            pstmt.setString(11, (String) data.getOrDefault("allergies", ""));
            
            boolean result = pstmt.executeUpdate() > 0;
            pstmt.close();
            return result;
        } catch (SQLException e) {
            System.err.println("Error creating patient profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================
    // CREATE METHODS
    // =====================================================
    
    /**
     * Create a new user (without profile)
     * @param user The user to create
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, salt, email, role, is_active, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());
            
            if (user.getCreatedBy() > 0) {
                pstmt.setInt(7, user.getCreatedBy());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // READ METHODS
    // =====================================================
    
    /**
     * Get user by ID
     * @param userId The user ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get user by username
     * @param username The username
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get user by email
     * @param email The email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get users by role
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY username";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get active users
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = 1 ORDER BY username";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
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
        String sql = "UPDATE users SET username=?, email=?, role=?, is_active=? WHERE user_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setInt(5, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Change user password
     * @param userId The user ID
     * @param newPasswordHash The new password hash
     * @param newSalt The new salt
     * @return true if successful, false otherwise
     */
    public boolean changePassword(int userId, String newPasswordHash, String newSalt) {
        String sql = "UPDATE users SET password_hash=?, salt=? WHERE user_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, newSalt);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deactivate a user (soft delete)
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Activate a user
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean activateUser(int userId) {
        String sql = "UPDATE users SET is_active = 1 WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error activating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if email already exists
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total user count
     * @return Total number of users
     */
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting total users: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get active user count
     * @return Number of active users
     */
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_active = 1";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active users: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get user count by role
     * @param role The role to count
     * @return Number of users with the specified role
     */
    public int getUserCountByRole(UserRole role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND is_active = 1";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to User object
     * @param rs The ResultSet
     * @return User object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setEmail(rs.getString("email"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedBy(rs.getInt("created_by"));
        user.setCreatedAt(rs.getString("created_at"));
        user.setUpdatedAt(rs.getString("updated_at"));
        return user;
    }
}
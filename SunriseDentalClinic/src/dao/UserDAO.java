package dao;

import db.DBconnection;
import model.User;
import model.User.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                
                // For demo purposes, we're comparing plain text
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
     * Get active users by role
     * @param role The role to filter by
     * @return List of active users with the specified role
     */
    public List<User> getActiveUsersByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND is_active = 1 ORDER BY username";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active users by role: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // =====================================================
    // CREATE METHODS
    // =====================================================
    
    /**
     * Create a new user
     * @param user The user to create
     * @return true if successful, false otherwise
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, salt, role, is_active) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getRole().name());
            pstmt.setBoolean(5, user.isActive());
            
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

    /**
     * Create a new patient user with default PATIENT role
     * This creates both a user account and a patient record
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
            String userSql = "INSERT INTO users (username, password_hash, salt, role, is_active) "
                           + "VALUES (?, ?, ?, 'PATIENT', 1)";
            pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, salt);
            
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
            
            // Then, create the patient record
            String patientSql = "INSERT INTO patients (user_id, first_name, last_name, email, phone) "
                              + "VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(patientSql);
            pstmt.setInt(1, userId);
            
            // Split full name into first and last name
            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            
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
    // UPDATE METHODS
    // =====================================================
    
    /**
     * Update an existing user
     * @param user The user to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, role = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getRole().name());
            pstmt.setBoolean(3, user.isActive());
            pstmt.setInt(4, user.getUserId());
            
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
        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
        
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

    // =====================================================
    // DELETE / DEACTIVATE METHODS
    // =====================================================
    
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

    /**
     * Delete a user permanently
     * @param userId The user ID
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
    // VALIDATION / CHECK METHODS
    // =====================================================
    
    /**
     * Check if username already exists (case insensitive)
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
     * Check if email already exists in patients table
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM patients WHERE email = ?";
        
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

    /**
     * Check if phone already exists in patients table
     * @param phone The phone number to check
     * @return true if exists, false otherwise
     */
    public boolean phoneExists(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM patients WHERE phone = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================
    
    /**
     * Get count of users by role
     * @param role The role to count
     * @return Number of users with the specified role
     */
    public int countUsersByRole(UserRole role) {
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

    /**
     * Get total count of active users
     * @return Total number of active users
     */
    public int countActiveUsers() {
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
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("salt"),
            UserRole.valueOf(rs.getString("role")),
            rs.getBoolean("is_active"),
            rs.getString("created_at")
        );
    }
}
package dao;

import db.DBconnection;
import model.Staff;
import model.User;
import model.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    // =====================================================
    // CREATE METHODS
    // =====================================================

    /**
     * Add a new staff member to the database
     * @param staff The staff object to save
     * @return true if successful, false otherwise
     */
    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (first_name, last_name, position, department, "
                   + "phone, email, hire_date, salary, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getPosition());
            pstmt.setString(4, staff.getDepartment());
            pstmt.setString(5, staff.getPhone());
            pstmt.setString(6, staff.getEmail());
            pstmt.setDate(7, staff.getHireDate());
            pstmt.setDouble(8, staff.getSalary());
            pstmt.setBoolean(9, staff.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    staff.setStaffId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    /**
     * Get staff by ID
     * @param staffId The staff ID
     * @return Staff object if found, null otherwise
     */
    public Staff getStaffById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get staff by user ID
     * @param userId The user ID
     * @return Staff object if found, null otherwise
     */
    public Staff getStaffByUserId(int userId) {
        String sql = "SELECT * FROM staff WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all staff members - ADMIN only
     * @return List of all staff
     */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY last_name, first_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get staff by position
     * @param position The position to filter by
     * @return List of staff with the specified position
     */
    public List<Staff> getStaffByPosition(String position) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE position = ? ORDER BY last_name, first_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by position: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get staff by department
     * @param department The department to filter by
     * @return List of staff in the specified department
     */
    public List<Staff> getStaffByDepartment(String department) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE department = ? ORDER BY last_name, first_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff by department: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get active staff members
     * @return List of active staff
     */
    public List<Staff> getActiveStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE is_active = true ORDER BY last_name, first_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get staff based on user role
     * @param user The current logged-in user
     * @return List of staff filtered by role
     */
    public List<Staff> getStaffForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                // Admin can see all staff
                return getAllStaff();
                
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                // Other roles can only see their own staff record (if they are staff)
                // Patient doesn't have staff record
                if (user.getStaffId() != null && user.getStaffId() > 0) {
                    Staff self = getStaffById(user.getStaffId());
                    List<Staff> result = new ArrayList<>();
                    if (self != null) {
                        result.add(self);
                    }
                    return result;
                }
                return new ArrayList<>();
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get staff by ID with permission check
     * @param staffId The staff ID
     * @param user The current user
     * @return Staff object if authorized, null otherwise
     */
    public Staff getStaffByIdForUser(int staffId, User user) {
        if (user == null) {
            return null;
        }
        
        Staff staff = getStaffById(staffId);
        if (staff == null) {
            return null;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                // Admin can view any staff
                return staff;
                
            case RECEPTION:
            case DENTIST:
                // Staff can only view themselves
                if (user.getStaffId() != null && staffId == user.getStaffId()) {
                    return staff;
                }
                return null;
                
            case PATIENT:
                // Patient cannot view staff
                return null;
                
            default:
                return null;
        }
    }

    /**
     * Search staff by name with role-based filtering
     * @param searchTerm The search term
     * @param user The current user for role-based filtering
     * @return List of matching staff
     */
    public List<Staff> searchStaff(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Staff> allStaff = getStaffForUser(user);
        if (allStaff == null || allStaff.isEmpty() || searchTerm == null || searchTerm.isEmpty()) {
            return allStaff;
        }
        
        String searchLower = searchTerm.toLowerCase().trim();
        List<Staff> filtered = new ArrayList<>();
        
        for (Staff s : allStaff) {
            String fullName = (s.getFirstName() + " " + s.getLastName()).toLowerCase();
            if (fullName.contains(searchLower)) {
                filtered.add(s);
            } else if (s.getPosition() != null && s.getPosition().toLowerCase().contains(searchLower)) {
                filtered.add(s);
            } else if (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(searchLower)) {
                filtered.add(s);
            } else if (s.getPhone() != null && s.getPhone().contains(searchTerm)) {
                filtered.add(s);
            } else if (s.getEmail() != null && s.getEmail().toLowerCase().contains(searchLower)) {
                filtered.add(s);
            }
        }
        
        return filtered;
    }

    /**
     * Search staff by name (original method - kept for compatibility)
     * @param searchTerm The search term
     * @return List of matching staff
     */
    public List<Staff> searchStaff(String searchTerm) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE first_name LIKE ? OR last_name LIKE ? OR position LIKE ? OR department LIKE ? OR phone LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get staff with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of staff
     */
    public List<Staff> getStaffPaginated(int offset, int limit) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY last_name, first_name LIMIT ? OFFSET ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting staff with pagination: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Get recent staff members
     * @param limit Number of recent staff to get
     * @return List of recent staff
     */
    public List<Staff> getRecentStaff(int limit) {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY created_at DESC LIMIT ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent staff: " + e.getMessage());
            e.printStackTrace();
        }
        return staffList;
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update staff information
     * @param staff The staff to update
     * @return true if successful, false otherwise
     */
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET first_name=?, last_name=?, position=?, department=?, "
                   + "phone=?, email=?, hire_date=?, salary=?, is_active=? "
                   + "WHERE staff_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getPosition());
            pstmt.setString(4, staff.getDepartment());
            pstmt.setString(5, staff.getPhone());
            pstmt.setString(6, staff.getEmail());
            pstmt.setDate(7, staff.getHireDate());
            pstmt.setDouble(8, staff.getSalary());
            pstmt.setBoolean(9, staff.isActive());
            pstmt.setInt(10, staff.getStaffId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deactivate a staff member (soft delete)
     * @param staffId The staff ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateStaff(int staffId) {
        String sql = "UPDATE staff SET is_active = false WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Activate a staff member
     * @param staffId The staff ID
     * @return true if successful, false otherwise
     */
    public boolean activateStaff(int staffId) {
        String sql = "UPDATE staff SET is_active = true WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error activating staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Link staff to a user account
     * @param staffId The staff ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkStaffToUser(int staffId, int userId) {
        String sql = "UPDATE staff SET user_id = ? WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error linking staff to user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a staff member - ADMIN only
     * @param staffId The staff ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Unlink staff from a user account
     * @param staffId The staff ID
     * @return true if successful, false otherwise
     */
    public boolean unlinkStaffFromUser(int staffId) {
        String sql = "UPDATE staff SET user_id = NULL WHERE staff_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unlinking staff from user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get staff count
     * @return Total number of staff
     */
    public int getStaffCount() {
        String sql = "SELECT COUNT(*) FROM staff";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting staff: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get active staff count
     * @return Number of active staff
     */
    public int getActiveStaffCount() {
        String sql = "SELECT COUNT(*) FROM staff WHERE is_active = true";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active staff: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get staff count by position
     * @param position The position to count
     * @return Number of staff with the specified position
     */
    public int getStaffCountByPosition(String position) {
        String sql = "SELECT COUNT(*) FROM staff WHERE position = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting staff by position: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get staff count by department
     * @param department The department to count
     * @return Number of staff in the specified department
     */
    public int getStaffCountByDepartment(String department) {
        String sql = "SELECT COUNT(*) FROM staff WHERE department = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, department);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting staff by department: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if email already exists for another staff member
     * @param email The email to check
     * @param excludeStaffId Staff ID to exclude from check (for updates)
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email, int excludeStaffId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE email = ? AND staff_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeStaffId);
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
     * Check if phone already exists for another staff member
     * @param phone The phone to check
     * @param excludeStaffId Staff ID to exclude from check (for updates)
     * @return true if exists, false otherwise
     */
    public boolean phoneExists(String phone, int excludeStaffId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE phone = ? AND staff_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            pstmt.setInt(2, excludeStaffId);
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
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Staff object
     * @param rs The ResultSet
     * @return Staff object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        
        // Handle NULL user_id
        int userId = rs.getInt("user_id");
        if (rs.wasNull()) {
            staff.setUserId(-1);
        } else {
            staff.setUserId(userId);
        }
        
        staff.setFirstName(rs.getString("first_name"));
        staff.setLastName(rs.getString("last_name"));
        staff.setPosition(rs.getString("position"));
        staff.setDepartment(rs.getString("department"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setHireDate(rs.getDate("hire_date"));
        staff.setSalary(rs.getDouble("salary"));
        staff.setActive(rs.getBoolean("is_active"));
        staff.setCreatedAt(rs.getString("created_at"));
        staff.setUpdatedAt(rs.getString("updated_at"));
        return staff;
    }
}
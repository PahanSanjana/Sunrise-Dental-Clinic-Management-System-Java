package dao;

import db.DBconnection;
import model.Dentist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    // =====================================================
    // CREATE METHODS
    // =====================================================

    /**
     * Add a new dentist to the database
     * @param dentist The dentist object to save
     * @return true if successful, false otherwise
     */
    public boolean addDentist(Dentist dentist) {
        String sql = "INSERT INTO dentists (dentist_name, specialization, license_number, "
                   + "working_hours, phone, email, years_of_experience, consultation_fee, "
                   + "is_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, dentist.getDentistName());
            pstmt.setString(2, dentist.getSpecialization());
            pstmt.setString(3, dentist.getLicenseNumber());
            pstmt.setString(4, dentist.getWorkingHours());
            pstmt.setString(5, dentist.getPhone());
            pstmt.setString(6, dentist.getEmail());
            pstmt.setInt(7, dentist.getYearsOfExperience());
            pstmt.setDouble(8, dentist.getConsultationFee());
            pstmt.setBoolean(9, dentist.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    dentist.setDentistId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    /**
     * Get dentist by ID
     * @param dentistId The dentist ID
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDentist(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentist by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get dentist by user ID
     * @param userId The user ID
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistByUserId(int userId) {
        String sql = "SELECT * FROM dentists WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDentist(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentist by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get dentist by license number
     * @param licenseNumber The license number
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistByLicenseNumber(String licenseNumber) {
        String sql = "SELECT * FROM dentists WHERE license_number = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDentist(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentist by license number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all dentists
     * @return List of all dentists
     */
    public List<Dentist> getAllDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY dentist_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Get available dentists
     * @return List of available dentists
     */
    public List<Dentist> getAvailableDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE is_available = true ORDER BY dentist_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Get dentists by specialization
     * @param specialization The specialization to filter by
     * @return List of dentists with the specified specialization
     */
    public List<Dentist> getDentistsBySpecialization(String specialization) {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE specialization = ? ORDER BY dentist_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentists by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Search dentists by name or specialization
     * @param searchTerm The search term
     * @return List of matching dentists
     */
    public List<Dentist> searchDentists(String searchTerm) {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE dentist_name LIKE ? OR specialization LIKE ? OR phone LIKE ? OR email LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Get dentists with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of dentists
     */
    public List<Dentist> getDentistsPaginated(int offset, int limit) {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY dentist_name LIMIT ? OFFSET ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting dentists with pagination: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Get recent dentists
     * @param limit Number of recent dentists to get
     * @return List of recent dentists
     */
    public List<Dentist> getRecentDentists(int limit) {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY created_at DESC LIMIT ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dentists.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return dentists;
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update dentist information
     * @param dentist The dentist to update
     * @return true if successful, false otherwise
     */
    public boolean updateDentist(Dentist dentist) {
        String sql = "UPDATE dentists SET dentist_name=?, specialization=?, license_number=?, "
                   + "working_hours=?, phone=?, email=?, years_of_experience=?, consultation_fee=?, "
                   + "is_available=? WHERE dentist_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dentist.getDentistName());
            pstmt.setString(2, dentist.getSpecialization());
            pstmt.setString(3, dentist.getLicenseNumber());
            pstmt.setString(4, dentist.getWorkingHours());
            pstmt.setString(5, dentist.getPhone());
            pstmt.setString(6, dentist.getEmail());
            pstmt.setInt(7, dentist.getYearsOfExperience());
            pstmt.setDouble(8, dentist.getConsultationFee());
            pstmt.setBoolean(9, dentist.isAvailable());
            pstmt.setInt(10, dentist.getDentistId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update dentist availability
     * @param dentistId The dentist ID
     * @param isAvailable The availability status
     * @return true if successful, false otherwise
     */
    public boolean updateAvailability(int dentistId, boolean isAvailable) {
        String sql = "UPDATE dentists SET is_available = ? WHERE dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, dentistId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Link a dentist to a user account
     * @param dentistId The dentist ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkDentistToUser(int dentistId, int userId) {
        String sql = "UPDATE dentists SET user_id = ? WHERE dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, dentistId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error linking dentist to user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a dentist
     * @param dentistId The dentist ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDentist(int dentistId) {
        String sql = "DELETE FROM dentists WHERE dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Unlink a dentist from a user account
     * @param dentistId The dentist ID
     * @return true if successful, false otherwise
     */
    public boolean unlinkDentistFromUser(int dentistId) {
        String sql = "UPDATE dentists SET user_id = NULL WHERE dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unlinking dentist from user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if license number already exists
     * @param licenseNumber The license number to check
     * @return true if exists, false otherwise
     */
    public boolean licenseNumberExists(String licenseNumber) {
        String sql = "SELECT COUNT(*) FROM dentists WHERE license_number = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking license number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if license number already exists for another dentist
     * @param licenseNumber The license number to check
     * @param excludeDentistId Dentist ID to exclude from check (for updates)
     * @return true if exists, false otherwise
     */
    public boolean licenseNumberExists(String licenseNumber, int excludeDentistId) {
        String sql = "SELECT COUNT(*) FROM dentists WHERE license_number = ? AND dentist_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseNumber);
            pstmt.setInt(2, excludeDentistId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking license number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if email already exists for another dentist
     * @param email The email to check
     * @param excludeDentistId Dentist ID to exclude from check (for updates)
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email, int excludeDentistId) {
        String sql = "SELECT COUNT(*) FROM dentists WHERE email = ? AND dentist_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeDentistId);
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
     * Check if phone already exists for another dentist
     * @param phone The phone to check
     * @param excludeDentistId Dentist ID to exclude from check (for updates)
     * @return true if phone exists, false otherwise
     */
    public boolean phoneExists(String phone, int excludeDentistId) {
        String sql = "SELECT COUNT(*) FROM dentists WHERE phone = ? AND dentist_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            pstmt.setInt(2, excludeDentistId);
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
     * Get count of all dentists
     * @return Total number of dentists
     */
    public int getDentistCount() {
        String sql = "SELECT COUNT(*) FROM dentists";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of available dentists
     * @return Number of available dentists
     */
    public int getAvailableDentistCount() {
        String sql = "SELECT COUNT(*) FROM dentists WHERE is_available = true";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting available dentists: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get count of dentists by specialization
     * @param specialization The specialization to count
     * @return Number of dentists with the specified specialization
     */
    public int getDentistCountBySpecialization(String specialization) {
        String sql = "SELECT COUNT(*) FROM dentists WHERE specialization = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting dentists by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Dentist object
     * @param rs The ResultSet
     * @return Dentist object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Dentist mapResultSetToDentist(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist();
        dentist.setDentistId(rs.getInt("dentist_id"));
        dentist.setDentistName(rs.getString("dentist_name"));
        dentist.setSpecialization(rs.getString("specialization"));
        dentist.setLicenseNumber(rs.getString("license_number"));
        dentist.setWorkingHours(rs.getString("working_hours"));
        dentist.setPhone(rs.getString("phone"));
        dentist.setEmail(rs.getString("email"));
        dentist.setYearsOfExperience(rs.getInt("years_of_experience"));
        dentist.setConsultationFee(rs.getDouble("consultation_fee"));
        dentist.setAvailable(rs.getBoolean("is_available"));
        
        // Handle NULL user_id
        int userId = rs.getInt("user_id");
        if (rs.wasNull()) {
            dentist.setUserId(-1);
        } else {
            dentist.setUserId(userId);
        }
        
        dentist.setCreatedAt(rs.getString("created_at"));
        dentist.setUpdatedAt(rs.getString("updated_at"));
        return dentist;
    }
}
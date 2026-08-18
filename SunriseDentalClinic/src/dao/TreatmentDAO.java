package dao;

import db.DBconnection;
import model.Treatment;
import model.User;
import model.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    /**
     * Add a new treatment to the database
     */
    public boolean addTreatment(Treatment treatment) {
        // ✅ FIXED: Using 'duration' (not 'estimated_duration')
        String sql = "INSERT INTO treatments (treatment_name, description, category, cost, duration, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, treatment.getTreatmentName());
            pstmt.setString(2, treatment.getDescription());
            pstmt.setString(3, treatment.getCategory());
            pstmt.setDouble(4, treatment.getCost());
            pstmt.setInt(5, treatment.getDuration()); // ✅ Using duration
            pstmt.setBoolean(6, treatment.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    treatment.setTreatmentId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding treatment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get treatment by ID
     */
    public Treatment getTreatmentById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, treatmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTreatment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting treatment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get treatment by name
     */
    public Treatment getTreatmentByName(String treatmentName) {
        String sql = "SELECT * FROM treatments WHERE treatment_name = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, treatmentName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTreatment(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting treatment by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all treatments - ADMIN, RECEPTION, DENTIST, PATIENT can all view treatments
     * @return List of all treatments
     */
    public List<Treatment> getAllTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY treatment_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all treatments: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Get active treatments
     */
    public List<Treatment> getActiveTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE is_active = true ORDER BY treatment_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active treatments: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Get treatments by category
     */
    public List<Treatment> getTreatmentsByCategory(String category) {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE category = ? ORDER BY treatment_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting treatments by category: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Get treatments based on user role
     * @param user The current logged-in user
     * @return List of treatments filtered by role
     */
    public List<Treatment> getTreatmentsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                // All roles can view all treatments
                // But ADMIN and RECEPTION can also add/edit
                return getAllTreatments();
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get treatment by ID with permission check
     * @param treatmentId The treatment ID
     * @param user The current user
     * @return Treatment object if authorized, null otherwise
     */
    public Treatment getTreatmentByIdForUser(int treatmentId, User user) {
        if (user == null) {
            return null;
        }
        
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            return null;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
            case DENTIST:
            case PATIENT:
                // All roles can view treatment details
                return treatment;
                
            default:
                return null;
        }
    }

    /**
     * Search treatments by name or category with role-based filtering
     * @param searchTerm The search term
     * @param user The current user for role-based filtering
     * @return List of matching treatments
     */
    public List<Treatment> searchTreatments(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Treatment> allTreatments = getTreatmentsForUser(user);
        if (allTreatments == null || allTreatments.isEmpty() || searchTerm == null || searchTerm.isEmpty()) {
            return allTreatments;
        }
        
        String searchLower = searchTerm.toLowerCase().trim();
        List<Treatment> filtered = new ArrayList<>();
        
        for (Treatment t : allTreatments) {
            if (t.getTreatmentName() != null && t.getTreatmentName().toLowerCase().contains(searchLower)) {
                filtered.add(t);
            } else if (t.getCategory() != null && t.getCategory().toLowerCase().contains(searchLower)) {
                filtered.add(t);
            } else if (t.getDescription() != null && t.getDescription().toLowerCase().contains(searchLower)) {
                filtered.add(t);
            }
        }
        
        return filtered;
    }

    /**
     * Search treatments by name or category (original method - kept for compatibility)
     */
    public List<Treatment> searchTreatments(String searchTerm) {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE treatment_name LIKE ? OR category LIKE ? OR description LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching treatments: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Update treatment information
     */
    public boolean updateTreatment(Treatment treatment) {
        // ✅ FIXED: Using 'duration' (not 'estimated_duration')
        String sql = "UPDATE treatments SET treatment_name=?, description=?, category=?, "
                   + "cost=?, duration=?, is_active=? WHERE treatment_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, treatment.getTreatmentName());
            pstmt.setString(2, treatment.getDescription());
            pstmt.setString(3, treatment.getCategory());
            pstmt.setDouble(4, treatment.getCost());
            pstmt.setInt(5, treatment.getDuration()); // ✅ Using duration
            pstmt.setBoolean(6, treatment.isActive());
            pstmt.setInt(7, treatment.getTreatmentId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating treatment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a treatment - ADMIN only
     */
    public boolean deleteTreatment(int treatmentId) {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, treatmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting treatment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deactivate a treatment
     */
    public boolean deactivateTreatment(int treatmentId) {
        String sql = "UPDATE treatments SET is_active = false WHERE treatment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, treatmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating treatment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Activate a treatment
     */
    public boolean activateTreatment(int treatmentId) {
        String sql = "UPDATE treatments SET is_active = true WHERE treatment_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, treatmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error activating treatment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if treatment name already exists
     */
    public boolean treatmentNameExists(String treatmentName) {
        String sql = "SELECT COUNT(*) FROM treatments WHERE treatment_name = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, treatmentName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking treatment name existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if treatment name already exists for another treatment
     * @param treatmentName The treatment name to check
     * @param excludeTreatmentId Treatment ID to exclude from check (for updates)
     * @return true if exists, false otherwise
     */
    public boolean treatmentNameExists(String treatmentName, int excludeTreatmentId) {
        String sql = "SELECT COUNT(*) FROM treatments WHERE treatment_name = ? AND treatment_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, treatmentName);
            pstmt.setInt(2, excludeTreatmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking treatment name existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get treatment count
     */
    public int getTreatmentCount() {
        String sql = "SELECT COUNT(*) FROM treatments";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting treatments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get active treatment count
     */
    public int getActiveTreatmentCount() {
        String sql = "SELECT COUNT(*) FROM treatments WHERE is_active = true";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active treatments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get treatments with pagination
     */
    public List<Treatment> getTreatmentsPaginated(int offset, int limit) {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY treatment_name LIMIT ? OFFSET ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting treatments with pagination: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Get categories with treatment count
     */
    public List<Object[]> getCategoryStats() {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT category, COUNT(*) as count FROM treatments GROUP BY category ORDER BY category";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                stats.add(new Object[]{
                    rs.getString("category"),
                    rs.getInt("count")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting category stats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Get treatments by multiple categories
     * @param categories List of categories to filter by
     * @return List of treatments in the specified categories
     */
    public List<Treatment> getTreatmentsByCategories(List<String> categories) {
        List<Treatment> treatments = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            return treatments;
        }
        
        String placeholders = String.join(",", java.util.Collections.nCopies(categories.size(), "?"));
        String sql = "SELECT * FROM treatments WHERE category IN (" + placeholders + ") ORDER BY treatment_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < categories.size(); i++) {
                pstmt.setString(i + 1, categories.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                treatments.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting treatments by categories: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

    /**
     * Map ResultSet to Treatment object
     */
    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        // ✅ FIXED: Using 'duration' (not 'estimated_duration')
        return new Treatment(
            rs.getInt("treatment_id"),
            rs.getString("treatment_name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getDouble("cost"),
            rs.getInt("duration"), // ✅ FIXED: was 'estimated_duration'
            rs.getBoolean("is_active"),
            rs.getString("created_at"),
            rs.getString("updated_at")
        );
    }
}
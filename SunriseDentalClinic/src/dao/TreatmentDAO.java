package dao;

import db.DBconnection;
import model.Treatment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    /**
     * Add a new treatment to the database
     * @param treatment The treatment object to save
     * @return true if successful, false otherwise
     */
    public boolean addTreatment(Treatment treatment) {
        String sql = "INSERT INTO treatments (treatment_name, description, category, cost, estimated_duration, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, treatment.getTreatmentName());
            pstmt.setString(2, treatment.getDescription());
            pstmt.setString(3, treatment.getCategory());
            pstmt.setDouble(4, treatment.getCost());
            pstmt.setInt(5, treatment.getDuration());
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
     * @param treatmentId The treatment ID
     * @return Treatment object if found, null otherwise
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
     * @param treatmentName The treatment name
     * @return Treatment object if found, null otherwise
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
     * Get all treatments
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
     * @return List of active treatments
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
     * @param category The category to filter by
     * @return List of treatments in the specified category
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
     * Search treatments by name or category
     * @param searchTerm The search term
     * @return List of matching treatments
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
     * @param treatment The treatment to update
     * @return true if successful, false otherwise
     */
    public boolean updateTreatment(Treatment treatment) {
        String sql = "UPDATE treatments SET treatment_name=?, description=?, category=?, "
                   + "cost=?, estimated_duration=?, is_active=? WHERE treatment_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, treatment.getTreatmentName());
            pstmt.setString(2, treatment.getDescription());
            pstmt.setString(3, treatment.getCategory());
            pstmt.setDouble(4, treatment.getCost());
            pstmt.setInt(5, treatment.getDuration());
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
     * Delete a treatment
     * @param treatmentId The treatment ID to delete
     * @return true if successful, false otherwise
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
     * Deactivate a treatment (soft delete)
     * @param treatmentId The treatment ID
     * @return true if successful, false otherwise
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
     * @param treatmentId The treatment ID
     * @return true if successful, false otherwise
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
     * @param treatmentName The treatment name to check
     * @return true if exists, false otherwise
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
     * Get treatment count
     * @return Total number of treatments
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
     * Get treatments with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of treatments
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
     * @return List of category stats
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
     * Map ResultSet to Treatment object
     * @param rs The ResultSet
     * @return Treatment object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        return new Treatment(
            rs.getInt("treatment_id"),
            rs.getString("treatment_name"),
            rs.getString("description"),
            rs.getString("category"),
            rs.getDouble("cost"),
            rs.getInt("estimated_duration"),
            rs.getBoolean("is_active"),
            rs.getString("created_at"),
            rs.getString("updated_at")
        );
    }
}
package dao;

import db.DBconnection;
import model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    /**
     * Add a new patient to the database
     * @param patient The patient object to save
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, "
                   + "phone, email, address, emergency_contact, emergency_phone, "
                   + "medical_history, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, patient.getDateOfBirth());
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getPhone());
            pstmt.setString(6, patient.getEmail());
            pstmt.setString(7, patient.getAddress());
            pstmt.setString(8, patient.getEmergencyContact());
            pstmt.setString(9, patient.getEmergencyPhone());
            pstmt.setString(10, patient.getMedicalHistory());
            pstmt.setString(11, patient.getAllergies());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    patient.setPatientId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get patient by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all patients
     * @return List of all patients
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY last_name, first_name";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Search patients by name
     * @param searchTerm The search term
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR email LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Update patient information
     * @param patient The patient to update
     * @return true if successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name=?, last_name=?, date_of_birth=?, gender=?, "
                   + "phone=?, email=?, address=?, emergency_contact=?, emergency_phone=?, "
                   + "medical_history=?, allergies=? WHERE patient_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, patient.getDateOfBirth());
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getPhone());
            pstmt.setString(6, patient.getEmail());
            pstmt.setString(7, patient.getAddress());
            pstmt.setString(8, patient.getEmergencyContact());
            pstmt.setString(9, patient.getEmergencyPhone());
            pstmt.setString(10, patient.getMedicalHistory());
            pstmt.setString(11, patient.getAllergies());
            pstmt.setInt(12, patient.getPatientId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a patient
     * @param patientId The patient ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if email already exists for another patient
     * @param email The email to check
     * @param excludePatientId Patient ID to exclude from check (for updates)
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email, int excludePatientId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE email = ? AND patient_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, excludePatientId);
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
     * Check if phone already exists for another patient
     * @param phone The phone to check
     * @param excludePatientId Patient ID to exclude from check (for updates)
     * @return true if phone exists, false otherwise
     */
    public boolean phoneExists(String phone, int excludePatientId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE phone = ? AND patient_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            pstmt.setInt(2, excludePatientId);
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

    /**
     * Map ResultSet to Patient object
     * @param rs The ResultSet
     * @return Patient object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt("patient_id"),
            rs.getInt("user_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getDate("date_of_birth"),
            rs.getString("gender"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address"),
            rs.getString("emergency_contact"),
            rs.getString("emergency_phone"),
            rs.getString("medical_history"),
            rs.getString("allergies"),
            rs.getString("created_at"),
            rs.getString("updated_at")
        );
    }
}
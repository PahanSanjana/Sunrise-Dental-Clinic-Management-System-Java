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
        String sql = "INSERT INTO patients (patient_name, gender, address, contact_number, email, "
                   + "date_of_birth, emergency_contact, emergency_phone, patient_login_id, "
                   + "medical_history, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getPatientName());
            pstmt.setString(2, patient.getGender());
            pstmt.setString(3, patient.getAddress());
            pstmt.setString(4, patient.getContactNumber());
            pstmt.setString(5, patient.getEmail());
            pstmt.setDate(6, patient.getDateOfBirth());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setString(8, patient.getEmergencyPhone());
            
            // Handle patient_login_id - if -1 or 0, set to NULL
            if (patient.getPatientLoginId() > 0) {
                pstmt.setInt(9, patient.getPatientLoginId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
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
     * Get patient by login ID (user ID)
     * @param loginId The user login ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientByLoginId(int loginId) {
        String sql = "SELECT * FROM patients WHERE patient_login_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loginId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient by login ID: " + e.getMessage());
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
        String sql = "SELECT * FROM patients ORDER BY patient_name";
        
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
     * Search patients by name or contact number
     * @param searchTerm The search term
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE patient_name LIKE ? OR contact_number LIKE ? OR email LIKE ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
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
        String sql = "UPDATE patients SET patient_name=?, gender=?, address=?, contact_number=?, email=?, "
                   + "date_of_birth=?, emergency_contact=?, emergency_phone=?, patient_login_id=?, "
                   + "medical_history=?, allergies=? WHERE patient_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getPatientName());
            pstmt.setString(2, patient.getGender());
            pstmt.setString(3, patient.getAddress());
            pstmt.setString(4, patient.getContactNumber());
            pstmt.setString(5, patient.getEmail());
            pstmt.setDate(6, patient.getDateOfBirth());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setString(8, patient.getEmergencyPhone());
            
            // Handle patient_login_id - if -1 or 0, set to NULL
            if (patient.getPatientLoginId() > 0) {
                pstmt.setInt(9, patient.getPatientLoginId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            
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
     * Get count of all patients
     * @return Total number of patients
     */
    public int getPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        
        try (Connection conn = DBconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting patients: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
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
     * Check if contact number already exists for another patient
     * @param contactNumber The contact number to check
     * @param excludePatientId Patient ID to exclude from check (for updates)
     * @return true if contact number exists, false otherwise
     */
    public boolean contactNumberExists(String contactNumber, int excludePatientId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE contact_number = ? AND patient_id != ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, contactNumber);
            pstmt.setInt(2, excludePatientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking contact number existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Link a patient to a user account
     * @param patientId The patient ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkPatientToUser(int patientId, int userId) {
        String sql = "UPDATE patients SET patient_login_id = ? WHERE patient_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error linking patient to user: " + e.getMessage());
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
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setPatientName(rs.getString("patient_name"));
        patient.setGender(rs.getString("gender"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        patient.setEmail(rs.getString("email"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        patient.setEmergencyPhone(rs.getString("emergency_phone"));
        
        // Handle NULL patient_login_id
        int loginId = rs.getInt("patient_login_id");
        if (rs.wasNull()) {
            patient.setPatientLoginId(-1); // -1 means NULL
        } else {
            patient.setPatientLoginId(loginId);
        }
        
        patient.setCreatedAt(rs.getString("created_at"));
        patient.setUpdatedAt(rs.getString("updated_at"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        patient.setAllergies(rs.getString("allergies"));
        return patient;
    }
    
    /**
 * Get recent patients (limited number)
 * @param limit Number of recent patients to get
 * @return List of recent patients
 */
public List<Patient> getRecentPatients(int limit) {
    List<Patient> patients = new ArrayList<>();
    String sql = "SELECT * FROM patients ORDER BY created_at DESC LIMIT ?";
    
    try (Connection conn = DBconnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, limit);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            patients.add(mapResultSetToPatient(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error getting recent patients: " + e.getMessage());
        e.printStackTrace();
    }
    return patients;
}

/**
 * Get patients with pagination
 * @param offset The offset (starting point)
 * @param limit The number of records to fetch
 * @return List of patients
 */
public List<Patient> getPatientsPaginated(int offset, int limit) {
    List<Patient> patients = new ArrayList<>();
    String sql = "SELECT * FROM patients ORDER BY patient_name LIMIT ? OFFSET ?";
    
    try (Connection conn = DBconnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, limit);
        pstmt.setInt(2, offset);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            patients.add(mapResultSetToPatient(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error getting patients with pagination: " + e.getMessage());
        e.printStackTrace();
    }
    return patients;
}
}
package dao;

import db.DBconnection;
import model.Patient;
import model.User;
import model.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    /**
     * Add a new patient to the database with validation
     * @param patient The patient object to save
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        // =============================================
        // VALIDATE BEFORE SAVING
        // =============================================
        
        // 1. Validate Name
        if (patient.getPatientName() == null || patient.getPatientName().trim().isEmpty()) {
            System.err.println("Validation Error: Patient Name is required");
            return false;
        }
        
        if (patient.getPatientName().trim().length() < 2) {
            System.err.println("Validation Error: Patient Name must be at least 2 characters");
            return false;
        }
        
        // 2. Validate Contact Number
        if (patient.getContactNumber() == null || patient.getContactNumber().trim().isEmpty()) {
            System.err.println("Validation Error: Contact Number is required");
            return false;
        }
        
        String phoneDigits = patient.getContactNumber().replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            System.err.println("Validation Error: Contact Number must have at least 10 digits");
            return false;
        }
        
        // 3. Validate Email (if provided)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            String email = patient.getEmail().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.err.println("Validation Error: Invalid email format");
                return false;
            }
        }
        
        // 4. Validate Date of Birth
        if (patient.getDateOfBirth() == null) {
            System.err.println("Validation Error: Date of Birth is required");
            return false;
        }
        
        // 5. Validate Gender
        if (patient.getGender() == null || patient.getGender().trim().isEmpty()) {
            System.err.println("Validation Error: Gender is required");
            return false;
        }
        
        // =============================================
        // CHECK FOR DUPLICATES
        // =============================================
        
        // Check if phone already exists
        if (contactNumberExists(patient.getContactNumber(), -1)) {
            System.err.println("Validation Error: Contact Number already exists");
            return false;
        }
        
        // Check if email already exists (if email is provided)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            if (emailExists(patient.getEmail(), -1)) {
                System.err.println("Validation Error: Email already exists");
                return false;
            }
        }
        
        // =============================================
        // SAVE TO DATABASE
        // =============================================
        
        String sql = "INSERT INTO patients (patient_name, gender, address, contact_number, email, "
                   + "date_of_birth, emergency_contact, emergency_phone, user_id, "
                   + "medical_history, allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, patient.getPatientName().trim());
            pstmt.setString(2, patient.getGender().trim());
            pstmt.setString(3, patient.getAddress());
            pstmt.setString(4, patient.getContactNumber().trim());
            pstmt.setString(5, patient.getEmail() != null ? patient.getEmail().trim() : null);
            pstmt.setDate(6, patient.getDateOfBirth());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setString(8, patient.getEmergencyPhone());
            
            // Handle user_id - if -1 or 0, set to NULL
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
     * Update patient information with validation
     * @param patient The patient to update
     * @return true if successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        // =============================================
        // VALIDATE BEFORE UPDATING
        // =============================================
        
        // 1. Validate Name
        if (patient.getPatientName() == null || patient.getPatientName().trim().isEmpty()) {
            System.err.println("Validation Error: Patient Name is required");
            return false;
        }
        
        if (patient.getPatientName().trim().length() < 2) {
            System.err.println("Validation Error: Patient Name must be at least 2 characters");
            return false;
        }
        
        // 2. Validate Contact Number
        if (patient.getContactNumber() == null || patient.getContactNumber().trim().isEmpty()) {
            System.err.println("Validation Error: Contact Number is required");
            return false;
        }
        
        String phoneDigits = patient.getContactNumber().replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            System.err.println("Validation Error: Contact Number must have at least 10 digits");
            return false;
        }
        
        // 3. Validate Email (if provided)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            String email = patient.getEmail().trim();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.err.println("Validation Error: Invalid email format");
                return false;
            }
        }
        
        // 4. Validate Date of Birth
        if (patient.getDateOfBirth() == null) {
            System.err.println("Validation Error: Date of Birth is required");
            return false;
        }
        
        // 5. Validate Gender
        if (patient.getGender() == null || patient.getGender().trim().isEmpty()) {
            System.err.println("Validation Error: Gender is required");
            return false;
        }
        
        // =============================================
        // CHECK FOR DUPLICATES (excluding this patient)
        // =============================================
        
        // Check if phone already exists for another patient
        if (contactNumberExists(patient.getContactNumber(), patient.getPatientId())) {
            System.err.println("Validation Error: Contact Number already exists for another patient");
            return false;
        }
        
        // Check if email already exists for another patient (if email is provided)
        if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
            if (emailExists(patient.getEmail(), patient.getPatientId())) {
                System.err.println("Validation Error: Email already exists for another patient");
                return false;
            }
        }
        
        // =============================================
        // UPDATE IN DATABASE
        // =============================================
        
        String sql = "UPDATE patients SET patient_name=?, gender=?, address=?, contact_number=?, email=?, "
                   + "date_of_birth=?, emergency_contact=?, emergency_phone=?, user_id=?, "
                   + "medical_history=?, allergies=? WHERE patient_id=?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getPatientName().trim());
            pstmt.setString(2, patient.getGender().trim());
            pstmt.setString(3, patient.getAddress());
            pstmt.setString(4, patient.getContactNumber().trim());
            pstmt.setString(5, patient.getEmail() != null ? patient.getEmail().trim() : null);
            pstmt.setDate(6, patient.getDateOfBirth());
            pstmt.setString(7, patient.getEmergencyContact());
            pstmt.setString(8, patient.getEmergencyPhone());
            
            // Handle user_id - if -1 or 0, set to NULL
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
     * Get patient by user ID (login ID)
     * @param userId The user ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientByUserId(int userId) {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all patients - ADMIN, RECEPTION, and DENTIST can see all patients
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
     * Get patients for a specific dentist (patients who have appointments with this dentist)
     * @param dentistId The dentist ID
     * @return List of patients
     */
    public List<Patient> getPatientsForDentist(int dentistId) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT DISTINCT p.* FROM patients p " +
                     "JOIN appointments a ON p.patient_id = a.patient_id " +
                     "WHERE a.dentist_id = ? " +
                     "ORDER BY p.patient_name";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting patients for dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Get patients based on user role
     * @param user The current logged-in user
     * @return List of patients filtered by role
     */
    public List<Patient> getPatientsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can see all patients
                return getAllPatients();
                
            case DENTIST:
                // Dentist can see patients from their appointments
                if (user.getDentistId() != null) {
                    return getPatientsForDentist(user.getDentistId());
                }
                return new ArrayList<>();
                
            case PATIENT:
                // Patient can only see themselves
                if (user.getPatientId() != null) {
                    Patient self = getPatientById(user.getPatientId());
                    List<Patient> result = new ArrayList<>();
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
     * Search patients by name or contact number with role-based filtering
     * @param searchTerm The search term
     * @param user The current user for role-based filtering
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Patient> allPatients = getPatientsForUser(user);
        if (allPatients == null || allPatients.isEmpty() || searchTerm == null || searchTerm.isEmpty()) {
            return allPatients;
        }
        
        String searchLower = searchTerm.toLowerCase().trim();
        List<Patient> filtered = new ArrayList<>();
        
        for (Patient p : allPatients) {
            if (p.getPatientName() != null && p.getPatientName().toLowerCase().contains(searchLower)) {
                filtered.add(p);
            } else if (p.getContactNumber() != null && p.getContactNumber().contains(searchTerm)) {
                filtered.add(p);
            } else if (p.getEmail() != null && p.getEmail().toLowerCase().contains(searchLower)) {
                filtered.add(p);
            }
        }
        
        return filtered;
    }

    /**
     * Search patients by name or contact number (original method - kept for compatibility)
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
     * Link a patient to a user account
     * @param patientId The patient ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkPatientToUser(int patientId, int userId) {
        String sql = "UPDATE patients SET user_id = ? WHERE patient_id = ?";
        
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
        
        // Handle NULL user_id
        int userId = rs.getInt("user_id");
        if (rs.wasNull()) {
            patient.setPatientLoginId(-1); // -1 means NULL
        } else {
            patient.setPatientLoginId(userId);
        }
        
        patient.setMedicalHistory(rs.getString("medical_history"));
        patient.setAllergies(rs.getString("allergies"));
        patient.setCreatedAt(rs.getString("created_at"));
        patient.setUpdatedAt(rs.getString("updated_at"));
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

    /**
     * Get patient count for a specific dentist
     * @param dentistId The dentist ID
     * @return Number of patients for the dentist
     */
    public int getPatientCountForDentist(int dentistId) {
        String sql = "SELECT COUNT(DISTINCT p.patient_id) FROM patients p " +
                     "JOIN appointments a ON p.patient_id = a.patient_id " +
                     "WHERE a.dentist_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dentistId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting patients for dentist: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get patient by ID with permission check
     * @param patientId The patient ID
     * @param user The current user
     * @return Patient object if authorized, null otherwise
     */
    public Patient getPatientByIdForUser(int patientId, User user) {
        if (user == null) {
            return null;
        }
        
        Patient patient = getPatientById(patientId);
        if (patient == null) {
            return null;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can view any patient
                return patient;
                
            case DENTIST:
                // Dentist can only view patients they have appointments with
                if (user.getDentistId() != null) {
                    String sql = "SELECT COUNT(*) FROM appointments " +
                                 "WHERE patient_id = ? AND dentist_id = ?";
                    
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        
                        pstmt.setInt(1, patientId);
                        pstmt.setInt(2, user.getDentistId());
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next() && rs.getInt(1) > 0) {
                            return patient;
                        }
                    } catch (SQLException e) {
                        System.err.println("Error checking patient access for dentist: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                return null;
                
            case PATIENT:
                // Patient can only view themselves
                if (user.getPatientId() != null && patientId == user.getPatientId()) {
                    return patient;
                }
                return null;
                
            default:
                return null;
        }
    }
}
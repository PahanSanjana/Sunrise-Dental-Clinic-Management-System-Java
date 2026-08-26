package controller;

import dao.PatientDAO;
import dao.UserDAO;
import model.Patient;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.AddPatientPanel;
import view.PatientDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientController {
    private AddPatientPanel addView;
    private PatientDetailsPanel detailsView;
    private PatientDAO patientDAO;
    private UserDAO userDAO;

    // =====================================================
    // CONSTRUCTOR FOR AddPatientPanel
    // =====================================================
    
    public PatientController(AddPatientPanel view) {
        this.addView = view;
        this.patientDAO = new PatientDAO();
        this.userDAO = new UserDAO();
        initAddController();
    }

    // =====================================================
    // CONSTRUCTOR FOR PatientDetailsPanel
    // =====================================================
    
    public PatientController(PatientDetailsPanel view) {
        this.detailsView = view;
        this.patientDAO = new PatientDAO();
        this.userDAO = new UserDAO();
    }

    // =====================================================
    // INIT ADD CONTROLLER
    // =====================================================

    private void initAddController() {
        // REMOVED: save listener is now handled directly in the view
        // The view's savePatient() method calls addPatient() directly
        addView.addClearListener(e -> addView.clearForm());
        addView.addCancelListener(e -> {
            Container parent = addView.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("PATIENT_LIST");
            }
        });
    }

    // =====================================================
    // CRUD METHODS
    // =====================================================

    /**
     * Add a new patient
     * @param patient The patient to add
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        return patientDAO.addPatient(patient);
    }

    /**
     * Get patient by ID
     */
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    /**
     * Update patient information
     */
    public boolean updatePatient(Patient patient) {
        return patientDAO.updatePatient(patient);
    }

    /**
     * Delete a patient
     */
    public boolean deletePatient(int patientId) {
        return patientDAO.deletePatient(patientId);
    }

    /**
     * Link patient to a user account
     */
    public boolean linkPatientToUser(int patientId, int userId) {
        return patientDAO.linkPatientToUser(patientId, userId);
    }

    /**
     * Create login account for a patient
     * @param patientId The patient ID
     * @param username The username
     * @param password The password
     * @return true if successful, false otherwise
     */
    public boolean createLoginForPatient(int patientId, String username, String password) {
        // Get patient details
        Patient patient = getPatientById(patientId);
        if (patient == null) {
            return false;
        }
        
        // Check if username exists
        if (userDAO.usernameExists(username)) {
            return false;
        }
        
        // Create user with PATIENT role
        int createdBy = LoginSession.getInstance().getCurrentUserId();
        User newUser = userDAO.createUserWithProfile(
            username, password, patient.getEmail(), UserRole.PATIENT, 
            createdBy, new HashMap<>()
        );
        
        if (newUser == null) {
            return false;
        }
        
        // Link patient to user
        return patientDAO.linkPatientToUser(patientId, newUser.getUserId());
    }

    /**
     * Navigate back to patient list
     */
    public void navigateBack() {
        if (detailsView != null) {
            Container parent = detailsView.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("PATIENT_LIST");
            }
        }
    }

    /**
     * Search patients by name or contact number
     */
    public List<Patient> searchPatients(String searchTerm) {
        return patientDAO.searchPatients(searchTerm);
    }

    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Get patient count
     */
    public int getPatientCount() {
        return patientDAO.getPatientCount();
    }

    /**
     * Get recent patients
     */
    public List<Patient> getRecentPatients(int limit) {
        return patientDAO.getRecentPatients(limit);
    }

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get patients based on user role
     * @param user The current logged-in user
     * @return List of patients filtered by role
     */
    public List<Patient> getPatientsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return patientDAO.getPatientsForUser(user);
    }

    /**
     * Get patients for the current logged-in user
     * @return List of patients filtered by current user's role
     */
    public List<Patient> getPatientsForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getPatientsForUser(currentUser);
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
        return patientDAO.getPatientByIdForUser(patientId, user);
    }

    /**
     * Get patient by ID for the current logged-in user
     * @param patientId The patient ID
     * @return Patient object if authorized, null otherwise
     */
    public Patient getPatientByIdForCurrentUser(int patientId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getPatientByIdForUser(patientId, currentUser);
    }

    /**
     * Search patients with role-based filtering
     * @param searchTerm The search term
     * @param user The current user
     * @return List of matching patients
     */
    public List<Patient> searchPatientsForUser(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return patientDAO.searchPatients(searchTerm, user);
    }

    /**
     * Search patients for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching patients
     */
    public List<Patient> searchPatientsForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchPatientsForUser(searchTerm, currentUser);
    }

    /**
     * Get patient count for a specific dentist
     * @param dentistId The dentist ID
     * @return Number of patients for the dentist
     */
    public int getPatientCountForDentist(int dentistId) {
        return patientDAO.getPatientCountForDentist(dentistId);
    }

    /**
     * Get patients with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of patients for the page
     */
    public List<Patient> getPatientsForUserPaginated(int page, int pageSize, User user) {
        List<Patient> allPatients = getPatientsForUser(user);
        if (allPatients == null || allPatients.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allPatients.size());
        
        if (start >= allPatients.size()) {
            return new ArrayList<>();
        }
        
        return allPatients.subList(start, end);
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view patients
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewPatients(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_PATIENTS");
    }

    /**
     * Check if user can add a patient
     * @param user The current user
     * @return true if can add, false otherwise
     */
    public boolean canAddPatient(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_PATIENTS");
    }

    /**
     * Check if user can edit a patient
     * @param patient The patient
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditPatient(Patient patient, User user) {
        if (user == null || patient == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
                return false;
                
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can delete a patient
     * @param patient The patient
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeletePatient(Patient patient, User user) {
        if (user == null || patient == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    // =====================================================
    // UPDATE METHODS WITH PERMISSION CHECK
    // =====================================================

    /**
     * Update patient with permission check
     * @param patient The patient to update
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updatePatientForUser(Patient patient, User user) {
        if (!canEditPatient(patient, user)) {
            return false;
        }
        return patientDAO.updatePatient(patient);
    }

    /**
     * Delete patient with permission check
     * @param patientId The patient ID to delete
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean deletePatientForUser(int patientId, User user) {
        Patient patient = getPatientById(patientId);
        if (patient == null) {
            return false;
        }
        
        if (!canDeletePatient(patient, user)) {
            return false;
        }
        
        return patientDAO.deletePatient(patientId);
    }

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get patient count for a user
     * @param user The current user
     * @return Total number of patients for the user
     */
    public int getPatientCountForUser(User user) {
        List<Patient> patients = getPatientsForUser(user);
        return patients != null ? patients.size() : 0;
    }

    /**
     * Get patient count for the current logged-in user
     * @return Total number of patients for the current user
     */
    public int getPatientCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getPatientCountForUser(currentUser);
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Validate patient data before saving
     * @param patient The patient to validate
     * @return Error message if invalid, null if valid
     */
    public String validatePatient(Patient patient) {
        if (patient == null) {
            return "Patient data is null.";
        }
        
        if (patient.getPatientName() == null || patient.getPatientName().isEmpty()) {
            return "Patient Name is required.";
        }
        
        if (patient.getPatientName().length() < 2) {
            return "Patient Name must be at least 2 characters.";
        }
        
        if (patient.getContactNumber() == null || patient.getContactNumber().isEmpty()) {
            return "Contact Number is required.";
        }
        
        String contactDigits = patient.getContactNumber().replaceAll("[^0-9]", "");
        if (contactDigits.length() < 10) {
            return "Please enter a valid contact number (at least 10 digits).";
        }
        
        if (patient.getDateOfBirth() == null) {
            return "Date of Birth is required.";
        }
        
        if (patient.getDateOfBirth().after(Date.valueOf(LocalDate.now()))) {
            return "Date of Birth cannot be in the future.";
        }
        
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            if (!patient.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Please enter a valid email address.";
            }
        }
        
        return null;
    }

    /**
     * Check if email already exists for another patient
     * @param email The email to check
     * @param excludePatientId Patient ID to exclude (for updates)
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email, int excludePatientId) {
        return patientDAO.emailExists(email, excludePatientId);
    }

    /**
     * Check if contact number already exists for another patient
     * @param contactNumber The contact number to check
     * @param excludePatientId Patient ID to exclude (for updates)
     * @return true if exists, false otherwise
     */
    public boolean contactNumberExists(String contactNumber, int excludePatientId) {
        return patientDAO.contactNumberExists(contactNumber, excludePatientId);
    }

    // =====================================================
    // HELPER METHODS FOR DETAILS VIEW
    // =====================================================

    /**
     * Load patient details with permission check
     * @param patientId The patient ID
     * @param user The current user
     */
    public void loadPatientDetailsForUser(int patientId, User user) {
        if (detailsView != null) {
            Patient patient = getPatientByIdForUser(patientId, user);
            if (patient != null) {
                detailsView.displayPatient(patient);
            } else {
                detailsView.showError("You don't have permission to view this patient.");
            }
        }
    }

    /**
     * Load patient details for the current user
     * @param patientId The patient ID
     */
    public void loadPatientDetailsForCurrentUser(int patientId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        loadPatientDetailsForUser(patientId, currentUser);
    }
}
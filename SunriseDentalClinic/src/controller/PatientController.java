package controller;

import dao.PatientDAO;
import model.Patient;
import view.AddPatientPanel;
import view.PatientDetailsPanel;
import view.PatientListPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientController {
    private AddPatientPanel addView;
    private PatientDetailsPanel detailsView;
    private PatientListPanel listView;
    private PatientDAO patientDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for AddPatientPanel
     */
    public PatientController(AddPatientPanel view) {
        this.addView = view;
        this.patientDAO = new PatientDAO();
        initAddController();
    }

    /**
     * Constructor for PatientDetailsPanel
     */
    public PatientController(PatientDetailsPanel view) {
        this.detailsView = view;
        this.patientDAO = new PatientDAO();
        initDetailsController();
    }

    /**
     * Constructor for PatientListPanel
     */
    public PatientController(PatientListPanel view) {
        this.listView = view;
        this.patientDAO = new PatientDAO();
    }

    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================

    private void initAddController() {
        addView.addSaveListener(e -> handleSavePatient());
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

    private void initDetailsController() {
        // No additional initialization needed for details view
        // The view handles its own button actions
    }

    // =====================================================
    // ADD PATIENT METHODS
    // =====================================================

    private void handleSavePatient() {
        // Get all form values
        String patientName = addView.getPatientName();
        String gender = addView.getGender();
        String address = addView.getAddress();
        String contactNumber = addView.getContactNumber();
        String email = addView.getEmail();
        String dob = addView.getDateOfBirth();
        String emergencyContact = addView.getEmergencyContact();
        String emergencyPhone = addView.getEmergencyPhone();
        String medicalHistory = addView.getMedicalHistory();
        String allergies = addView.getAllergies();

        // Validate required fields
        if (patientName.isEmpty()) {
            addView.showError("Patient Name is required.");
            return;
        }

        if (patientName.length() < 2) {
            addView.showError("Patient Name must be at least 2 characters.");
            return;
        }

        if (!patientName.matches("^[a-zA-Z\\s.]+$")) {
            addView.showError("Patient Name can only contain letters, spaces, and dots.");
            return;
        }

        // Validate Date of Birth
        if (dob.isEmpty()) {
            addView.showError("Date of Birth is required.");
            return;
        }

        Date dateOfBirth = null;
        try {
            LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateOfBirth = Date.valueOf(localDate);
            
            if (localDate.isAfter(LocalDate.now())) {
                addView.showError("Date of Birth cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            addView.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate contact number
        if (contactNumber.isEmpty()) {
            addView.showError("Contact Number is required.");
            return;
        }
        String contactDigits = contactNumber.replaceAll("[^0-9]", "");
        if (contactDigits.length() < 10) {
            addView.showError("Please enter a valid contact number (at least 10 digits).");
            return;
        }

        // Validate email (optional but if provided, validate format)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            addView.showError("Please enter a valid email address.");
            return;
        }

        // Create Patient object
        Patient patient = new Patient(
            patientName, gender, address, contactNumber,
            email, dateOfBirth, emergencyContact, emergencyPhone,
            -1, // patientLoginId - -1 means NULL
            medicalHistory, allergies
        );

        // Show loading message
        addView.showSuccess("Saving patient... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return patientDAO.addPatient(patient);
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        addView.showSuccess("Patient saved successfully! Patient ID: " + patient.getPatientId());
                        addView.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(2000, e -> {
                            Container parent = addView.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("PATIENT_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        addView.showError("Failed to save patient. Please try again.");
                    }
                } catch (Exception e) {
                    addView.showError("Error saving patient: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // =====================================================
    // PATIENT CRUD OPERATIONS
    // =====================================================

    /**
     * Get patient by ID
     * @param patientId The patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    /**
     * Get patient by login ID (user ID)
     * @param loginId The user login ID
     * @return Patient object if found, null otherwise
     */
 public Patient getPatientByUserId(int userId) {
    return patientDAO.getPatientByUserId(userId);  // ✅ FIXED
}

    /**
     * Get all patients
     * @return List of all patients
     */
    public java.util.List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Search patients
     * @param searchTerm The search term
     * @return List of matching patients
     */
    public java.util.List<Patient> searchPatients(String searchTerm) {
        return patientDAO.searchPatients(searchTerm);
    }

    /**
     * Add a new patient
     * @param patient The patient to add
     * @return true if successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        return patientDAO.addPatient(patient);
    }

    /**
     * Update patient information
     * @param patient The patient to update
     * @return true if successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        return patientDAO.updatePatient(patient);
    }

    /**
     * Delete a patient
     * @param patientId The patient ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePatient(int patientId) {
        return patientDAO.deletePatient(patientId);
    }

    /**
     * Get total patient count
     * @return Total number of patients
     */
    public int getPatientCount() {
        return patientDAO.getPatientCount();
    }

    /**
     * Link a patient to a user account
     * @param patientId The patient ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkPatientToUser(int patientId, int userId) {
        return patientDAO.linkPatientToUser(patientId, userId);
    }

    // =====================================================
    // LIST VIEW METHODS
    // =====================================================

    /**
     * Load patients for the list view
     * @param searchText The search text
     * @param filter The gender filter
     */
    public void loadPatients(String searchText, String filter) {
        if (listView != null) {
            // This is handled by PatientListController
            // But we keep this method for consistency
        }
    }

    /**
     * Refresh the patient list
     */
    public void refreshPatientList() {
        if (listView != null) {
            listView.loadPatients();
        }
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Validate patient data before saving
     * @param patient The patient to validate
     * @return Error message if invalid, null if valid
     */
    public String validatePatient(Patient patient) {
        if (patient.getPatientName() == null || patient.getPatientName().isEmpty()) {
            return "Patient Name is required.";
        }
        if (patient.getPatientName().length() < 2) {
            return "Patient Name must be at least 2 characters.";
        }
        if (!patient.getPatientName().matches("^[a-zA-Z\\s.]+$")) {
            return "Patient Name can only contain letters, spaces, and dots.";
        }
        if (patient.getContactNumber() == null || patient.getContactNumber().isEmpty()) {
            return "Contact Number is required.";
        }
        String contactDigits = patient.getContactNumber().replaceAll("[^0-9]", "");
        if (contactDigits.length() < 10) {
            return "Please enter a valid contact number (at least 10 digits).";
        }
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            if (!patient.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Please enter a valid email address.";
            }
        }
        if (patient.getDateOfBirth() == null) {
            return "Date of Birth is required.";
        }
        if (patient.getDateOfBirth().toLocalDate().isAfter(LocalDate.now())) {
            return "Date of Birth cannot be in the future.";
        }
        return null;
    }

    /**
     * Create a patient from form data
     * @param patientName Patient name
     * @param gender Gender
     * @param address Address
     * @param contactNumber Contact number
     * @param email Email
     * @param dob Date of birth string (YYYY-MM-DD)
     * @param emergencyContact Emergency contact name
     * @param emergencyPhone Emergency contact phone
     * @param medicalHistory Medical history
     * @param allergies Allergies
     * @return Patient object or null if error
     */
    public Patient createPatientFromData(String patientName, String gender, String address,
                                         String contactNumber, String email, String dob,
                                         String emergencyContact, String emergencyPhone,
                                         String medicalHistory, String allergies) {
        try {
            Date dateOfBirth = null;
            if (dob != null && !dob.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dateOfBirth = Date.valueOf(localDate);
            }
            
            return new Patient(
                patientName, gender, address, contactNumber,
                email, dateOfBirth, emergencyContact, emergencyPhone,
                -1, // patientLoginId - -1 means NULL
                medicalHistory, allergies
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
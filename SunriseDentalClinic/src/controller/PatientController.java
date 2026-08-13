package controller;

import dao.PatientDAO;
import dao.UserDAO;
import model.Patient;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import view.AddPatientPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PatientController {
    private AddPatientPanel addView;
    private PatientDAO patientDAO;
    private UserDAO userDAO;

    // Constructor for AddPatientPanel
    public PatientController(AddPatientPanel view) {
        this.addView = view;
        this.patientDAO = new PatientDAO();
        this.userDAO = new UserDAO();
        initAddController();
    }

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

    private void handleSavePatient() {
        // Get patient details
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

        // =============================================
        // CHECK IF LOGIN ACCOUNT IS REQUESTED
        // =============================================
        boolean createLogin = addView.isCreateLogin();
        String username = addView.getUsername();
        String password = addView.getPassword();
        String confirmPassword = addView.getConfirmPassword();

        if (createLogin) {
            // Validate login credentials
            if (username.isEmpty()) {
                addView.showError("Username is required for login account.");
                return;
            }
            if (username.length() < 3) {
                addView.showError("Username must be at least 3 characters.");
                return;
            }
            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                addView.showError("Username can only contain letters, numbers, and underscores.");
                return;
            }
            if (userDAO.usernameExists(username)) {
                addView.showError("Username already exists. Please choose another.");
                return;
            }
            
            if (password.isEmpty()) {
                addView.showError("Password is required for login account.");
                return;
            }
            if (password.length() < 6) {
                addView.showError("Password must be at least 6 characters.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                addView.showError("Passwords do not match.");
                return;
            }
        }

        // Create Patient object (without user_id first)
        Patient patient = new Patient(
            patientName, gender, address, contactNumber,
            email, dateOfBirth, emergencyContact, emergencyPhone,
            0, // temporary user_id (will be set later if login created)
            medicalHistory, allergies
        );

        addView.showSuccess("Saving patient... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // 1. Save patient
                boolean patientSaved = patientDAO.addPatient(patient);
                if (!patientSaved) {
                    return false;
                }
                
                // 2. If login required, create user and link
                if (createLogin) {
                    String salt = "salt_" + System.currentTimeMillis();
                    int createdBy = LoginSession.getInstance().getCurrentUserId();
                    
                    // Create user with PATIENT role
                    User newUser = userDAO.createUserWithProfile(
                        username, password, email, UserRole.PATIENT, 
                        createdBy, new HashMap<>()
                    );
                    
                    if (newUser == null) {
                        return false;
                    }
                    
                    // Link patient to user
                    return patientDAO.linkPatientToUser(patient.getPatientId(), newUser.getUserId());
                }
                
                return true;
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        String message = "Patient saved successfully! Patient ID: " + patient.getPatientId();
                        if (createLogin) {
                            message += "\nLogin account created for: " + username;
                        }
                        addView.showSuccess(message);
                        addView.clearForm();
                        
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

    // Other patient methods...
    public Patient getPatientById(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    public boolean updatePatient(Patient patient) {
        return patientDAO.updatePatient(patient);
    }

    public boolean deletePatient(int patientId) {
        return patientDAO.deletePatient(patientId);
    }
}
package controller;

import dao.UserDAO;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import view.Signup;
import view.Login;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SignupController {
    
    private Signup signupView;
    private UserDAO userDAO;

    public SignupController(Signup view) {
        this.signupView = view;
        this.userDAO = new UserDAO();
        initController();
    }

    private void initController() {
        signupView.addSignupListener(e -> handleSignup());
        signupView.addCancelListener(e -> {
            signupView.dispose();
            openLoginView();
        });
        signupView.addLoginLinkListener(e -> {
            signupView.dispose();
            openLoginView();
        });
    }

    private void handleSignup() {
        // Get login credentials
        String username = signupView.getUsername();
        String password = signupView.getPassword();
        String confirmPassword = signupView.getConfirmPassword();
        
        // Get patient details
        String fullName = signupView.getFullName();
        String gender = signupView.getGender();
        String dobStr = signupView.getDateOfBirth();
        String email = signupView.getEmail();
        String phone = signupView.getPhone();
        String address = signupView.getAddress();
        String emergencyContact = signupView.getEmergencyContact();
        String emergencyPhone = signupView.getEmergencyPhone();
        String medicalHistory = signupView.getMedicalHistory();
        String allergies = signupView.getAllergies();

        // Validate login credentials
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            signupView.showError("Username, Password, Full Name and Phone are required.");
            return;
        }

        if (username.length() < 3) {
            signupView.showError("Username must be at least 3 characters.");
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            signupView.showError("Username can only contain letters, numbers, and underscores.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            signupView.showError("Username already exists. Please choose another.");
            return;
        }

        if (password.length() < 6) {
            signupView.showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signupView.showError("Passwords do not match.");
            return;
        }

        // Validate email (optional)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            signupView.showError("Please enter a valid email address.");
            return;
        }

        // Validate phone
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            signupView.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // ✅ Validate Date of Birth
        Date dateOfBirth = null;
        if (dobStr.isEmpty()) {
            signupView.showError("Date of Birth is required.");
            return;
        }
        try {
            LocalDate localDate = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateOfBirth = Date.valueOf(localDate);
            
            if (localDate.isAfter(LocalDate.now())) {
                signupView.showError("Date of Birth cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            signupView.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        signupView.showSuccess("Creating account... Please wait.");
        signupView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // ✅ Use final variables for SwingWorker
        final String finalUsername = username;
        final String finalPassword = password;
        final String finalEmail = email;
        final String finalFullName = fullName;
        final String finalGender = gender;
        final String finalAddress = address;
        final String finalPhone = phone;
        final Date finalDateOfBirth = dateOfBirth;
        final String finalEmergencyContact = emergencyContact;
        final String finalEmergencyPhone = emergencyPhone;
        final String finalMedicalHistory = medicalHistory;
        final String finalAllergies = allergies;

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Prepare profile data for patient
                Map<String, Object> profileData = new HashMap<>();
                profileData.put("patientName", finalFullName);
                profileData.put("gender", finalGender);
                profileData.put("address", finalAddress);
                profileData.put("contactNumber", finalPhone);
                profileData.put("email", finalEmail);
                profileData.put("dateOfBirth", finalDateOfBirth);
                profileData.put("emergencyContact", finalEmergencyContact);
                profileData.put("emergencyPhone", finalEmergencyPhone);
                profileData.put("medicalHistory", finalMedicalHistory);
                profileData.put("allergies", finalAllergies);
                
                // Create user with PATIENT role
                // Use 0 for createdBy (will be set to NULL in database)
                return userDAO.createUserWithProfile(
                    finalUsername, finalPassword, finalEmail, UserRole.PATIENT, 
                    0, profileData
                );
            }

            @Override
            protected void done() {
                signupView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    User user = get();
                    if (user != null) {
                        signupView.showSuccess("Account created successfully!");
                        signupView.clearFields();
                        
                        // ✅ FIXED: Navigate to Login page instead of Dashboard
                        Timer timer = new Timer(1500, e -> {
                            signupView.dispose();
                            openLoginView();  // ✅ Opens Login page, NOT Dashboard
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        signupView.showError("Failed to create account. Please try again.");
                    }
                } catch (Exception e) {
                    signupView.showError("Error creating account: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // ✅ REMOVED: openMainApplication() - No longer needed

    private void openLoginView() {
        SwingUtilities.invokeLater(() -> {
            Login loginView = new Login();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}
package controller;

import dao.UserDAO;
import model.User;
import view.Signup;
import view.Login;

import javax.swing.*;

public class SignupController {
    private Signup signupView;
    private UserDAO userDAO;

    public SignupController(Signup signupView) {
        this.signupView = signupView;
        this.userDAO = new UserDAO();
        initController();
    }

    private void initController() {
        signupView.addSignupListener(e -> handleSignup());
        signupView.addCancelListener(e -> signupView.dispose());
        signupView.addLoginLinkListener(e -> {
            signupView.dispose();
            openLoginView();
        });
    }

    private void handleSignup() {
        String username = signupView.getUsername();
        String fullName = signupView.getFullName();
        String email = signupView.getEmail();
        String phone = signupView.getPhone();
        String password = signupView.getPassword();
        String confirmPassword = signupView.getConfirmPassword();

        // Validate all fields
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || 
            phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            signupView.showError("All fields are required.");
            return;
        }

        // Validate username
        if (username.length() < 3) {
            signupView.showError("Username must be at least 3 characters.");
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            signupView.showError("Username can only contain letters, numbers, and underscores.");
            return;
        }

        // Check if username exists
        if (userDAO.usernameExists(username)) {
            signupView.showError("Username already exists. Please choose another.");
            return;
        }

        // Validate full name
        if (fullName.length() < 2) {
            signupView.showError("Please enter your full name.");
            return;
        }

        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            signupView.showError("Full name can only contain letters and spaces.");
            return;
        }

        // Validate email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            signupView.showError("Please enter a valid email address.");
            return;
        }

        // Check if email exists
        if (userDAO.emailExists(email)) {
            signupView.showError("Email already registered. Please use another.");
            return;
        }

        // Validate phone
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            signupView.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate password
        if (password.length() < 6) {
            signupView.showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            signupView.showError("Password must contain at least one uppercase letter.");
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            signupView.showError("Password must contain at least one lowercase letter.");
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            signupView.showError("Password must contain at least one number.");
            return;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            signupView.showError("Passwords do not match.");
            return;
        }

        // Create the user (default role: PATIENT)
        // For demo, we're using plain text password (should be hashed in production)
        String salt = "dummy_salt_" + System.currentTimeMillis();
        
        // Show loading message
        signupView.showSuccess("Creating account... Please wait.");
        signupView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        // Use SwingWorker to perform signup in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.createPatientUser(username, password, salt, fullName, email, phone);
            }

            @Override
            protected void done() {
                signupView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    User newUser = get();
                    if (newUser == null) {
                        signupView.showError("Failed to create account. Please try again.");
                        return;
                    }

                    // Success
                    signupView.showSuccess("Account created successfully! You can now login.");
                    
                    // Close signup and open login after delay
                    Timer timer = new Timer(1500, e -> {
                        signupView.dispose();
                        openLoginView();
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } catch (Exception e) {
                    signupView.showError("Error creating account: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openLoginView() {
        SwingUtilities.invokeLater(() -> {
            Login loginView = new Login();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            Signup signupView = new Signup();
            new SignupController(signupView);
            signupView.setVisible(true);
        });
    }
}
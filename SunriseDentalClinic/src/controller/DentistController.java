package controller;

import dao.DentistDAO;
import dao.UserDAO;
import model.Dentist;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import view.AddDentistPanel;
import view.DentistListPanel;
import view.DentistDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DentistController {
    private AddDentistPanel addView;
    private DentistListPanel listView;
    private DentistDetailsPanel detailsView;
    private DentistDAO dentistDAO;
    private UserDAO userDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public DentistController(AddDentistPanel view) {
        this.addView = view;
        this.dentistDAO = new DentistDAO();
        this.userDAO = new UserDAO();
        initAddController();
    }

    public DentistController(DentistListPanel view) {
        this.listView = view;
        this.dentistDAO = new DentistDAO();
        this.userDAO = new UserDAO();
    }

    public DentistController(DentistDetailsPanel view) {
        this.detailsView = view;
        this.dentistDAO = new DentistDAO();
        this.userDAO = new UserDAO();
    }

    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================

    private void initAddController() {
        if (addView != null) {
            addView.addSaveListener(e -> handleSaveDentist());
            addView.addClearListener(e -> addView.clearForm());
            addView.addCancelListener(e -> {
                Container parent = addView.getParent();
                while (parent != null && !(parent instanceof MainFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof MainFrame) {
                    ((MainFrame) parent).showCard("DENTIST_LIST");
                }
            });
        }
    }

    // =====================================================
    // ADD DENTIST METHODS
    // =====================================================

    private void handleSaveDentist() {
        if (addView == null) return;
        
        // Get all form values
        String dentistName = addView.getDentistName();
        String specialization = addView.getSpecialization();
        String licenseNumber = addView.getLicenseNumber();
        String workingHours = addView.getWorkingHours();
        String phone = addView.getPhone();
        String email = addView.getEmail();
        String experienceStr = addView.getExperience();
        String feeStr = addView.getConsultationFee();
        boolean isAvailable = addView.isAvailable();

        // Check if login account is requested
        boolean createLogin = addView.isCreateLogin();
        String username = addView.getUsername();
        String password = addView.getPassword();
        String confirmPassword = addView.getConfirmPassword();

        // =============================================
        // VALIDATE REQUIRED FIELDS
        // =============================================

        // Validate Dentist Name
        if (dentistName.isEmpty()) {
            addView.showError("Dentist Name is required.");
            return;
        }

        if (dentistName.length() < 2) {
            addView.showError("Dentist Name must be at least 2 characters.");
            return;
        }

        if (!dentistName.matches("^[a-zA-Z\\s.]+$")) {
            addView.showError("Dentist Name can only contain letters, spaces, and dots.");
            return;
        }

        // Validate Specialization
        if (specialization.isEmpty()) {
            addView.showError("Specialization is required.");
            return;
        }

        // Validate License Number
        if (licenseNumber.isEmpty()) {
            addView.showError("License Number is required.");
            return;
        }

        if (dentistDAO.licenseNumberExists(licenseNumber)) {
            addView.showError("License Number already exists. Please enter a unique license number.");
            return;
        }

        // Validate Phone
        if (phone.isEmpty()) {
            addView.showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            addView.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate Email
        if (email.isEmpty()) {
            addView.showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            addView.showError("Please enter a valid email address.");
            return;
        }

        // Validate Years of Experience
        int yearsOfExperience = 0;
        if (experienceStr.isEmpty()) {
            addView.showError("Years of experience is required.");
            return;
        }
        try {
            yearsOfExperience = Integer.parseInt(experienceStr);
            if (yearsOfExperience < 0) {
                addView.showError("Years of experience cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for years of experience.");
            return;
        }

        // Validate Consultation Fee
        double consultationFee = 0;
        if (feeStr.isEmpty()) {
            addView.showError("Consultation fee is required.");
            return;
        }
        try {
            consultationFee = Double.parseDouble(feeStr);
            if (consultationFee < 0) {
                addView.showError("Consultation fee cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for consultation fee.");
            return;
        }

        // =============================================
        // VALIDATE LOGIN CREDENTIALS
        // =============================================
        if (createLogin) {
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

        // =============================================
        // CREATE DENTIST OBJECT
        // =============================================
        Dentist dentist = new Dentist(
            dentistName, specialization, licenseNumber,
            workingHours, phone, email, yearsOfExperience,
            consultationFee, isAvailable
        );

        // =============================================
        // SAVE WITH SWINGWORKER
        // =============================================
        addView.showSuccess("Saving dentist... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Store final values for use in SwingWorker
        final String finalDentistName = dentistName;
        final String finalSpecialization = specialization;
        final String finalLicenseNumber = licenseNumber;
        final String finalWorkingHours = workingHours;
        final String finalPhone = phone;
        final String finalEmail = email;
        final int finalYearsOfExperience = yearsOfExperience;
        final double finalConsultationFee = consultationFee;
        final boolean finalIsAvailable = isAvailable;

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // 1. Save dentist
                boolean dentistSaved = dentistDAO.addDentist(dentist);
                if (!dentistSaved) {
                    return false;
                }
                
                // 2. If login required, create user and link
                if (createLogin) {
                    int createdBy = LoginSession.getInstance().getCurrentUserId();
                    
                    // Prepare profile data for dentist - only need licenseNumber for UPDATE
                    Map<String, Object> profileData = new HashMap<>();
                    profileData.put("licenseNumber", finalLicenseNumber);  // ✅ Used to find the dentist
                    
                    // Create user with DENTIST role - this will UPDATE user_id in dentists table
                    User newUser = userDAO.createUserWithProfile(
                        username, password, email, UserRole.DENTIST,
                        createdBy, profileData
                    );
                    
                    if (newUser == null) {
                        return false;
                    }
                    
                    // ✅ No need to link separately - updateDentistProfile already did it
                    return true;
                }
                
                return true;
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        String message = "Dentist saved successfully! Dentist ID: " + dentist.getDentistId();
                        if (createLogin) {
                            message += "\nLogin account created for: " + username;
                            message += "\nRole: DENTIST";
                        }
                        addView.showSuccess(message);
                        addView.clearForm();
                        
                        Timer timer = new Timer(2000, e -> {
                            Container parent = addView.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("DENTIST_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        addView.showError("Failed to save dentist. Please try again.");
                    }
                } catch (Exception e) {
                    addView.showError("Error saving dentist: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    public Dentist getDentistById(int dentistId) {
        return dentistDAO.getDentistById(dentistId);
    }

    public Dentist getDentistByUserId(int userId) {
        return dentistDAO.getDentistByUserId(userId);
    }

    public Dentist getDentistByLicenseNumber(String licenseNumber) {
        return dentistDAO.getDentistByLicenseNumber(licenseNumber);
    }

    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    public List<Dentist> getAvailableDentists() {
        return dentistDAO.getAvailableDentists();
    }

    public List<Dentist> getDentistsBySpecialization(String specialization) {
        return dentistDAO.getDentistsBySpecialization(specialization);
    }

    public List<Dentist> searchDentists(String searchTerm) {
        return dentistDAO.searchDentists(searchTerm);
    }

    public List<Dentist> getDentistsPaginated(int offset, int limit) {
        return dentistDAO.getDentistsPaginated(offset, limit);
    }

    public List<Dentist> getRecentDentists(int limit) {
        return dentistDAO.getRecentDentists(limit);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    public boolean updateDentist(Dentist dentist) {
        return dentistDAO.updateDentist(dentist);
    }

    public boolean updateAvailability(int dentistId, boolean isAvailable) {
        return dentistDAO.updateAvailability(dentistId, isAvailable);
    }

    public boolean linkDentistToUser(int dentistId, int userId) {
        return dentistDAO.linkDentistToUser(dentistId, userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    public boolean deleteDentist(int dentistId) {
        return dentistDAO.deleteDentist(dentistId);
    }

    public boolean unlinkDentistFromUser(int dentistId) {
        return dentistDAO.unlinkDentistFromUser(dentistId);
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    public boolean licenseNumberExists(String licenseNumber) {
        return dentistDAO.licenseNumberExists(licenseNumber);
    }

    public boolean licenseNumberExists(String licenseNumber, int excludeDentistId) {
        return dentistDAO.licenseNumberExists(licenseNumber, excludeDentistId);
    }

    public boolean emailExists(String email, int excludeDentistId) {
        return dentistDAO.emailExists(email, excludeDentistId);
    }

    public boolean phoneExists(String phone, int excludeDentistId) {
        return dentistDAO.phoneExists(phone, excludeDentistId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    public int getDentistCount() {
        return dentistDAO.getDentistCount();
    }

    public int getAvailableDentistCount() {
        return dentistDAO.getAvailableDentistCount();
    }

    public int getDentistCountBySpecialization(String specialization) {
        return dentistDAO.getDentistCountBySpecialization(specialization);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    public void loadDentists(String searchText, String filter) {
        if (listView != null) {
            listView.loadDentists();
        }
    }

    public void refreshDentistList() {
        if (listView != null) {
            listView.loadDentists();
        }
    }

    public void navigateBack() {
        if (detailsView != null) {
            Container parent = detailsView.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("DENTIST_LIST");
            }
        }
    }
}
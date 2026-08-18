package controller;

import dao.DentistDAO;
import dao.UserDAO;
import model.Dentist;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.AddDentistPanel;
import view.DentistListPanel;
import view.DentistDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
                    profileData.put("licenseNumber", finalLicenseNumber);
                    
                    // Create user with DENTIST role - this will UPDATE user_id in dentists table
                    User newUser = userDAO.createUserWithProfile(
                        username, password, email, UserRole.DENTIST,
                        createdBy, profileData
                    );
                    
                    if (newUser == null) {
                        return false;
                    }
                    
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

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get dentists based on user role
     * @param user The current logged-in user
     * @return List of dentists filtered by role
     */
    public List<Dentist> getDentistsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return dentistDAO.getDentistsForUser(user);
    }

    /**
     * Get dentists for the current logged-in user
     * @return List of dentists filtered by current user's role
     */
    public List<Dentist> getDentistsForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getDentistsForUser(currentUser);
    }

    /**
     * Get dentist by ID with permission check
     * @param dentistId The dentist ID
     * @param user The current user
     * @return Dentist object if authorized, null otherwise
     */
    public Dentist getDentistByIdForUser(int dentistId, User user) {
        if (user == null) {
            return null;
        }
        return dentistDAO.getDentistByIdForUser(dentistId, user);
    }

    /**
     * Get dentist by ID for the current logged-in user
     * @param dentistId The dentist ID
     * @return Dentist object if authorized, null otherwise
     */
    public Dentist getDentistByIdForCurrentUser(int dentistId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getDentistByIdForUser(dentistId, currentUser);
    }

    /**
     * Search dentists with role-based filtering
     * @param searchTerm The search term
     * @param user The current user
     * @return List of matching dentists
     */
    public List<Dentist> searchDentistsForUser(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return dentistDAO.searchDentists(searchTerm, user);
    }

    /**
     * Search dentists for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching dentists
     */
    public List<Dentist> searchDentistsForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchDentistsForUser(searchTerm, currentUser);
    }

    /**
     * Get available dentists for a user (patients can see available dentists)
     * @param user The current user
     * @return List of available dentists
     */
    public List<Dentist> getAvailableDentistsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
            case RECEPTION:
                // Admin and Reception can see all dentists
                return dentistDAO.getAllDentists();
                
            case DENTIST:
                // Dentist can only see themselves
                if (user.getDentistId() != null && user.getDentistId() > 0) {
                    Dentist self = dentistDAO.getDentistById(user.getDentistId());
                    List<Dentist> result = new ArrayList<>();
                    if (self != null) {
                        result.add(self);
                    }
                    return result;
                }
                return new ArrayList<>();
                
            case PATIENT:
                // Patient can see all available dentists
                return dentistDAO.getAvailableDentists();
                
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Get dentists with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of dentists for the page
     */
    public List<Dentist> getDentistsForUserPaginated(int page, int pageSize, User user) {
        List<Dentist> allDentists = getDentistsForUser(user);
        if (allDentists == null || allDentists.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allDentists.size());
        
        if (start >= allDentists.size()) {
            return new ArrayList<>();
        }
        
        return allDentists.subList(start, end);
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view dentists
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewDentists(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_DENTISTS");
    }

    /**
     * Check if user can add a dentist
     * @param user The current user
     * @return true if can add, false otherwise
     */
    public boolean canAddDentist(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_DENTISTS");
    }

    /**
     * Check if user can edit a dentist
     * @param dentist The dentist
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditDentist(Dentist dentist, User user) {
        if (user == null || dentist == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return true;
                
            case DENTIST:
                // Dentist can only edit themselves
                return user.getDentistId() != null && dentist.getDentistId() == user.getDentistId();
                
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can delete a dentist
     * @param dentist The dentist
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteDentist(Dentist dentist, User user) {
        if (user == null || dentist == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return false;
                
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
     * Update dentist with permission check
     * @param dentist The dentist to update
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updateDentistForUser(Dentist dentist, User user) {
        if (!canEditDentist(dentist, user)) {
            return false;
        }
        return dentistDAO.updateDentist(dentist);
    }

    /**
     * Delete dentist with permission check
     * @param dentistId The dentist ID to delete
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean deleteDentistForUser(int dentistId, User user) {
        Dentist dentist = getDentistById(dentistId);
        if (dentist == null) {
            return false;
        }
        
        if (!canDeleteDentist(dentist, user)) {
            return false;
        }
        
        return dentistDAO.deleteDentist(dentistId);
    }

    /**
     * Update availability with permission check
     * @param dentistId The dentist ID
     * @param isAvailable The availability status
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updateAvailabilityForUser(int dentistId, boolean isAvailable, User user) {
        Dentist dentist = getDentistById(dentistId);
        if (dentist == null) {
            return false;
        }
        
        if (!canEditDentist(dentist, user)) {
            return false;
        }
        
        return dentistDAO.updateAvailability(dentistId, isAvailable);
    }

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get dentist count for a user
     * @param user The current user
     * @return Total number of dentists for the user
     */
    public int getDentistCountForUser(User user) {
        List<Dentist> dentists = getDentistsForUser(user);
        return dentists != null ? dentists.size() : 0;
    }

    /**
     * Get dentist count for the current logged-in user
     * @return Total number of dentists for the current user
     */
    public int getDentistCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getDentistCountForUser(currentUser);
    }

    /**
     * Get available dentist count for a user
     * @param user The current user
     * @return Number of available dentists
     */
    public int getAvailableDentistCountForUser(User user) {
        List<Dentist> dentists = getAvailableDentistsForUser(user);
        return dentists != null ? dentists.size() : 0;
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Validate dentist data before saving
     * @param dentist The dentist to validate
     * @return Error message if invalid, null if valid
     */
    public String validateDentist(Dentist dentist) {
        if (dentist == null) {
            return "Dentist data is null.";
        }
        
        if (dentist.getDentistName() == null || dentist.getDentistName().isEmpty()) {
            return "Dentist Name is required.";
        }
        
        if (dentist.getDentistName().length() < 2) {
            return "Dentist Name must be at least 2 characters.";
        }
        
        if (dentist.getSpecialization() == null || dentist.getSpecialization().isEmpty()) {
            return "Specialization is required.";
        }
        
        if (dentist.getLicenseNumber() == null || dentist.getLicenseNumber().isEmpty()) {
            return "License Number is required.";
        }
        
        if (dentist.getPhone() == null || dentist.getPhone().isEmpty()) {
            return "Phone number is required.";
        }
        
        String phoneDigits = dentist.getPhone().replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }
        
        if (dentist.getEmail() == null || dentist.getEmail().isEmpty()) {
            return "Email is required.";
        }
        
        if (!dentist.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Please enter a valid email address.";
        }
        
        if (dentist.getYearsOfExperience() < 0) {
            return "Years of experience cannot be negative.";
        }
        
        if (dentist.getConsultationFee() < 0) {
            return "Consultation fee cannot be negative.";
        }
        
        return null;
    }

    // =====================================================
    // HELPER METHODS FOR DETAILS VIEW
    // =====================================================

    /**
     * Load dentist details with permission check
     * @param dentistId The dentist ID
     * @param user The current user
     */
    public void loadDentistDetailsForUser(int dentistId, User user) {
        if (detailsView != null) {
            Dentist dentist = getDentistByIdForUser(dentistId, user);
            if (dentist != null) {
                detailsView.displayDentist(dentist);
            } else {
                detailsView.showError("You don't have permission to view this dentist.");
            }
        }
    }

    /**
     * Load dentist details for the current user
     * @param dentistId The dentist ID
     */
    public void loadDentistDetailsForCurrentUser(int dentistId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        loadDentistDetailsForUser(dentistId, currentUser);
    }

    // =====================================================
    // LOAD METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load dentists for the list view based on current user
     */
    public void loadDentistsForCurrentUser() {
        if (listView != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<Dentist> dentists = getDentistsForUser(currentUser);
            listView.displayDentists(dentists);
        }
    }
}
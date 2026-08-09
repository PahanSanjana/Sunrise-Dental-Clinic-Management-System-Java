package controller;

import dao.DentistDAO;
import model.Dentist;
import view.AddDentistPanel;
import view.DentistListPanel;
import view.DentistDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DentistController {
    private AddDentistPanel addView;
    private DentistListPanel listView;
    private DentistDetailsPanel detailsView;
    private DentistDAO dentistDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for AddDentistPanel
     * @param view The AddDentistPanel instance
     */
    public DentistController(AddDentistPanel view) {
        this.addView = view;
        this.dentistDAO = new DentistDAO();
        initAddController();
    }

    /**
     * Constructor for DentistListPanel
     * @param view The DentistListPanel instance
     */
    public DentistController(DentistListPanel view) {
        this.listView = view;
        this.dentistDAO = new DentistDAO();
    }

    /**
     * Constructor for DentistDetailsPanel
     * @param view The DentistDetailsPanel instance
     */
    public DentistController(DentistDetailsPanel view) {
        this.detailsView = view;
        this.dentistDAO = new DentistDAO();
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

        // Validate required fields
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

        // Validate specialization
        if (specialization.isEmpty()) {
            addView.showError("Specialization is required.");
            return;
        }

        // Validate license number
        if (licenseNumber.isEmpty()) {
            addView.showError("License Number is required.");
            return;
        }

        if (dentistDAO.licenseNumberExists(licenseNumber)) {
            addView.showError("License Number already exists. Please enter a unique license number.");
            return;
        }

        // Validate phone
        if (phone.isEmpty()) {
            addView.showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            addView.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            addView.showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            addView.showError("Please enter a valid email address.");
            return;
        }

        // Validate years of experience
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

        // Validate consultation fee
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

        // Create Dentist object
        Dentist dentist = new Dentist(
            dentistName, 
            specialization, 
            licenseNumber,
            workingHours, 
            phone, 
            email, 
            yearsOfExperience, 
            consultationFee, 
            isAvailable
        );

        // Show loading message
        addView.showSuccess("Saving dentist... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return dentistDAO.addDentist(dentist);
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        addView.showSuccess("Dentist saved successfully!");
                        addView.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
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

    /**
     * Get dentist by ID
     * @param dentistId The dentist ID
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistById(int dentistId) {
        return dentistDAO.getDentistById(dentistId);
    }

    /**
     * Get dentist by user ID
     * @param userId The user ID
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistByUserId(int userId) {
        return dentistDAO.getDentistByUserId(userId);
    }

    /**
     * Get dentist by license number
     * @param licenseNumber The license number
     * @return Dentist object if found, null otherwise
     */
    public Dentist getDentistByLicenseNumber(String licenseNumber) {
        return dentistDAO.getDentistByLicenseNumber(licenseNumber);
    }

    /**
     * Get all dentists
     * @return List of all dentists
     */
    public List<Dentist> getAllDentists() {
        return dentistDAO.getAllDentists();
    }

    /**
     * Get available dentists
     * @return List of available dentists
     */
    public List<Dentist> getAvailableDentists() {
        return dentistDAO.getAvailableDentists();
    }

    /**
     * Get dentists by specialization
     * @param specialization The specialization to filter by
     * @return List of dentists with the specified specialization
     */
    public List<Dentist> getDentistsBySpecialization(String specialization) {
        return dentistDAO.getDentistsBySpecialization(specialization);
    }

    /**
     * Search dentists by name or specialization
     * @param searchTerm The search term
     * @return List of matching dentists
     */
    public List<Dentist> searchDentists(String searchTerm) {
        return dentistDAO.searchDentists(searchTerm);
    }

    /**
     * Get dentists with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of dentists
     */
    public List<Dentist> getDentistsPaginated(int offset, int limit) {
        return dentistDAO.getDentistsPaginated(offset, limit);
    }

    /**
     * Get recent dentists
     * @param limit Number of recent dentists to get
     * @return List of recent dentists
     */
    public List<Dentist> getRecentDentists(int limit) {
        return dentistDAO.getRecentDentists(limit);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update dentist information
     * @param dentist The dentist to update
     * @return true if successful, false otherwise
     */
    public boolean updateDentist(Dentist dentist) {
        return dentistDAO.updateDentist(dentist);
    }

    /**
     * Update dentist availability
     * @param dentistId The dentist ID
     * @param isAvailable The availability status
     * @return true if successful, false otherwise
     */
    public boolean updateAvailability(int dentistId, boolean isAvailable) {
        return dentistDAO.updateAvailability(dentistId, isAvailable);
    }

    /**
     * Link a dentist to a user account
     * @param dentistId The dentist ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkDentistToUser(int dentistId, int userId) {
        return dentistDAO.linkDentistToUser(dentistId, userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a dentist
     * @param dentistId The dentist ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDentist(int dentistId) {
        return dentistDAO.deleteDentist(dentistId);
    }

    /**
     * Unlink a dentist from a user account
     * @param dentistId The dentist ID
     * @return true if successful, false otherwise
     */
    public boolean unlinkDentistFromUser(int dentistId) {
        return dentistDAO.unlinkDentistFromUser(dentistId);
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if license number exists
     * @param licenseNumber The license number to check
     * @return true if exists, false otherwise
     */
    public boolean licenseNumberExists(String licenseNumber) {
        return dentistDAO.licenseNumberExists(licenseNumber);
    }

    /**
     * Check if license number exists for another dentist
     * @param licenseNumber The license number to check
     * @param excludeDentistId Dentist ID to exclude from check
     * @return true if exists, false otherwise
     */
    public boolean licenseNumberExists(String licenseNumber, int excludeDentistId) {
        return dentistDAO.licenseNumberExists(licenseNumber, excludeDentistId);
    }

    /**
     * Check if email exists for another dentist
     * @param email The email to check
     * @param excludeDentistId Dentist ID to exclude from check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email, int excludeDentistId) {
        return dentistDAO.emailExists(email, excludeDentistId);
    }

    /**
     * Check if phone exists for another dentist
     * @param phone The phone to check
     * @param excludeDentistId Dentist ID to exclude from check
     * @return true if phone exists, false otherwise
     */
    public boolean phoneExists(String phone, int excludeDentistId) {
        return dentistDAO.phoneExists(phone, excludeDentistId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total dentist count
     * @return Total number of dentists
     */
    public int getDentistCount() {
        return dentistDAO.getDentistCount();
    }

    /**
     * Get available dentist count
     * @return Number of available dentists
     */
    public int getAvailableDentistCount() {
        return dentistDAO.getAvailableDentistCount();
    }

    /**
     * Get dentist count by specialization
     * @param specialization The specialization to count
     * @return Number of dentists with the specified specialization
     */
    public int getDentistCountBySpecialization(String specialization) {
        return dentistDAO.getDentistCountBySpecialization(specialization);
    }

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load dentists for the list view
     * @param searchText The search text
     * @param filter The filter (All, Available, Unavailable)
     */
    public void loadDentists(String searchText, String filter) {
        if (listView != null) {
            listView.loadDentists();
        }
    }

    /**
     * Refresh the dentist list
     */
    public void refreshDentistList() {
        if (listView != null) {
            listView.loadDentists();
        }
    }
}
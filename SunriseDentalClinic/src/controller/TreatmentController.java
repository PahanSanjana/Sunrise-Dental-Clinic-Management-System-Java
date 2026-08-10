package controller;

import dao.TreatmentDAO;
import model.Treatment;
import view.AddTreatmentPanel;
import view.TreatmentListPanel;
import view.TreatmentDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TreatmentController {
    private AddTreatmentPanel addView;
    private TreatmentListPanel listView;
    private TreatmentDetailsPanel detailsView;
    private TreatmentDAO treatmentDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for AddTreatmentPanel
     * @param view The AddTreatmentPanel instance
     */
    public TreatmentController(AddTreatmentPanel view) {
        this.addView = view;
        this.treatmentDAO = new TreatmentDAO();
        initAddController();
    }

    /**
     * Constructor for TreatmentListPanel
     * @param view The TreatmentListPanel instance
     */
    public TreatmentController(TreatmentListPanel view) {
        this.listView = view;
        this.treatmentDAO = new TreatmentDAO();
    }

    /**
     * Constructor for TreatmentDetailsPanel
     * @param view The TreatmentDetailsPanel instance
     */
    public TreatmentController(TreatmentDetailsPanel view) {
        this.detailsView = view;
        this.treatmentDAO = new TreatmentDAO();
    }

    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================

    private void initAddController() {
        if (addView != null) {
            addView.addSaveListener(e -> handleSaveTreatment());
            addView.addClearListener(e -> addView.clearForm());
            addView.addCancelListener(e -> {
                Container parent = addView.getParent();
                while (parent != null && !(parent instanceof MainFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof MainFrame) {
                    ((MainFrame) parent).showCard("TREATMENT_LIST");
                }
            });
        }
    }

    // =====================================================
    // ADD TREATMENT METHODS
    // =====================================================

    private void handleSaveTreatment() {
        if (addView == null) return;
        
        // Get all form values
        String treatmentName = addView.getTreatmentName();
        String description = addView.getDescription();
        String category = addView.getCategory();
        String costStr = addView.getCost();
        String durationStr = addView.getDuration();
        boolean isActive = addView.isActive();

        // Validate treatment name
        if (treatmentName.isEmpty()) {
            addView.showError("Treatment Name is required.");
            return;
        }
        if (treatmentName.length() < 2) {
            addView.showError("Treatment Name must be at least 2 characters.");
            return;
        }

        // Check if treatment name exists
        if (treatmentDAO.treatmentNameExists(treatmentName)) {
            addView.showError("Treatment name already exists. Please use a different name.");
            return;
        }

        // Validate description
        if (description.isEmpty()) {
            addView.showError("Description is required.");
            return;
        }

        // Validate category
        if (category == null || category.isEmpty()) {
            addView.showError("Category is required.");
            return;
        }

        // Validate cost
        double cost = 0;
        if (costStr.isEmpty()) {
            addView.showError("Cost is required.");
            return;
        }
        try {
            cost = Double.parseDouble(costStr);
            if (cost < 0) {
                addView.showError("Cost cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for cost.");
            return;
        }

        // Validate duration
        int duration = 0;
        if (durationStr.isEmpty()) {
            addView.showError("Duration is required.");
            return;
        }
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                addView.showError("Duration must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for duration.");
            return;
        }

        // Create Treatment object
        Treatment treatment = new Treatment(
            treatmentName, description, category,
            cost, duration, isActive
        );

        // Show loading message
        addView.showSuccess("Saving treatment... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return treatmentDAO.addTreatment(treatment);
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        addView.showSuccess("Treatment saved successfully!");
                        addView.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
                            Container parent = addView.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("TREATMENT_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        addView.showError("Failed to save treatment. Please try again.");
                    }
                } catch (Exception e) {
                    addView.showError("Error saving treatment: " + e.getMessage());
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
     * Get treatment by ID
     * @param treatmentId The treatment ID
     * @return Treatment object if found, null otherwise
     */
    public Treatment getTreatmentById(int treatmentId) {
        return treatmentDAO.getTreatmentById(treatmentId);
    }

    /**
     * Get treatment by name
     * @param treatmentName The treatment name
     * @return Treatment object if found, null otherwise
     */
    public Treatment getTreatmentByName(String treatmentName) {
        return treatmentDAO.getTreatmentByName(treatmentName);
    }

    /**
     * Get all treatments
     * @return List of all treatments
     */
    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAllTreatments();
    }

    /**
     * Get active treatments
     * @return List of active treatments
     */
    public List<Treatment> getActiveTreatments() {
        return treatmentDAO.getActiveTreatments();
    }

    /**
     * Get treatments by category
     * @param category The category to filter by
     * @return List of treatments in the specified category
     */
    public List<Treatment> getTreatmentsByCategory(String category) {
        return treatmentDAO.getTreatmentsByCategory(category);
    }

    /**
     * Search treatments by name or category
     * @param searchTerm The search term
     * @return List of matching treatments
     */
    public List<Treatment> searchTreatments(String searchTerm) {
        return treatmentDAO.searchTreatments(searchTerm);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update treatment information
     * @param treatment The treatment to update
     * @return true if successful, false otherwise
     */
    public boolean updateTreatment(Treatment treatment) {
        return treatmentDAO.updateTreatment(treatment);
    }

    /**
     * Deactivate a treatment (soft delete)
     * @param treatmentId The treatment ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateTreatment(int treatmentId) {
        return treatmentDAO.deactivateTreatment(treatmentId);
    }

    /**
     * Activate a treatment
     * @param treatmentId The treatment ID
     * @return true if successful, false otherwise
     */
    public boolean activateTreatment(int treatmentId) {
        return treatmentDAO.activateTreatment(treatmentId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a treatment
     * @param treatmentId The treatment ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteTreatment(int treatmentId) {
        return treatmentDAO.deleteTreatment(treatmentId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total treatment count
     * @return Total number of treatments
     */
    public int getTreatmentCount() {
        return treatmentDAO.getTreatmentCount();
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Check if treatment name exists
     * @param treatmentName The treatment name to check
     * @return true if exists, false otherwise
     */
    public boolean treatmentNameExists(String treatmentName) {
        return treatmentDAO.treatmentNameExists(treatmentName);
    }

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load treatments for the list view
     * @param searchText The search text
     * @param category The category filter
     * @param filter The status filter (All, Active, Inactive)
     */
    public void loadTreatments(String searchText, String category, String filter) {
        if (listView != null) {
            listView.loadTreatments();
        }
    }

    /**
     * Refresh the treatment list
     */
    public void refreshTreatmentList() {
        if (listView != null) {
            listView.loadTreatments();
        }
    }

    // =====================================================
    // VALIDATION METHODS FOR FORM
    // =====================================================

    /**
     * Validate treatment data before saving
     * @param treatment The treatment to validate
     * @return Error message if invalid, null if valid
     */
    public String validateTreatment(Treatment treatment) {
        if (treatment.getTreatmentName() == null || treatment.getTreatmentName().isEmpty()) {
            return "Treatment Name is required.";
        }
        if (treatment.getTreatmentName().length() < 2) {
            return "Treatment Name must be at least 2 characters.";
        }
        if (treatment.getDescription() == null || treatment.getDescription().isEmpty()) {
            return "Description is required.";
        }
        if (treatment.getCategory() == null || treatment.getCategory().isEmpty()) {
            return "Category is required.";
        }
        if (treatment.getCost() < 0) {
            return "Cost cannot be negative.";
        }
        if (treatment.getDuration() <= 0) {
            return "Duration must be greater than 0.";
        }
        return null;
    }
}
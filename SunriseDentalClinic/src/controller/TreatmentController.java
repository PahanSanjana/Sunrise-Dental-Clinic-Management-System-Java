package controller;

import dao.TreatmentDAO;
import model.Treatment;
import view.AddTreatmentPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TreatmentController {
    private AddTreatmentPanel view;
    private TreatmentDAO treatmentDAO;

    public TreatmentController(AddTreatmentPanel view) {
        this.view = view;
        this.treatmentDAO = new TreatmentDAO();
        initController();
    }

    private void initController() {
        view.addSaveListener(e -> handleSaveTreatment());
        view.addClearListener(e -> view.clearForm());
        view.addCancelListener(e -> {
            Container parent = view.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("TREATMENT_LIST");
            }
        });
    }

    private void handleSaveTreatment() {
        // Get all form values
        String treatmentName = view.getTreatmentName();
        String description = view.getDescription();
        String category = view.getCategory();
        String costStr = view.getCost();
        String durationStr = view.getDuration();
        boolean isActive = view.isActive();

        // Validate treatment name
        if (treatmentName.isEmpty()) {
            view.showError("Treatment Name is required.");
            return;
        }
        if (treatmentName.length() < 2) {
            view.showError("Treatment Name must be at least 2 characters.");
            return;
        }

        // Check if treatment name exists
        if (treatmentDAO.treatmentNameExists(treatmentName)) {
            view.showError("Treatment name already exists. Please use a different name.");
            return;
        }

        // Validate description
        if (description.isEmpty()) {
            view.showError("Description is required.");
            return;
        }

        // Validate category
        if (category.isEmpty()) {
            view.showError("Category is required.");
            return;
        }

        // Validate cost
        double cost = 0;
        if (costStr.isEmpty()) {
            view.showError("Cost is required.");
            return;
        }
        try {
            cost = Double.parseDouble(costStr);
            if (cost < 0) {
                view.showError("Cost cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number for cost.");
            return;
        }

        // Validate duration
        int duration = 0;
        if (durationStr.isEmpty()) {
            view.showError("Duration is required.");
            return;
        }
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                view.showError("Duration must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number for duration.");
            return;
        }

        // Create Treatment object
        Treatment treatment = new Treatment(
            treatmentName, description, category,
            cost, duration, isActive
        );

        // Show loading message
        view.showSuccess("Saving treatment... Please wait.");
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return treatmentDAO.addTreatment(treatment);
            }

            @Override
            protected void done() {
                view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        view.showSuccess("Treatment saved successfully!");
                        view.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
                            Container parent = view.getParent();
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
                        view.showError("Failed to save treatment. Please try again.");
                    }
                } catch (Exception e) {
                    view.showError("Error saving treatment: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    public Treatment getTreatmentById(int treatmentId) {
        return treatmentDAO.getTreatmentById(treatmentId);
    }

    public Treatment getTreatmentByName(String treatmentName) {
        return treatmentDAO.getTreatmentByName(treatmentName);
    }

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAllTreatments();
    }

    public List<Treatment> getActiveTreatments() {
        return treatmentDAO.getActiveTreatments();
    }

    public List<Treatment> getTreatmentsByCategory(String category) {
        return treatmentDAO.getTreatmentsByCategory(category);
    }

    public List<Treatment> searchTreatments(String searchTerm) {
        return treatmentDAO.searchTreatments(searchTerm);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    public boolean updateTreatment(Treatment treatment) {
        return treatmentDAO.updateTreatment(treatment);
    }

    public boolean deactivateTreatment(int treatmentId) {
        return treatmentDAO.deactivateTreatment(treatmentId);
    }

    public boolean activateTreatment(int treatmentId) {
        return treatmentDAO.activateTreatment(treatmentId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    public boolean deleteTreatment(int treatmentId) {
        return treatmentDAO.deleteTreatment(treatmentId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    public int getTreatmentCount() {
        return treatmentDAO.getTreatmentCount();
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    public boolean treatmentNameExists(String treatmentName) {
        return treatmentDAO.treatmentNameExists(treatmentName);
    }
}
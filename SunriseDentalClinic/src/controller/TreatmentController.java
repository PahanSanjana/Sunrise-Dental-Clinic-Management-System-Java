package controller;

import dao.TreatmentDAO;
import model.Treatment;
import model.User;
import model.User.UserRole;
import model.LoginSession;
import model.RolePermissions;
import view.AddTreatmentPanel;
import view.TreatmentListPanel;
import view.TreatmentDetailsPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

    // =====================================================
    // ROLE-BASED DATA ACCESS METHODS
    // =====================================================

    /**
     * Get treatments based on user role
     * @param user The current logged-in user
     * @return List of treatments filtered by role
     */
    public List<Treatment> getTreatmentsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return treatmentDAO.getTreatmentsForUser(user);
    }

    /**
     * Get treatments for the current logged-in user
     * @return List of treatments filtered by current user's role
     */
    public List<Treatment> getTreatmentsForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getTreatmentsForUser(currentUser);
    }

    /**
     * Get treatment by ID with permission check
     * @param treatmentId The treatment ID
     * @param user The current user
     * @return Treatment object if authorized, null otherwise
     */
    public Treatment getTreatmentByIdForUser(int treatmentId, User user) {
        if (user == null) {
            return null;
        }
        return treatmentDAO.getTreatmentByIdForUser(treatmentId, user);
    }

    /**
     * Get treatment by ID for the current logged-in user
     * @param treatmentId The treatment ID
     * @return Treatment object if authorized, null otherwise
     */
    public Treatment getTreatmentByIdForCurrentUser(int treatmentId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getTreatmentByIdForUser(treatmentId, currentUser);
    }

    /**
     * Search treatments with role-based filtering
     * @param searchTerm The search term
     * @param user The current user
     * @return List of matching treatments
     */
    public List<Treatment> searchTreatmentsForUser(String searchTerm, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return treatmentDAO.searchTreatments(searchTerm, user);
    }

    /**
     * Search treatments for the current logged-in user
     * @param searchTerm The search term
     * @return List of matching treatments
     */
    public List<Treatment> searchTreatmentsForCurrentUser(String searchTerm) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return searchTreatmentsForUser(searchTerm, currentUser);
    }

    /**
     * Get treatments with pagination and role-based filtering
     * @param page The page number (0-based)
     * @param pageSize The page size
     * @param user The current user
     * @return List of treatments for the page
     */
    public List<Treatment> getTreatmentsForUserPaginated(int page, int pageSize, User user) {
        List<Treatment> allTreatments = getTreatmentsForUser(user);
        if (allTreatments == null || allTreatments.isEmpty()) {
            return new ArrayList<>();
        }
        
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allTreatments.size());
        
        if (start >= allTreatments.size()) {
            return new ArrayList<>();
        }
        
        return allTreatments.subList(start, end);
    }

    /**
     * Get treatments by multiple categories with role-based filtering
     * @param categories List of categories
     * @param user The current user
     * @return List of treatments in the specified categories
     */
    public List<Treatment> getTreatmentsByCategoriesForUser(List<String> categories, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Treatment> allTreatments = getTreatmentsForUser(user);
        if (allTreatments == null || allTreatments.isEmpty() || categories == null || categories.isEmpty()) {
            return allTreatments;
        }
        
        List<Treatment> filtered = new ArrayList<>();
        for (Treatment treatment : allTreatments) {
            if (treatment.getCategory() != null && categories.contains(treatment.getCategory())) {
                filtered.add(treatment);
            }
        }
        return filtered;
    }

    /**
     * Get active treatments for a user
     * @param user The current user
     * @return List of active treatments
     */
    public List<Treatment> getActiveTreatmentsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Treatment> allTreatments = getTreatmentsForUser(user);
        if (allTreatments == null || allTreatments.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Treatment> active = new ArrayList<>();
        for (Treatment treatment : allTreatments) {
            if (treatment.isActive()) {
                active.add(treatment);
            }
        }
        return active;
    }

    // =====================================================
    // PERMISSION CHECK METHODS
    // =====================================================

    /**
     * Check if user can view treatments
     * @param user The current user
     * @return true if can view, false otherwise
     */
    public boolean canViewTreatments(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "VIEW_TREATMENTS");
    }

    /**
     * Check if user can add a treatment
     * @param user The current user
     * @return true if can add, false otherwise
     */
    public boolean canAddTreatment(User user) {
        if (user == null) {
            return false;
        }
        return RolePermissions.hasActionPermission(user.getRole(), "ADD_TREATMENTS");
    }

    /**
     * Check if user can edit a treatment
     * @param treatment The treatment
     * @param user The current user
     * @return true if can edit, false otherwise
     */
    public boolean canEditTreatment(Treatment treatment, User user) {
        if (user == null || treatment == null) {
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
     * Check if user can delete a treatment
     * @param treatment The treatment
     * @param user The current user
     * @return true if can delete, false otherwise
     */
    public boolean canDeleteTreatment(Treatment treatment, User user) {
        if (user == null || treatment == null) {
            return false;
        }
        
        UserRole role = user.getRole();
        
        switch (role) {
            case ADMIN:
                return true;
                
            case RECEPTION:
                return false;
                
            case DENTIST:
                return false;
                
            case PATIENT:
                return false;
                
            default:
                return false;
        }
    }

    /**
     * Check if user can toggle treatment status
     * @param treatment The treatment
     * @param user The current user
     * @return true if can toggle, false otherwise
     */
    public boolean canToggleTreatmentStatus(Treatment treatment, User user) {
        if (user == null || treatment == null) {
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

    // =====================================================
    // UPDATE METHODS WITH PERMISSION CHECK
    // =====================================================

    /**
     * Update treatment with permission check
     * @param treatment The treatment to update
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean updateTreatmentForUser(Treatment treatment, User user) {
        if (!canEditTreatment(treatment, user)) {
            return false;
        }
        return treatmentDAO.updateTreatment(treatment);
    }

    /**
     * Delete treatment with permission check
     * @param treatmentId The treatment ID to delete
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean deleteTreatmentForUser(int treatmentId, User user) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            return false;
        }
        
        if (!canDeleteTreatment(treatment, user)) {
            return false;
        }
        
        return treatmentDAO.deleteTreatment(treatmentId);
    }

    /**
     * Toggle treatment status with permission check
     * @param treatmentId The treatment ID
     * @param user The current user
     * @return true if successful, false otherwise
     */
    public boolean toggleTreatmentStatusForUser(int treatmentId, User user) {
        Treatment treatment = getTreatmentById(treatmentId);
        if (treatment == null) {
            return false;
        }
        
        if (!canToggleTreatmentStatus(treatment, user)) {
            return false;
        }
        
        boolean success;
        if (treatment.isActive()) {
            success = treatmentDAO.deactivateTreatment(treatmentId);
        } else {
            success = treatmentDAO.activateTreatment(treatmentId);
        }
        
        return success;
    }

    // =====================================================
    // COUNT METHODS WITH ROLE-BASED FILTERING
    // =====================================================

    /**
     * Get treatment count for a user
     * @param user The current user
     * @return Total number of treatments for the user
     */
    public int getTreatmentCountForUser(User user) {
        List<Treatment> treatments = getTreatmentsForUser(user);
        return treatments != null ? treatments.size() : 0;
    }

    /**
     * Get treatment count for the current logged-in user
     * @return Total number of treatments for the current user
     */
    public int getTreatmentCountForCurrentUser() {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        return getTreatmentCountForUser(currentUser);
    }

    /**
     * Get active treatment count for a user
     * @param user The current user
     * @return Number of active treatments
     */
    public int getActiveTreatmentCountForUser(User user) {
        List<Treatment> treatments = getActiveTreatmentsForUser(user);
        return treatments != null ? treatments.size() : 0;
    }

    /**
     * Get category stats for a user
     * @param user The current user
     * @return List of category stats [category, count]
     */
    public List<Object[]> getCategoryStatsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Treatment> treatments = getTreatmentsForUser(user);
        if (treatments == null || treatments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Count treatments by category
        java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
        for (Treatment treatment : treatments) {
            String category = treatment.getCategory();
            if (category != null) {
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
        }
        
        List<Object[]> stats = new ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            stats.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        
        // Sort by category name
        stats.sort((a, b) -> ((String) a[0]).compareTo((String) b[0]));
        
        return stats;
    }

    // =====================================================
    // HELPER METHODS FOR DETAILS VIEW
    // =====================================================

    /**
     * Load treatment details with permission check
     * @param treatmentId The treatment ID
     * @param user The current user
     */
    public void loadTreatmentDetailsForUser(int treatmentId, User user) {
        if (detailsView != null) {
            Treatment treatment = getTreatmentByIdForUser(treatmentId, user);
            if (treatment != null) {
                detailsView.displayTreatment(treatment);
            } else {
                detailsView.showError("You don't have permission to view this treatment.");
            }
        }
    }

    /**
     * Load treatment details for the current user
     * @param treatmentId The treatment ID
     */
    public void loadTreatmentDetailsForCurrentUser(int treatmentId) {
        User currentUser = LoginSession.getInstance().getCurrentUser();
        loadTreatmentDetailsForUser(treatmentId, currentUser);
    }

    // =====================================================
    // LOAD METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load treatments for the list view based on current user
     */
    public void loadTreatmentsForCurrentUser() {
        if (listView != null) {
            User currentUser = LoginSession.getInstance().getCurrentUser();
            List<Treatment> treatments = getTreatmentsForUser(currentUser);
            listView.displayTreatments(treatments);
        }
    }

    /**
     * Get filtered treatments with role-based access
     * @param searchText The search text
     * @param category The category filter
     * @param filter The status filter (All, Active, Inactive)
     * @param user The current user
     * @return List of filtered treatments
     */
    public List<Treatment> getFilteredTreatmentsForUser(String searchText, String category, String filter, User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Treatment> treatments = getTreatmentsForUser(user);
        if (treatments == null || treatments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Apply category filter
        if (category != null && !category.isEmpty() && !category.equals("All Categories")) {
            treatments.removeIf(t -> t.getCategory() == null || !t.getCategory().equals(category));
        }
        
        // Apply status filter
        if (filter != null && !filter.isEmpty() && !filter.equals("All")) {
            if ("Active".equals(filter)) {
                treatments.removeIf(t -> !t.isActive());
            } else if ("Inactive".equals(filter)) {
                treatments.removeIf(t -> t.isActive());
            }
        }
        
        // Apply search filter
        if (searchText != null && !searchText.isEmpty()) {
            String searchLower = searchText.toLowerCase().trim();
            treatments.removeIf(t -> {
                boolean nameMatch = t.getTreatmentName() != null && 
                                   t.getTreatmentName().toLowerCase().contains(searchLower);
                boolean categoryMatch = t.getCategory() != null && 
                                      t.getCategory().toLowerCase().contains(searchLower);
                boolean descMatch = t.getDescription() != null && 
                                  t.getDescription().toLowerCase().contains(searchLower);
                return !nameMatch && !categoryMatch && !descMatch;
            });
        }
        
        return treatments;
    }
}
package controller;

import dao.StaffDAO;
import model.Staff;
import view.AddStaffPanel;
import view.StaffListPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffController {
    private AddStaffPanel addView;
    private StaffListPanel listView;
    private StaffDAO staffDAO;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    /**
     * Constructor for AddStaffPanel
     * @param view The AddStaffPanel instance
     */
    public StaffController(AddStaffPanel view) {
        this.addView = view;
        this.staffDAO = new StaffDAO();
        initAddController();
    }

    /**
     * Constructor for StaffListPanel
     * @param view The StaffListPanel instance
     */
    public StaffController(StaffListPanel view) {
        this.listView = view;
        this.staffDAO = new StaffDAO();
        // No initialization needed for list view
    }

    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================

    private void initAddController() {
        if (addView != null) {
            addView.addSaveListener(e -> handleSaveStaff());
            addView.addClearListener(e -> addView.clearForm());
            addView.addCancelListener(e -> {
                Container parent = addView.getParent();
                while (parent != null && !(parent instanceof MainFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof MainFrame) {
                    ((MainFrame) parent).showCard("STAFF_LIST");
                }
            });
        }
    }

    // =====================================================
    // ADD STAFF METHODS
    // =====================================================

    private void handleSaveStaff() {
        if (addView == null) return;
        
        // Get all form values
        String firstName = addView.getFirstName();
        String lastName = addView.getLastName();
        String position = addView.getPosition();
        String department = addView.getDepartment();
        String phone = addView.getPhone();
        String email = addView.getEmail();
        String hireDateStr = addView.getHireDate();
        String salaryStr = addView.getSalary();
        boolean isActive = addView.isActive();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty()) {
            addView.showError("First Name and Last Name are required.");
            return;
        }

        if (firstName.length() < 2 || lastName.length() < 2) {
            addView.showError("Name must be at least 2 characters.");
            return;
        }

        if (!firstName.matches("^[a-zA-Z\\s]+$") || !lastName.matches("^[a-zA-Z\\s]+$")) {
            addView.showError("Name can only contain letters and spaces.");
            return;
        }

        // Validate position
        if (position.isEmpty()) {
            addView.showError("Position is required.");
            return;
        }

        // Validate department
        if (department.isEmpty()) {
            addView.showError("Department is required.");
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

        // Validate hire date
        if (hireDateStr.isEmpty()) {
            addView.showError("Hire Date is required.");
            return;
        }

        Date hireDate = null;
        try {
            LocalDate localDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            hireDate = Date.valueOf(localDate);
            
            if (localDate.isAfter(LocalDate.now())) {
                addView.showError("Hire Date cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            addView.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate salary
        double salary = 0;
        if (salaryStr.isEmpty()) {
            addView.showError("Salary is required.");
            return;
        }
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                addView.showError("Salary cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            addView.showError("Please enter a valid number for salary.");
            return;
        }

        // Create Staff object
        Staff staff = new Staff(
            firstName, lastName, position, department,
            phone, email, hireDate, salary, isActive
        );

        // Show loading message
        addView.showSuccess("Saving staff... Please wait.");
        addView.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return staffDAO.addStaff(staff);
            }

            @Override
            protected void done() {
                addView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        addView.showSuccess("Staff saved successfully!");
                        addView.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
                            Container parent = addView.getParent();
                            while (parent != null && !(parent instanceof MainFrame)) {
                                parent = parent.getParent();
                            }
                            if (parent instanceof MainFrame) {
                                ((MainFrame) parent).showCard("STAFF_LIST");
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        addView.showError("Failed to save staff. Please try again.");
                    }
                } catch (Exception e) {
                    addView.showError("Error saving staff: " + e.getMessage());
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
     * Get staff by ID
     * @param staffId The staff ID
     * @return Staff object if found, null otherwise
     */
    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    /**
     * Get staff by user ID
     * @param userId The user ID
     * @return Staff object if found, null otherwise
     */
    public Staff getStaffByUserId(int userId) {
        return staffDAO.getStaffByUserId(userId);
    }

    /**
     * Get all staff members
     * @return List of all staff
     */
    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    /**
     * Get staff by position
     * @param position The position to filter by
     * @return List of staff with the specified position
     */
    public List<Staff> getStaffByPosition(String position) {
        return staffDAO.getStaffByPosition(position);
    }

    /**
     * Get staff by department
     * @param department The department to filter by
     * @return List of staff in the specified department
     */
    public List<Staff> getStaffByDepartment(String department) {
        return staffDAO.getStaffByDepartment(department);
    }

    /**
     * Get active staff members
     * @return List of active staff
     */
    public List<Staff> getActiveStaff() {
        return staffDAO.getActiveStaff();
    }

    /**
     * Search staff by name, position, department, or phone
     * @param searchTerm The search term
     * @return List of matching staff
     */
    public List<Staff> searchStaff(String searchTerm) {
        return staffDAO.searchStaff(searchTerm);
    }

    /**
     * Get staff with pagination
     * @param offset The offset (starting point)
     * @param limit The number of records to fetch
     * @return List of staff
     */
    public List<Staff> getStaffPaginated(int offset, int limit) {
        // This would need to be implemented in StaffDAO
        // For now, return all staff
        return staffDAO.getAllStaff();
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    /**
     * Update staff information
     * @param staff The staff to update
     * @return true if successful, false otherwise
     */
    public boolean updateStaff(Staff staff) {
        return staffDAO.updateStaff(staff);
    }

    /**
     * Deactivate a staff member (soft delete)
     * @param staffId The staff ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateStaff(int staffId) {
        return staffDAO.deactivateStaff(staffId);
    }

    /**
     * Activate a staff member
     * @param staffId The staff ID
     * @return true if successful, false otherwise
     */
    public boolean activateStaff(int staffId) {
        return staffDAO.activateStaff(staffId);
    }

    /**
     * Link staff to a user account
     * @param staffId The staff ID
     * @param userId The user ID to link
     * @return true if successful, false otherwise
     */
    public boolean linkStaffToUser(int staffId, int userId) {
        return staffDAO.linkStaffToUser(staffId, userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    /**
     * Delete a staff member
     * @param staffId The staff ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    /**
     * Get total staff count
     * @return Total number of staff
     */
    public int getStaffCount() {
        return staffDAO.getStaffCount();
    }

    /**
     * Get active staff count
     * @return Number of active staff
     */
    public int getActiveStaffCount() {
        return staffDAO.getActiveStaffCount();
    }

    // =====================================================
    // HELPER METHODS FOR LIST VIEW
    // =====================================================

    /**
     * Load staff for the list view
     * @param searchText The search text
     * @param filter The status filter (All, Active, Inactive)
     * @param department The department filter
     */
    public void loadStaff(String searchText, String filter, String department) {
        if (listView != null) {
            listView.loadStaff();
        }
    }

    /**
     * Refresh the staff list
     */
    public void refreshStaffList() {
        if (listView != null) {
            listView.loadStaff();
        }
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    /**
     * Validate staff data before saving
     * @param staff The staff to validate
     * @return Error message if invalid, null if valid
     */
    public String validateStaff(Staff staff) {
        if (staff.getFirstName() == null || staff.getFirstName().isEmpty()) {
            return "First Name is required.";
        }
        if (staff.getFirstName().length() < 2) {
            return "First Name must be at least 2 characters.";
        }
        if (staff.getLastName() == null || staff.getLastName().isEmpty()) {
            return "Last Name is required.";
        }
        if (staff.getLastName().length() < 2) {
            return "Last Name must be at least 2 characters.";
        }
        if (staff.getPosition() == null || staff.getPosition().isEmpty()) {
            return "Position is required.";
        }
        if (staff.getDepartment() == null || staff.getDepartment().isEmpty()) {
            return "Department is required.";
        }
        if (staff.getPhone() == null || staff.getPhone().isEmpty()) {
            return "Phone number is required.";
        }
        String phoneDigits = staff.getPhone().replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            return "Please enter a valid phone number (at least 10 digits).";
        }
        if (staff.getEmail() == null || staff.getEmail().isEmpty()) {
            return "Email is required.";
        }
        if (!staff.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Please enter a valid email address.";
        }
        if (staff.getHireDate() == null) {
            return "Hire Date is required.";
        }
        if (staff.getHireDate().toLocalDate().isAfter(LocalDate.now())) {
            return "Hire Date cannot be in the future.";
        }
        if (staff.getSalary() < 0) {
            return "Salary cannot be negative.";
        }
        return null;
    }
}
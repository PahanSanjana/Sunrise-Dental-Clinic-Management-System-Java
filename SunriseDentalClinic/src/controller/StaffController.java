package controller;

import dao.StaffDAO;
import model.Staff;
import view.AddStaffPanel;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffController {
    private AddStaffPanel view;
    private StaffDAO staffDAO;

    public StaffController(AddStaffPanel view) {
        this.view = view;
        this.staffDAO = new StaffDAO();
        initController();
    }

    private void initController() {
        view.addSaveListener(e -> handleSaveStaff());
        view.addClearListener(e -> view.clearForm());
        view.addCancelListener(e -> {
            Container parent = view.getParent();
            while (parent != null && !(parent instanceof MainFrame)) {
                parent = parent.getParent();
            }
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).showCard("STAFF_LIST");
            }
        });
    }

    private void handleSaveStaff() {
        // Get all form values
        String firstName = view.getFirstName();
        String lastName = view.getLastName();
        String position = view.getPosition();
        String department = view.getDepartment();
        String phone = view.getPhone();
        String email = view.getEmail();
        String hireDateStr = view.getHireDate();
        String salaryStr = view.getSalary();
        boolean isActive = view.isActive();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty()) {
            view.showError("First Name and Last Name are required.");
            return;
        }

        if (firstName.length() < 2 || lastName.length() < 2) {
            view.showError("Name must be at least 2 characters.");
            return;
        }

        if (!firstName.matches("^[a-zA-Z\\s]+$") || !lastName.matches("^[a-zA-Z\\s]+$")) {
            view.showError("Name can only contain letters and spaces.");
            return;
        }

        // Validate position
        if (position.isEmpty()) {
            view.showError("Position is required.");
            return;
        }

        // Validate department
        if (department.isEmpty()) {
            view.showError("Department is required.");
            return;
        }

        // Validate phone
        if (phone.isEmpty()) {
            view.showError("Phone number is required.");
            return;
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (phoneDigits.length() < 10) {
            view.showError("Please enter a valid phone number (at least 10 digits).");
            return;
        }

        // Validate email
        if (email.isEmpty()) {
            view.showError("Email is required.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            view.showError("Please enter a valid email address.");
            return;
        }

        // Validate hire date
        if (hireDateStr.isEmpty()) {
            view.showError("Hire Date is required.");
            return;
        }

        Date hireDate = null;
        try {
            LocalDate localDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            hireDate = Date.valueOf(localDate);
            
            if (localDate.isAfter(LocalDate.now())) {
                view.showError("Hire Date cannot be in the future.");
                return;
            }
        } catch (Exception e) {
            view.showError("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Validate salary
        double salary = 0;
        if (salaryStr.isEmpty()) {
            view.showError("Salary is required.");
            return;
        }
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                view.showError("Salary cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Please enter a valid number for salary.");
            return;
        }

        // Create Staff object
        Staff staff = new Staff(
            firstName, lastName, position, department,
            phone, email, hireDate, salary, isActive
        );

        // Show loading message
        view.showSuccess("Saving staff... Please wait.");
        view.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // Use SwingWorker to save in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return staffDAO.addStaff(staff);
            }

            @Override
            protected void done() {
                view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                try {
                    boolean success = get();
                    if (success) {
                        view.showSuccess("Staff saved successfully!");
                        view.clearForm();
                        
                        // Show success and navigate back after delay
                        Timer timer = new Timer(1500, e -> {
                            Container parent = view.getParent();
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
                        view.showError("Failed to save staff. Please try again.");
                    }
                } catch (Exception e) {
                    view.showError("Error saving staff: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // =====================================================
    // READ METHODS
    // =====================================================

    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    public Staff getStaffByUserId(int userId) {
        return staffDAO.getStaffByUserId(userId);
    }

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    public List<Staff> getStaffByPosition(String position) {
        return staffDAO.getStaffByPosition(position);
    }

    public List<Staff> getStaffByDepartment(String department) {
        return staffDAO.getStaffByDepartment(department);
    }

    public List<Staff> getActiveStaff() {
        return staffDAO.getActiveStaff();
    }

    public List<Staff> searchStaff(String searchTerm) {
        return staffDAO.searchStaff(searchTerm);
    }

    // =====================================================
    // UPDATE METHODS
    // =====================================================

    public boolean updateStaff(Staff staff) {
        return staffDAO.updateStaff(staff);
    }

    public boolean deactivateStaff(int staffId) {
        return staffDAO.deactivateStaff(staffId);
    }

    public boolean activateStaff(int staffId) {
        return staffDAO.activateStaff(staffId);
    }

    public boolean linkStaffToUser(int staffId, int userId) {
        return staffDAO.linkStaffToUser(staffId, userId);
    }

    // =====================================================
    // DELETE METHODS
    // =====================================================

    public boolean deleteStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    // =====================================================
    // COUNT METHODS
    // =====================================================

    public int getStaffCount() {
        return staffDAO.getStaffCount();
    }

    public int getActiveStaffCount() {
        return staffDAO.getActiveStaffCount();
    }
}